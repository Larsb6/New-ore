# Double jump state: 0 = on the ground, 1 = ready (jump key released in mid-air), 2 = used
scoreboard players add @a endrite.dj 0
execute as @a[scores={endrite.dj=1..}] if predicate endrite:grounded run scoreboard players set @s endrite.dj 0
execute as @a[scores={endrite.dj=0}] unless predicate endrite:grounded unless predicate endrite:jump_pressed run scoreboard players set @s endrite.dj 1
# Endrite boots always carry the double jump enchantment (a grindstone would remove it)
execute as @a if items entity @s armor.feet *[minecraft:custom_data~{endrite:"boots"}] unless items entity @s armor.feet *[minecraft:enchantments~[{enchantments:"endrite:double_jump"}]] run item modify entity @s armor.feet endrite:add_double_jump
