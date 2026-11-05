#!/usr/bin/env python3
"""Builds the datapack + resource pack version of Endrite into datapack/.

Datapacks cannot add blocks or items, so the datapack version maps Endrite onto vanilla:
- Endrite ore   -> crying obsidian generated in End stone; in the End it drops Raw Endrite.
- Tools/armor   -> netherite items carrying the Endrite components (stats taken from the mod,
                   see tools/datapack_item_components.json).
- Raw/ingot     -> operator-only items (jigsaw / structure block) that survival players cannot
                   obtain otherwise, so the recipes cannot be fed with ordinary vanilla items.
- Double jump   -> a boots enchantment that triggers a small upward wind burst on jump input.
The resource pack supplies textures, models, armor layers and translations.

Usage: python3 tools/make_datapack.py <vanilla-extract-dir>
<vanilla-extract-dir> holds the unzipped Minecraft 1.21.11 client jar (assets/ and data/),
used to copy the vanilla End biomes and vanilla item model definitions.
"""
import copy
import json
import os
import shutil
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MOD_RES = os.path.join(ROOT, "src/main/resources")
VANILLA = sys.argv[1]
OUT = os.path.join(ROOT, "datapack")
DP = os.path.join(OUT, "Endrite-Datapack")
RP = os.path.join(OUT, "Endrite-Resourcepack")
NS = "endrite"

DATA_FORMAT = 94
RESOURCE_FORMAT = 75

COMPONENTS = json.load(open(os.path.join(ROOT, "tools/datapack_item_components.json")))
TOOLS = ["sword", "pickaxe", "axe", "shovel", "hoe", "spear"]
ARMOR = ["helmet", "chestplate", "leggings", "boots"]
INGOT_BASE = "minecraft:structure_block"
RAW_BASE = "minecraft:jigsaw"
CMD = {"strings": ["endrite"]}

EN = json.load(open(os.path.join(MOD_RES, f"assets/{NS}/lang/en_us.json")))
DE = json.load(open(os.path.join(MOD_RES, f"assets/{NS}/lang/de_de.json")))


def w(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        if isinstance(obj, str):
            f.write(obj)
        else:
            json.dump(obj, f, indent=2, ensure_ascii=False)
            f.write("\n")


def name(key):
    return {"translate": f"item.{NS}.{key}", "fallback": EN[f"item.{NS}.{key}"]}


def item_stack(kind):
    """Returns (item id, components) of an Endrite item."""
    if kind == "raw_endrite":
        return RAW_BASE, {
            "minecraft:item_name": name("raw_endrite"),
            "minecraft:item_model": "minecraft:raw_gold",
            "minecraft:custom_model_data": CMD,
            "minecraft:rarity": "common",
            "minecraft:custom_data": {NS: "raw_endrite"},
        }
    if kind == "endrite_ingot":
        return INGOT_BASE, {
            "minecraft:item_name": name("endrite_ingot"),
            "minecraft:item_model": "minecraft:netherite_ingot",
            "minecraft:custom_model_data": CMD,
            "minecraft:rarity": "common",
            "minecraft:damage_resistant": {"types": "#minecraft:is_fire"},
            "minecraft:custom_data": {NS: "endrite_ingot"},
        }
    entry = COMPONENTS[kind]
    comps = copy.deepcopy(entry["components"])
    for rule in comps.get("minecraft:tool", {}).get("rules", []):
        if rule["blocks"] == f"#{NS}:incorrect_for_endrite_tool":
            rule["blocks"] = "#minecraft:incorrect_for_netherite_tool"
    comps.update({
        "minecraft:item_name": name(f"endrite_{kind}"),
        "minecraft:repairable": {"items": INGOT_BASE},
        "minecraft:custom_model_data": CMD,
        "minecraft:custom_data": {NS: kind},
    })
    if kind == "boots":
        comps["minecraft:enchantments"] = {f"{NS}:double_jump": 1}
    return entry["base"], comps


ALL_ITEMS = ["raw_endrite", "endrite_ingot"] + TOOLS + ARMOR


def item_key(kind):
    return kind if kind in ("raw_endrite", "endrite_ingot") else f"endrite_{kind}"


# ---------------------------------------------------------------- datapack
def build_datapack():
    shutil.rmtree(DP, ignore_errors=True)
    w(f"{DP}/pack.mcmeta", {"pack": {
        "description": "Endrite (Datapack) – End ore, tools and armor above netherite",
        "min_format": DATA_FORMAT, "max_format": DATA_FORMAT}})
    D = f"{DP}/data/{NS}"
    M = f"{DP}/data/minecraft"

    # One loot table per item: used by the ore drop and by /function endrite:give
    for kind in ALL_ITEMS:
        iid, comps = item_stack(kind)
        w(f"{D}/loot_table/item/{item_key(kind)}.json", {"type": "minecraft:command", "pools": [{
            "rolls": 1, "entries": [{"type": "minecraft:item", "name": iid,
                                     "functions": [{"function": "minecraft:set_components", "components": comps}]}]}]})

    # Crying obsidian: Raw Endrite in the End (fortune applies), vanilla behaviour everywhere else
    w(f"{M}/loot_table/blocks/crying_obsidian.json", {"type": "minecraft:block", "random_sequence": "minecraft:blocks/crying_obsidian", "pools": [{
        "rolls": 1.0, "bonus_rolls": 0.0,
        "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:loot_table", "value": f"{NS}:item/raw_endrite",
             "conditions": [
                 {"condition": "minecraft:location_check", "predicate": {"dimension": "minecraft:the_end"}},
                 {"condition": "minecraft:inverted", "term": {"condition": "minecraft:match_tool", "predicate": {
                     "predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}}],
             "functions": [
                 {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                 {"function": "minecraft:explosion_decay"}]},
            {"type": "minecraft:item", "name": "minecraft:crying_obsidian",
             "conditions": [{"condition": "minecraft:survives_explosion"}]}]}]}]})
    w(f"{M}/tags/block/wither_immune.json", {"replace": False, "values": ["minecraft:crying_obsidian"]})

    # Recipes: same patterns as the mod, ingredients are the Endrite base items
    def result(kind, count=1):
        iid, comps = item_stack(kind)
        return {"id": iid, "count": count, "components": comps}
    patterns = {
        "sword": ["X", "X", "#"], "pickaxe": ["XXX", " # ", " # "], "axe": ["XX", "X#", " #"],
        "shovel": ["X", "#", "#"], "hoe": ["XX", " #", " #"], "spear": ["  X", " # ", "#  "],
        "helmet": ["XXX", "X X"], "chestplate": ["X X", "XXX", "XXX"], "leggings": ["XXX", "X X", "X X"], "boots": ["X X", "X X"],
    }
    recipes = []
    for kind, pattern in patterns.items():
        key = {"X": INGOT_BASE}
        if kind in TOOLS:
            key["#"] = "minecraft:stick"
        w(f"{D}/recipe/endrite_{kind}.json", {"type": "minecraft:crafting_shaped", "category": "equipment",
                                              "key": key, "pattern": pattern, "result": result(kind)})
        recipes.append(f"{NS}:endrite_{kind}")
    for kind, time in [("smelting", 200), ("blasting", 100)]:
        w(f"{D}/recipe/endrite_ingot_from_{kind}.json", {"type": f"minecraft:{kind}", "category": "misc", "cookingtime": time,
                                                        "experience": 2.0, "ingredient": RAW_BASE, "result": result("endrite_ingot")})
        recipes.append(f"{NS}:endrite_ingot_from_{kind}")
    # Hidden advancement that puts the recipes into the recipe book once Endrite is picked up
    w(f"{D}/advancement/unlock_recipes.json", {
        "criteria": {"has_endrite": {"trigger": "minecraft:inventory_changed", "conditions": {"items": [
            {"items": [RAW_BASE, INGOT_BASE], "predicates": {"minecraft:custom_data": {NS: "raw_endrite"}}},
        ]}}, "has_ingot": {"trigger": "minecraft:inventory_changed", "conditions": {"items": [
            {"items": INGOT_BASE, "predicates": {"minecraft:custom_data": {NS: "endrite_ingot"}}}]}}},
        "requirements": [["has_endrite", "has_ingot"]],
        "rewards": {"recipes": recipes}})

    # World generation: crying obsidian veins in End stone, End biomes only
    w(f"{D}/worldgen/configured_feature/ore_endrite.json", {"type": "minecraft:ore", "config": {
        "size": 3, "discard_chance_on_air_exposure": 0.5,
        "targets": [{"target": {"predicate_type": "minecraft:block_match", "block": "minecraft:end_stone"},
                     "state": {"Name": "minecraft:crying_obsidian"}}]}})
    mod_placed = json.load(open(os.path.join(MOD_RES, f"data/{NS}/worldgen/placed_feature/ore_endrite.json")))
    mod_placed["feature"] = f"{NS}:ore_endrite"
    w(f"{D}/worldgen/placed_feature/ore_endrite.json", mod_placed)
    for biome in ["the_end", "end_highlands", "end_midlands", "small_end_islands", "end_barrens"]:
        b = json.load(open(os.path.join(VANILLA, f"data/minecraft/worldgen/biome/{biome}.json")))
        features = b["features"]
        while len(features) < 7:
            features.append([])
        features[6].append(f"{NS}:ore_endrite")  # step 6 = underground_ores
        w(f"{M}/worldgen/biome/{biome}.json", b)

    # Double jump: boots enchantment, fired by jump input in mid-air while wearing the full set
    def piece(slot, kind):
        return {"items": COMPONENTS[kind]["base"], "predicates": {"minecraft:custom_data": {NS: kind}}}
    full_set = {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"equipment": {
        "head": piece("head", "helmet"), "chest": piece("chest", "chestplate"),
        "legs": piece("legs", "leggings"), "feet": piece("feet", "boots")}}}
    jump_pressed = {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {
        "type_specific": {"type": "minecraft:player", "input": {"jump": True}}}}
    grounded = {"condition": "minecraft:any_of", "terms": [
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"flags": {"is_on_ground": True}}},
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"flags": {"is_in_water": True}}},
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"flags": {"is_flying": True}}},
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"vehicle": {}}},
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"location": {"block": {"blocks": "#minecraft:climbable"}}}},
    ]}
    w(f"{D}/predicate/jump_pressed.json", jump_pressed)
    w(f"{D}/predicate/grounded.json", grounded)
    # Enchantment conditions may not reference predicate files, so they are inlined there
    w(f"{D}/enchantment/double_jump.json", {
        "description": {"translate": f"enchantment.{NS}.double_jump", "fallback": "Double Jump"},
        "supported_items": "minecraft:netherite_boots",
        "weight": 1, "max_level": 1,
        "min_cost": {"base": 100, "per_level_above_first": 0}, "max_cost": {"base": 100, "per_level_above_first": 0},
        "anvil_cost": 1, "slots": ["feet"],
        "effects": {"minecraft:tick": [{
            "requirements": {"condition": "minecraft:all_of", "terms": [
                {"condition": "minecraft:entity_scores", "entity": "this", "scores": {"endrite.dj": 1}},
                jump_pressed,
                {"condition": "minecraft:inverted", "term": grounded},
                full_set]},
            "effect": {"type": "minecraft:all_of", "effects": [
                {"type": "minecraft:explode", "block_interaction": "none", "radius": 1.5, "offset": [0, -0.5, 0],
                 "knockback_multiplier": 0.73,
                 "small_particle": {"type": "minecraft:reverse_portal"}, "large_particle": {"type": "minecraft:reverse_portal"},
                 "sound": "minecraft:entity.enderman.teleport"},
                {"type": "minecraft:run_function", "function": f"{NS}:double_jump/used"}]}}]}})
    w(f"{D}/item_modifier/add_double_jump.json", {"function": "minecraft:set_enchantments", "enchantments": {f"{NS}:double_jump": 1}})

    w(f"{M}/tags/function/load.json", {"values": [f"{NS}:load"]})
    w(f"{M}/tags/function/tick.json", {"values": [f"{NS}:tick"]})
    w(f"{D}/function/load.mcfunction", "scoreboard objectives add endrite.dj dummy\n")
    w(f"{D}/function/tick.mcfunction", "\n".join([
        "# Double jump state: 0 = on the ground, 1 = ready (jump key released in mid-air), 2 = used",
        "scoreboard players add @a endrite.dj 0",
        f"execute as @a[scores={{endrite.dj=1..}}] if predicate {NS}:grounded run scoreboard players set @s endrite.dj 0",
        f"execute as @a[scores={{endrite.dj=0}}] unless predicate {NS}:grounded unless predicate {NS}:jump_pressed run scoreboard players set @s endrite.dj 1",
        "# Endrite boots always carry the double jump enchantment (a grindstone would remove it)",
        f"execute as @a if items entity @s armor.feet *[minecraft:custom_data~{{{NS}:\"boots\"}}] unless items entity @s armor.feet *[minecraft:enchantments~[{{enchantments:\"{NS}:double_jump\"}}]] run item modify entity @s armor.feet {NS}:add_double_jump",
        ""]))
    w(f"{D}/function/double_jump/used.mcfunction", "scoreboard players set @s endrite.dj 2\n")
    w(f"{D}/function/give.mcfunction", "".join(f"loot give @s loot {NS}:item/{item_key(k)}\n" for k in ALL_ITEMS))


# ----------------------------------------------------------- resource pack
def build_resourcepack():
    shutil.rmtree(RP, ignore_errors=True)
    w(f"{RP}/pack.mcmeta", {"pack": {
        "description": "Endrite (Resource Pack) – textures for the Endrite datapack",
        "min_format": RESOURCE_FORMAT, "max_format": RESOURCE_FORMAT}})
    shutil.copy(os.path.join(MOD_RES, f"assets/{NS}/icon.png"), f"{RP}/pack.png")
    shutil.copy(os.path.join(MOD_RES, f"assets/{NS}/icon.png"), f"{DP}/pack.png")
    for sub in ["textures/item", "textures/entity", "models/item", "equipment"]:
        shutil.copytree(os.path.join(MOD_RES, f"assets/{NS}/{sub}"), f"{RP}/assets/{NS}/{sub}")

    # Vanilla item definitions get a custom_model_data switch: Endrite model, otherwise vanilla
    overrides = {f"endrite_{k}": COMPONENTS[k]["base"].split(":")[1] for k in TOOLS + ARMOR}
    overrides.update({"raw_endrite": "raw_gold", "endrite_ingot": "netherite_ingot"})
    for endrite_item, vanilla_item in overrides.items():
        vanilla_def = json.load(open(os.path.join(VANILLA, f"assets/minecraft/items/{vanilla_item}.json")))
        endrite_def = json.load(open(os.path.join(MOD_RES, f"assets/{NS}/items/{endrite_item}.json")))
        merged = dict(vanilla_def)
        merged["model"] = {"type": "minecraft:select", "property": "minecraft:custom_model_data",
                           "cases": [{"when": "endrite", "model": endrite_def["model"]}],
                           "fallback": vanilla_def["model"]}
        w(f"{RP}/assets/minecraft/items/{vanilla_item}.json", merged)

    for lang, data in [("en_us", EN), ("de_de", DE)]:
        tr = {k: v for k, v in data.items() if k.startswith("item.")}
        tr[f"enchantment.{NS}.double_jump"] = "Double Jump" if lang == "en_us" else "Doppelsprung"
        w(f"{RP}/assets/{NS}/lang/{lang}.json", tr)


def zip_dir(src, dst):
    with zipfile.ZipFile(dst, "w", zipfile.ZIP_DEFLATED) as z:
        for base, _, files in os.walk(src):
            for f in sorted(files):
                p = os.path.join(base, f)
                info = zipfile.ZipInfo(os.path.relpath(p, src), date_time=(2026, 1, 1, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                z.writestr(info, open(p, "rb").read())


if __name__ == "__main__":
    build_datapack()
    build_resourcepack()
    os.makedirs(os.path.join(OUT, "dist"), exist_ok=True)
    zip_dir(DP, os.path.join(OUT, "dist", "Endrite-Datapack.zip"))
    zip_dir(RP, os.path.join(OUT, "dist", "Endrite-Resourcepack.zip"))
    print("built", OUT)
