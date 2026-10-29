# TitanOres — project context

Endgame ore progression mod for the **Titan Modpack**. Author: Pedro (beginner in modding — explain steps in
**Portuguese** in chat; keep everything inside the mod in **English**).

## Stack
- Minecraft **1.16.5**, Forge **36.2.42**, official mappings, mod id `titanores`, package `com.titanmods.titanores`.
- ForgeGradle 6 + Gradle **8.8** wrapper (runs on the system JDK 21); mod compiles/runs on **Java 8** via toolchain.
  JDK 8 path is in the USER file `~/.gradle/gradle.properties` (never put machine paths in the project).
- Optional dep: Curios 1.16.5-4.1.0.1 (`compileOnly`/`runtimeOnly fg.deobf`). Third-party mods for testing go in
  `build.gradle` with `fg.deobf`, never in `run/mods` (obfuscated jars crash dev env).
- Test-only mods in runClient (versions in `gradle.properties`): JEI 7.7.1.153 (API is `compileOnly` for future
  JEI integration), Mekanism 10.1.2.457, Powah 2.3.16 + Lollipop 3.2.9, Thermal Foundation 1.5.2.30 + CoFH Core 1.5.2.22 (iron gear for
  testing) (via CurseMaven file ids).
  runClient JVM: `-Xms2G -Xmx6G` (PC has 16 GB).

- Sister mod **Titan Utilities** (`D:\projetos\Mods\TitanUtilities`, mod id `titanutilities`, package
  `com.titanmods.titanutilities`) depends on this mod (loads `build/libs/titanores-<version>.jar` in dev). The final
  items will move there (ids change). Bump its `titanores_version` when this mod's version changes.

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
- Exception to the per-material folders: the modpack's FINAL items use `textures/item/finalitens/` (user's choice).
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
  (machines moved to Titan Utilities).
- MOVED TO TITAN UTILITIES (2026-10-29, TitanOres 1.0.3): Titan Factory, Titan Crafter, Speed Upgrade, their recipe
  types (`titanutilities:titan_factory` / `titan_crafting`), the machine recipes (incl. Emberium alloy and the
  Titanium Music Disc), `ModEnergyStorage`, the JEI plugin and the `machine_blocks` tag. Their ids are now
  `titanutilities:*`. Forge Upgrades moved too (1.0.4), then the final items + Blue Soil (1.0.5). See `D:\projetos\Mods\TitanUtilities\CLAUDE.md`. Emberium items stay here (made by the factory).
- Final items (10) and Blue Soil MOVED TO TITAN UTILITIES (TitanOres 1.0.5, ids `titanutilities:*`; details in its
  CLAUDE.md). RECIPE RULE (user): recipes using only vanilla + Titan items go IN THE MOD (JSON); KubeJS in the modpack
  only for recipes needing other mods' items.
- Farming (branch `feacture/seeds`): DONE parts 1+2. Items `<ore>_seed` (BlockNamedItem of `<ore>_crop`) and
  `<ore>_essence` (tags `forge:seeds`, `titanores:essences`); Blue Soil now lives in Titan Utilities. `block/EssenceCropBlock`: grows ONLY on the block tag
  `titanores:essence_crop_soil` (empty here; Titan Utilities adds `titanutilities:blue_soil`) (overrides canSurvive; vanilla asks for farmland),
  1-in-5 chance per random tick (slower than wheat); `event/CropEvents` cancels vanilla bone meal (other mods' growth
  items/accelerators still work). Mature drop: 1 essence + 1 seed + 25% chance of an extra seed. 8 essences (ring) -> 1 nugget. Crop stage textures
  (`textures/block/crop/`) are from Mystical Agriculture (MIT, credited in README + `resources/licenses/`), final
  stage recolored per ore. Part 3 DONE: ore processing. Items `<ore>_dirty_dust/_clump/_shard/_crystal`
  (textures `textures/item/mekanism/`, Mekanism tags `mekanism:<kind>s/<ore>`); slurries `titanores:dirty_<ore>` /
  `clean_<ore>` in `compat/mekanism/MekanismCompat` (only registered if Mekanism is loaded; clean uses
  `fluid/cold_<ore>_still`, dirty is a darkened copy). Recipes in `recipes/processing/<ore>/` with `forge:mod_loaded`
  conditions: Mekanism 2x-5x chain (copied from Mekanism's iron), 2x dust in IE crusher, Create crushing+milling,
  Occultism crushing, Blood Magic ARC; dust -> ingot smelting/blasting. No Thermal (pack has no Thermal Expansion).
  NEXT: `fluid/hot_<ore>_*` = Tinkers molten metal (ask what the "cold" liquid means there); Botany Pots via KubeJS.
- Forbidden & Arcanus compat: the Forge Upgrades (`forge_upgrade_tier_2..5`) MOVED TO TITAN UTILITIES (TitanOres 1.0.4,
  ids `titanutilities:*`). The Source Crystal rituals in the pack still give `titanores:source_crystal`.
- Ore tooltips: `item/OreBlockItem` adds 2 gold lines (lang `tooltip.titanores.<ore>.where/.height`; pickaxe
  left to Waila/Jade, user's choice);
  keep them in sync with `world/ModOreGeneration` and the tiers.

## Documentation (separate repo — keep it updated after EVERY change)
- `D:\projetos\Mods\TitanOres_doc` — Material for MkDocs, GitHub Pages.
- After mod changes: `python scripts/sync_from_mod.py ../TitanOres`, update the page + `docs/changelog.md`,
  then `python -m mkdocs build --strict`.

## Git
- Remote `github.com:pedroedu06/TitanOres`, branch `main`. The user commits/pushes; suggest `git add -A` + message.
