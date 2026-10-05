# TitanOres — project context

Endgame ore progression mod for the **Titan Modpack**. Author: Pedro (beginner in modding — explain steps in
**Portuguese** in chat; keep everything inside the mod in **English**).

## Stack
- Minecraft **1.16.5**, Forge **36.2.42**, official mappings, mod id `titanores`, package `com.titanmodpack.titanores`.
- ForgeGradle 6 + Gradle **8.8** wrapper (runs on the system JDK 21); mod compiles/runs on **Java 8** via toolchain.
  JDK 8 path is in the USER file `~/.gradle/gradle.properties` (never put machine paths in the project).
- Optional dep: Curios 1.16.5-4.1.0.1 (`compileOnly`/`runtimeOnly fg.deobf`). Third-party mods for testing go in
  `build.gradle` with `fg.deobf`, never in `run/mods` (obfuscated jars crash dev env).
- Test-only mods in runClient (versions in `gradle.properties`): JEI 7.7.1.153 (API is `compileOnly` for future
  JEI integration), Mekanism 10.1.2.457, Powah 2.3.16 + Lollipop 3.2.9, Thermal Foundation 1.5.2.30 + CoFH Core 1.5.2.22 (iron gear for
  testing) (via CurseMaven file ids).
  runClient JVM: `-Xms2G -Xmx6G` (PC has 16 GB).

## Commands (PowerShell, from repo root)
- Build: `.\gradlew.bat build` (filter output with `Select-String 'error:|BUILD'`).
- Run: `.\gradlew.bat runClient` — the user tests in game; I don't launch the game.
- Don't write Java files with PowerShell `Set-Content` (adds a BOM that breaks javac).

## Conventions
- IDs, file names, comments, logs in English; Portuguese only in `lang/pt_br.json`.
- Textures: lowercase per-material folders `textures/item/<material>/<material>_<item>.png`, `textures/block/<material>/`,
  worn armor `textures/models/armor/<material>_layer_1|2.png`, GUI `textures/gui/`. No spaces/uppercase in names.
- Item models of blocks use parent `titanores:block/<id>` (no subfolder); only textures use subfolders.
- Every item is fire resistant (`ModItems.props()`).
- After adding assets, verify all model/texture references exist (small Python check) before building.
- Plan mode is used often: write the plan, wait for approval, then implement.

## Where things are
- Registries: `init/` (ModBlocks, ModItems, ModTileEntities, ModContainers, ModRecipes, ModLootModifiers, ModTags).
- Tool stats `item/ModItemTier`; armor stats `item/ModArmorMaterial`; worldgen `world/ModOreGeneration`.
- Gameplay events in `event/`; client HUD/screens/keybinds in `client/`; network in `network/`.
- Data: `src/main/resources/data/titanores/` (recipes, loot tables, loot_modifiers, tags) + `data/forge/tags`.

## Current state (see memory files for full numbers)
- Done: Solarite / Emberite / Titanium ores, all 4 tool tiers (incl. Titanium Star), all 4 armor tiers, special
  items (heart, apple, carrot, lantern, magnet, star), Emberium alloy (made in the Titan Factory), Titan Factory
  block + GUI + energy + processing.
- Titan Factory (name will change): 100M FE buffer (`energy/ModEnergyStorage`, receive-only, all sides), gradual
  consumption (energy/time per tick, pauses without power), items in from TOP (column-aware `TopInput`) and out from
  BOTTOM. Recipe type `titanores:titan_factory` (`left`/`right` columns of 3, `mirrored`, `energy`, `time`, `result`).
  Speed upgrades: item `speed_upgrade`, ONE slot (holds up to 4) in a tab on the RIGHT of the GUI next to the energy bar (separate handler, NBT "Upgrades", no automation access); each halves the time, total energy unchanged.
  Recipes: Emberium (3 emberite + 3 titanium ingots -> 3 emberium, 40M FE, 60 s); Emberium Block (3 emberite + 3 titanium blocks -> 3 emberium blocks, 80M FE, 120 s); Speed Upgrade (each column clock / swiftness potion via `forge:nbt` / solarite ingot -> 1, 800k FE, 10 s; mixed columns, so top automation cannot build it). User will define ~5 more recipes.
  Machine crafting recipe (`recipes/titan_factory.json`, shaped): `IVI / GMG / SGS` = emberite ingot, glass
  (`forge:glass/colorless`), iron gear (`forge:gears/iron`), machine block (tag `titanores:machine_blocks`, optional
  entries: mekanism steel casing, thermal machine frame, etc. — modpacks extend it by datapack), solarite ingot.
- JEI: `compat/jei/` (`@JeiPlugin`, only loaded by JEI): Titan Factory category using the machine GUI crop, looping
  animation (progress fills, energy bar statically shows only the recipe energy), click areas on the GUI arrows,
  "+" transfer handler (slots 0-5), upgrade tab registered as a JEI extra area.
- Pending (after the machine): ore **tooltips** showing where to find them (dimension, biome, Y range, pickaxe);
  details to be defined with the user.

## Documentation (separate repo — keep it updated after EVERY change)
- `D:\projetos\Mods\TitanOres_doc` — Material for MkDocs, GitHub Pages.
- After mod changes: `python scripts/sync_from_mod.py ../TitanOres`, update the page + `docs/changelog.md`,
  then `python -m mkdocs build --strict`.

## Git
- Remote `github.com:pedroedu06/TitanOres`, branch `main`. The user commits/pushes; suggest `git add -A` + message.
