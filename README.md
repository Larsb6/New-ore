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

## Bauen

```sh
./gradlew build
```

Die fertige Mod liegt danach in `build/libs/endrite-<version>.jar` (benötigt Fabric Loader und Fabric API).
