# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A **Fabric** mod for Minecraft about **rough construction (kaba inşaat)**. v1.0 = "Structure & Blueprints": concrete molds that cure, rebar, a trowel builder's wand that uses mortar, steel beams, excavation/demolition tools, and blueprints projected into the world as a hologram ghost. Goal: fun and Minecraft-like, **not** a realistic simulator, and with **no chores**. Every tool must be functional.

- Loader: Fabric (Java). Fabric API is the only dependency. **No other mods as dependencies.**
- The user is a student, B1–B2 English, Windows 11. Budget is $0.

## Docs

@docs/rules.md
@docs/phases.md

- `docs/game-design.md` — **source of truth for game content.** v1.0 scope is locked (§0 has all user decisions, §5 is the "later" list).
- `docs/items.md` — **what is really built:** every item/tool/block with names, recipe, stats, rules and code files. Update it in the same step as any item change.
- `docs/research.md` — technical research: versions, tools, concepts, technical risks, sources.

**Starting a new chat:** read "Current status" at the top of `docs/phases.md` first. Update it at the end of every step, so the next chat can continue without the old conversation.

## Commands

The Gradle project is created in Phase 0, and these commands don't work before that. On Windows use `gradlew.bat` in PowerShell, or `./gradlew` in Bash.

- `./gradlew runClient` — start Minecraft with the mod (main way to test)
- `./gradlew runServer` — start a dedicated server (catches client-only code used on the server)
- `./gradlew build` — build the mod jar into `build/libs/`
- `./gradlew runDatagen` — generate JSON (recipes, models, loot tables, tags, advancements, lang) if datagen is set up
- `./gradlew test` / `./gradlew test --tests "ClassName.methodName"` — unit tests for pure-Java logic (e.g. mold flood fill). Most testing is manual in-game.

## Architecture (Fabric basics to keep in mind)

- **Two entrypoints** in `src/main/resources/fabric.mod.json`: the main `ModInitializer` (both sides: blocks, items, block entities, entities, networking) and the `ClientModInitializer` (client only: screens, rendering, key bindings).
- **Split source sets:** `src/main/` is common, `src/client/` is client-only. Never reference client classes (e.g. `Minecraft`, screens, renderers) from `src/main/`, or the dedicated server crashes.
- **Registration:** one registry class per content type (e.g. `ModBlocks`, `ModItems`, `ModBlockEntities`), called from the main initializer. The mod ID is the namespace.
- **Assets/data:** `src/main/resources/assets/<modid>/` (models, blockstates, lang `en_us.json` + `tr_tr.json`) and `src/main/resources/data/<modid>/` (recipes, loot tables, tags, advancements).
- **Textures:** models point to vanilla textures (`minecraft:block/...`) as placeholders. Never copy Mojang PNGs into the repo.
- **Minecraft 26.1+ is unobfuscated: use Mojang names (`Level`, `Player`, `Minecraft`, `CreativeModeTabEvents`), not Yarn names (`World`, `PlayerEntity`, `MinecraftClient`, `ItemGroupEvents`).** Java 25, plugin `net.fabricmc.fabric-loom`. Versions are in `gradle.properties`. Old tutorials and memory are often wrong, so check docs.fabricmc.net or the generated Minecraft sources.
- **Key design choices (see `docs/research.md` §6 for the reasons):**
  - The hologram ghost uses vanilla **Block Display entities**, not custom rendering.
  - Concrete curing uses **scheduled ticks**, not block entities.
  - Blueprints use vanilla **StructureTemplate** and save **blocks only**, never container contents.
