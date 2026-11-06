# Endrite

Fabric-Mod für **Minecraft 1.21.11**: fügt **Endrit** hinzu – ein neues Erz, das **ausschließlich im End** vorkommt – samt Werkzeugen, Waffen und einem Rüstungsset, die stärker sind als Netherit. Die Texturen sind deutlich heller als Netherit (helles, silbriges Violett).

## Inhalt

| Typ | Objekte |
| --- | --- |
| Blöcke | Endriterz, Rohendritblock, Endritblock (Beacon-Basis) |
| Materialien | Rohendrit, Endritbarren (Beacon-Bezahlung) |
| Werkzeuge | Spitzhacke, Axt, Schaufel, Hacke |
| Waffen | Schwert, Axt, Speer |
| Rüstung | Helm, Harnisch, Beinschutz, Stiefel (mit Rüstungsbesatz-Support) |

Es gibt **keine Schmiedevorlage** – alles wird direkt an der Werkbank aus Endritbarren hergestellt (gleiche Rezeptmuster wie Diamant).

## Gewinnung

- Endriterz ersetzt **nur Endstein** und wird nur in End-Biomen platziert (Hauptinsel und äußere Inseln, Y 0–100).
- Benötigt mindestens eine **Diamant-Spitzhacke**. Droppt Rohendrit (Glück wirkt), mit Behutsamkeit das Erz selbst.
- Rohendrit oder Erz im Ofen / Schmelzofen → Endritbarren.
- Das Erz ist **explosionssicher** (Explosionsresistenz 1200 wie Antiker Schrott – TNT, Creeper und Endkristalle zerstören es nicht) und kann weder vom **Enderdrachen** noch vom **Wither** zerstört werden. Das gilt auch für Endritblock und Rohendritblock.

## Werte

Endrit liegt eine Stufe über Netherit, mit dem gleichen Abstand wie Diamant → Netherit.

| Werkzeugmaterial | Diamant | Netherit | **Endrit** |
| --- | --- | --- | --- |
| Haltbarkeit | 1561 | 2031 | **2501** |
| Abbaugeschwindigkeit | 8 | 9 | **10** |
| Schwert-Schaden | 7 | 8 | **9** |
| Axt-Schaden | 9 | 10 | **11** |
| Verzauberbarkeit | 10 | 15 | **15** |

Der Speer setzt die Diamant→Netherit-Progression der Ladeattacke ebenfalls um eine Stufe fort.

| Rüstung | Netherit | **Endrit** |
| --- | --- | --- |
| Haltbarkeits-Multiplikator | 37 | **42** |
| Rüstungshärte (pro Teil) | 3,0 | **4,0** |
| Rüstungspunkte (Helm/Brust/Beine/Füße) | 3/8/6/3 | 3/8/6/3 |
| Rückstoßresistenz (pro Teil) | 0,1 | 0,1 |

Wie Netherit sind Endrit-Ausrüstung, Barren und Block feuerfest.

## Set-Bonus: Doppelsprung

Wer **alle vier Endrit-Rüstungsteile** trägt, kann in der Luft ein zweites Mal springen (Sprungtaste in der Luft erneut drücken). Der Doppelsprung lädt sich bei jeder Landung wieder auf und funktioniert nicht beim Fliegen, Gleiten mit Elytren, Schwimmen, Klettern oder Reiten. Die Fallhöhe wird beim zweiten Sprung zurückgesetzt, Fallschaden zählt also erst ab dort.

Die Mod muss dafür auf **Client und Server** installiert sein.

## Server-Version (Vanilla-Clients)

`server/` baut eine **rein serverseitige** Variante: Sie kommt auf einen Fabric-Server, und Spieler können mit einem **normalen Vanilla-Minecraft 1.21.11** beitreten – ohne Fabric, ohne Mod.

- Inhalt und Werte sind identisch mit der Mod (gleicher Code, gleiche Block-/Item-IDs): eigenes Endriterz, Endrit-/Rohendritblock, alle Werkzeuge, Waffen, Rüstung, Doppelsprung.
- Technisch läuft das über [Polymer](https://github.com/Patbox/polymer) (ist in der jar enthalten): Clients sehen Vanilla-Items/-Blöcke, die über ein **automatisch gesendetes Resource Pack** wie Endrit aussehen.
- Installation: `endrite-server-1.0.0.jar` + **Fabric API** in den `mods`-Ordner des Fabric-Servers. Beim ersten Start wird `config/polymer/auto-host.json` angelegt, sodass der Server das Resource Pack selbst über seinen normalen Port ausliefert und **verlangt** (sonst sähen Endrit-Blöcke wie Notenblöcke aus). Wer das nicht will, setzt dort `"require": false`.
- Nur für dedizierte Server. Im Einzelspieler die normale Mod benutzen; Server-Variante und normale Mod nicht gleichzeitig installieren.
- Der Doppelsprung wird komplett vom Server gesteuert (er liest die Sprungtaste aus den Eingabe-Paketen des Clients). Bei hohem Ping reagiert er entsprechend verzögert.
- Die Stärke des Doppelsprungs steht in `config/endrite.json` (`double_jump_strength`, Standard `1.75`). Das ist ein Faktor auf die normale Sprungkraft: `1.0` = +1,25 Blöcke (wie die Mod), `1.5` = +2,6, `1.75` = +3,4, `2.0` = +4,3. Nach dem Ändern den Server neu starten.

Bauen: `./gradlew -p server build` → `server/build/libs/endrite-server-1.0.0.jar`.

## Datapack-Version

Unter `datapack/dist/` gibt es Endrit zusätzlich als **Datapack + Resource Pack** (ohne Fabric). Datapacks können keine neuen Blöcke oder Items hinzufügen, deshalb baut diese Version auf Vanilla-Objekten auf:

| | Mod | Datapack |
| --- | --- | --- |
| Erz | eigener Block „Endriterz“ | **Weinender Obsidian**, der im End in Endstein generiert (gleiche Häufigkeit). Im End droppt er Rohendrit (Glück wirkt), mit Behutsamkeit oder außerhalb des Ends normalen weinenden Obsidian |
| Werkzeuge, Waffen, Rüstung | eigene Items | Netherit-Items mit den exakten Endrit-Werten (Haltbarkeit, Schaden, Abbaugeschwindigkeit, Rüstungshärte …) |
| Rohendrit / Barren | eigene Items | Spezial-Items, die sich nur über das Erz bzw. den Ofen bekommen lassen |
| Endritblock / Rohendritblock | ja | nein |
| Doppelsprung | ja | ja – über eine Verzauberung „Doppelsprung“ auf den Stiefeln, wirkt mit dem vollen Set |

- **`Endrite-Datapack.zip`** in den Ordner `datapacks` der Welt legen (oder beim Erstellen der Welt auswählen). Weil der Datapack die End-Biome verändert, zeigt Minecraft beim Laden den Hinweis „Experimentelle Einstellungen“ – das ist normal.
- **`Endrite-Resourcepack.zip`** als Ressourcenpaket aktivieren. Ohne Resource Pack sehen die Items wie Netherit aus und die **Rüstung ist unsichtbar**.
- Erz generiert nur in End-Chunks, die nach dem Installieren neu entstehen.
- Abbauen dauert länger als in der Mod, weil weinender Obsidian sehr hart ist (Diamant-Spitzhacke ca. 10 s, mit Effizienz V deutlich schneller).
- Unterschiede: Weinender Obsidian, den man selbst ins End mitbringt (z. B. vom Piglin-Handel), droppt dort ebenfalls Rohendrit. Endrit-Ausrüstung zählt für Vanilla-Fortschritte als Netherit. Der Doppelsprung addiert seinen Schub zur aktuellen Geschwindigkeit; wer schon schnell fällt, kommt weniger hoch als in der Mod.

Neu erzeugen: `python3 tools/make_datapack.py <entpackter Minecraft-1.21.11-Client>`.

## Bauen

```sh
./gradlew build
```

Die fertige Mod liegt danach in `build/libs/endrite-<version>.jar` (benötigt Fabric Loader und Fabric API).
