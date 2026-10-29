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
  block + GUI + energy + processing, Titan Crafter (second machine).
- Titan Factory (name will change): 100M FE buffer (`energy/ModEnergyStorage`, receive-only, all sides), gradual
  consumption (energy/time per tick, pauses without power), items in from TOP (column-aware `TopInput`) and out from
  BOTTOM. Recipe type `titanores:titan_factory` (`left`/`right` columns of 3, `mirrored`, `energy`, `time`, `result`).
  Speed upgrades: item `speed_upgrade`, ONE slot (holds up to 4) in a tab on the RIGHT of the GUI next to the energy bar (separate handler, NBT "Upgrades", no automation access); each halves the time, total energy unchanged.
  Recipes: Emberium (3 emberite + 3 titanium ingots -> 3 emberium, 40M FE, 60 s); Emberium Block (3 emberite + 3 titanium blocks -> 3 emberium blocks, 80M FE, 120 s); Speed Upgrade (each column clock / swiftness potion via `forge:nbt` / solarite ingot -> 1, 800k FE, 10 s; mixed columns, so top automation cannot build it). User will define ~5 more recipes.
  Machine crafting recipe (`recipes/titan_factory.json`, shaped): `IVI / GMG / SGS` = emberite ingot, glass
  (`forge:glass/colorless`), iron gear (`forge:gears/iron`), machine block (tag `titanores:machine_blocks`, optional
  entries: mekanism steel casing, thermal machine frame, etc. — modpacks extend it by datapack), solarite ingot.
- Titan Crafter (`titan_crafter`): 9x7 grid (slots 0-62 row by row, 63 output; was 9x6 — user wanted an odd height for symmetric
  recipes; old 55-slot saves are migrated in `loadItems`), 200M FE buffer, same tick logic as the
  factory, no upgrades. Recipe type `titanores:titan_crafting` = FIXED 9x7 pattern (7 rows x 9 chars, space = empty,
  no offset/mirror). Top input is "template" style (`TitanCrafterItemHandlers.TopInput`: only tops up slots already
  holding the item); bottom = output. GUI `textures/gui/titan_crafter_gui.png` (240x240). Made in the factory:
  2 crafting tables + 2 iron blocks + 2 `forge:gears/iron`, 80M FE, 240 s.
  TODO: switch those gears to `forge:gears/diamond` when the modpack (AllTheOres) is ready; final recipes wait for the
  modpack mod list + special items. Crafter tested OK. Recipes: Titanium Music Disc (36 titanium ingots in a
  diamond shape + `#minecraft:music_discs` in the center, 36M FE, 60 s).
- JEI: `compat/jei/` (`@JeiPlugin`, only loaded by JEI): Titan Factory category using the machine GUI crop, looping
  animation (progress fills, energy bar statically shows only the recipe energy), click areas on the GUI arrows,
  "+" transfer handler (slots 0-5), upgrade tab registered as a JEI extra area. JEI 7.7.1 recipe windows are FIXED at
  198 px wide, so the Titan Crafter category uses a compact custom layout (180 px) instead of its 240 px GUI.
- Final items (10, all created): endgame components for the modpack. RECIPE RULE (user): recipes using only
  vanilla + this mod's items go IN THE MOD (JSON); KubeJS in the modpack only for recipes needing other mods' items. All final items follow the Source Crystal style (epic rarity, no glow,
  no tooltip, no code comments). Modpack recipes that need other mods live in the PACK repo:
  `C:/Users/Pedro/curseforge/minecraft/Instances/TitanMods/kubejs/server_scripts/titan_final_items.js`
  (Sponge Bob = Create mechanical crafting 7x8). Recipe types without a KubeJS helper go as JSON in the pack's
  `kubejs/data/titanmodpack/...` (datapack): Source Crystal = Forbidden & Arcanus Hephaestus Forge rituals
  (`hephaestus_forge/rituals/source_crystal_<fire|ice|lightning>_heart.json`; FA 1.16 forge has ONLY 6 PEDESTALS (max 6 inputs, slots 0-5); inputs are single items, no tags, and
  position-independent; essences 600 aureal / 8 souls / 20000 blood / 350 XP = needs forge tier 3+). Docs: own page `docs/final-items.md` (table + one section per item). Done so far: `source_crystal`, `sponge_bob` (made with Create in the modpack),
  `ancestral_seed`, `world_core`. `raid_king` (crown): `item/RaidKingItem` (HEAD equipment slot, no armor, NBT `HeroLevel`) +
  `event/RaidKingEvents`: drops where the last raider dies once a raid with `getBadOmenLevel() >= getMaxBadOmenLevel()`
  turns `isVictory()` (pending map per world, checked on WorldTick); worn on head or Curios `head` slot -> Hero of the
  Village amplifier HeroLevel-1 renewed every 20 ticks. NOTE: in LivingDeathEvent the raider already left its raid
  (die() calls removeFromRaid first), so the raid is found with `getRaidAt(pos)`. Tested OK in game.
  Plain final items added 2026-10-15: `antenna`, `calculator`, `iron_man_mask`, `music_disc_titanium`, `universal_remote`
  (10 of 10 — all final items exist; only recipes remain).
- Farming (branch `feacture/seeds`): DONE parts 1+2. Items `<ore>_seed` (BlockNamedItem of `<ore>_crop`) and
  `<ore>_essence` (tags `forge:seeds`, `titanores:essences`); `blue_soil` (cube_bottom_top, shovel; recipe via KubeJS in
  the modpack). `block/EssenceCropBlock`: grows ONLY on blue_soil (overrides canSurvive; vanilla asks for farmland),
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
- Forbidden & Arcanus compat: FA 16.2.3 Hephaestus Forge tiers can't be upgraded in survival (Level only read from
  NBT). `item/ForgeUpgradeItem` (`forge_upgrade_tier_2..5`, 5 = "X") right-clicked on `forbidden_arcanus:hephaestus_forge`
  raises the NBT Level by one (no compile dependency). Upgrade rituals (adapted from FA 1.21.1, essences scaled to the
  1.16 tier caps) live in the pack's `kubejs/data/titanmodpack/hephaestus_forge/rituals/`. Textures generated in
  `textures/item/forge_upgrade/`. Mod version 1.0.2.
- Ore tooltips: `item/OreBlockItem` adds 2 gold lines (lang `tooltip.titanores.<ore>.where/.height`; pickaxe
  left to Waila/Jade, user's choice);
  keep them in sync with `world/ModOreGeneration` and the tiers.

## Documentation (separate repo — keep it updated after EVERY change)
- `D:\projetos\Mods\TitanOres_doc` — Material for MkDocs, GitHub Pages.
- After mod changes: `python scripts/sync_from_mod.py ../TitanOres`, update the page + `docs/changelog.md`,
  then `python -m mkdocs build --strict`.

## Git
- Remote `github.com:pedroedu06/TitanOres`, branch `main`. The user commits/pushes; suggest `git add -A` + message.
