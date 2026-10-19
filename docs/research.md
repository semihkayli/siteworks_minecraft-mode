# Technical Research — Fabric mod in 2026

_Checked 2026-10-11. This file is about **technology only**. Game content is in `docs/game-design.md`, and the plan is in `docs/phases.md`._

---

## 0. Short summary

- **Target:** Minecraft **26.3** (or 26.2), Fabric Loader + Fabric API, **Java 25**. Everything is free ($0).
- **Big warning:** since **26.1**, Minecraft's code is **not obfuscated (gizlenmemiş)**, and Fabric uses **Mojang's official names**. Most YouTube tutorials and the old Fabric Wiki use old names (Yarn), so their code will not work as written. Use **docs.fabricmc.net** first.
- **Key vanilla features we reuse:**
  - **Block Display entities** for the hologram ghost
  - **StructureTemplate** (`.nbt`) for blueprints
  - **scheduled ticks** for concrete curing
  - **falling-block behaviour** for wet concrete

---

## 1. Versions and tech stack

| Thing | Version / choice | Note |
|---|---|---|
| Minecraft | **26.3 "Wilderness Bound"** (2026-09-15) | 26.2 is the alternative (decide in Phase 0) |
| Java (JDK) | **25** | Needed for Minecraft 26.1+ |
| Fabric Loader | 0.19.5 | Latest stable |
| Fabric API | 0.162.0+26.3 (2026-10-06) | Mod ID is now `fabric-api` (was `fabric`) |
| Fabric Loom (Gradle plugin) | 1.18.4 (pinned 2026-10-19; was `1.18-SNAPSHOT`) | Plugin: `net.fabricmc.fabric-loom` |
| Gradle | 9.7.1 | Comes with the template (Gradle wrapper) |
| Names in code | **Mojang official names** | Yarn is not supported any more |
| IDE | IntelliJ IDEA **2025.3+** | Older versions break Mixins |

**26.3 vs 26.2:**
- **26.3** is the newest, so the mod stays useful longer. Most of its changes are in world generation, brewing and composting, and we don't use those. ✅ Recommended.
- **26.2** matches the current official docs exactly.
- 26.3 also removed block codecs and moved keyboard input from GLFW to SDL (`InputConstants`).

Check the newest numbers again at <https://fabricmc.net/develop/> before setup.

---

## 2. Big changes in 2026 (why old tutorials are dangerous)

1. **No obfuscation (26.1).** Old Yarn names like `World`, `PlayerEntity` and `ItemGroupEvents` became Mojang names like `Level`, `Player` and `CreativeModeTabEvents`.
2. **Build file:** use `implementation` instead of `modImplementation`, and `jar` instead of `remapJar`. The Fabric template does this.
3. **ItemStack timing:** an `ItemStack` can't be created before a world loads. Some methods use `ItemStackTemplate`.
4. **Rendering:**
   - Drawing is now 2 stages: first **extract (çıkarmak)** data, then **submit (göndermek)** draw commands.
   - World render events came back as *level render* events.
   - Raw OpenGL code must move to Blaze3D.
   - → One more reason we use Block Display entities for the ghost.
5. **26.3:**
   - The fluid behaviour API is now stable.
   - New `FluidFlowCallback`.
   - Several registries were replaced by **data components (veri bileşenleri)** and data packs.

**Rule:** trust docs.fabricmc.net, the Fabric blog and the **generated Minecraft sources** in the IDE. Don't trust old wiki pages, pre-2026 videos, or Claude's memory.

---

## 3. Tools to install (all free)

| Tool | Why | Note |
|---|---|---|
| **Eclipse Temurin JDK 25** | Java compiler/runtime | `winget install EclipseAdoptium.Temurin.25.JDK` |
| **IntelliJ IDEA** (free core, 2025.3+) | Java editor | ⚠️ Don't start the Ultimate trial (it asks for payment later) |
| **Minecraft Development plugin** | Helps with Fabric/Mixins/JSON | Don't use its project wizard, because its templates are old |
| **Fabric Template Mod Generator** | Correct starter project | <https://fabricmc.net/develop/template/> |
| **Blockbench** | Custom block shapes (stake, beam, rebar, net) | Free, open source |
| **Git** | Version history, undo mistakes | GitHub is optional |
| Pixel editor (Piskel / LibreSprite / Paint.NET) | Own textures, later | ⚠️ Aseprite costs money |
| **Modrinth** | Publish the mod, later | Free |

---

## 4. Concepts we need (Java + Fabric)

**Java:**
- classes and **inheritance (kalıtım)**
- **interfaces (arayüz)**
- static fields
- **generics (genel tipler)**
- **lambdas** (`->`, for events)
- **records** (small data classes)
- enums
- queues/sets (for **flood fill (taşma doldurma)**)

| Concept | Simple meaning | Used for |
|---|---|---|
| **Registry (kayıt)** | Tell the game "this block/item exists" | Everything |
| **Block state (blok durumu)** | Small values on a block (`axis=x`, `wet=true`) | Steel beam axis, mixer fill level |
| **Block entity (blok varlığı)** | Extra data + logic on a block | Concrete Mixer, Survey Stake (holds the blueprint) |
| **Scheduled tick (zamanlanmış tik)** | "Call this block again in N ticks", a light timer | Wet concrete curing (~2 min = 2400 ticks) |
| **Falling block** | Vanilla gravity (sand, gravel) | Wet concrete |
| **Data component (veri bileşeni)** | Data stored on an item stack | Blueprint ID/name/size, Measuring Tape corner 1, Trowel mode |
| **Saved data** | Data saved with the world | Blueprint contents |
| **StructureTemplate** | Vanilla class behind the structure block (`.nbt`) | Saving/loading blueprints |
| **Block Display entity** | Vanilla entity that *shows* a block (can be scaled, can glow) | The hologram ghost |
| **Menu + Screen** | A GUI with slots (like the cartography table) | Drafting Table |
| **Tags** | Groups of blocks/items (`#minecraft:logs`) | "masonry blocks" for the trowel, "soft ground" for the mattock |
| **Datagen (veri üretimi)** | Java code that writes JSON (recipes, models, loot, advancements, lang) | Fewer hand-written JSON files |
| **Client / server split** | Server = logic, client = drawing | Keep client code in `src/client/` |
| **Mixin** | Changing Minecraft's own code | Avoid, use only if no event exists |

---

## 5. Similar mods (lessons)

| Mod | Lesson |
|---|---|
| **Litematica** | The best ghost-preview UX (missing = see-through, wrong = red). We copy the *idea*, survival-friendly |
| **Create** – Schematic & Quill | 2-corner selection is easy to understand. Auto-build is very strong, so we don't do it |
| **Construction Wand / Building Wands** | Wand that extends a wall face, with preview and modes → our Trowel |
| **StructSaver** (Fabric) | The vanilla `.nbt` structure format is enough for blueprints |
| **Carry On** | Moving full chests works, but has duplication risks → our Hand Truck waits for v1.1 |
| **Elevator mods, MrCrayfish Furniture (electricity), SecurityCraft, Fluid Pipes, JustSink, Cable Facades** | References for the *later* services/finishing versions |
| **Create: Pipes'n Physics** | ❌ Example of "too realistic". We avoid this |

---

## 6. Technical risks

| Risk | Plan |
|---|---|
| Old tutorials / AI memory use old names | Check docs + generated sources every time |
| Ghost = many entities → lag | Layer mode (Laser Level) + max ~512 visible ghost blocks |
| Ghost entities left behind (server crash, chunk unload) | Tag every ghost entity with the stake's ID, and clean up orphans when chunks load |
| Ghost must update when players build | Re-check only the visible blocks, on block place/break near a stake, not every tick |
| Item duplication bugs | No container-moving in v1.0 (Hand Truck → v1.1). Test the trowel carefully |
| Lag from many curing blocks | Scheduled ticks, not block entities |
| Money | Nothing costs money. Traps: Aseprite, the IntelliJ Ultimate trial |

---

## 7. Sources

**Official:**
- [Fabric 26.1 blog](https://fabricmc.net/2026/03/14/261.html)
- [Fabric 26.3 blog](https://fabricmc.net/2026/09/15/263.html)
- [Fabric 1.21.9 blog (render events removed)](https://fabricmc.net/2025/09/23/1219.html)
- [Fabric 1.21.11 blog](https://fabricmc.net/2025/12/05/12111.html)
- [Fabric Docs](https://docs.fabricmc.net/develop/)
- [Migrating mappings](https://docs.fabricmc.net/develop/porting/mappings/)
- [Porting Fabric API 26.1](https://docs.fabricmc.net/develop/porting/fabric-api)
- [Block Entity Renderers (26.1.2)](https://docs.fabricmc.net/26.1.2/develop/blocks/block-entity-renderer)
- [Setting up IntelliJ (26.1.2)](https://docs.fabricmc.net/26.1.2/develop/getting-started/intellij-idea/setting-up)
- [Fabric develop page](https://fabricmc.net/develop/)
- [Fabric API on Modrinth](https://modrinth.com/mod/fabric-api/version/0.162.0+26.3)
- [Fabric API releases](https://github.com/FabricMC/fabric-api/releases)
- [Fabric Wiki Transfer API (legacy)](https://wiki.fabricmc.net/tutorial:transfer-api)
- [IntelliJ unified product](https://www.jetbrains.com/help/idea/intellij-idea-single-distribution.html.md)
- [Temurin 25](https://adoptium.net/de/news/2025/09/eclipse-temurin-25-available)
- [Adoptium install](https://adoptium.net/fr/installation)
- [Blockbench](https://github.com/JannisX11/blockbench)
- [Display entity data (Minecraft Wiki)](https://minecraft.wiki/w/Template:Nbt_inherit/display_entity/template)
- [Display class (mappings.dev)](https://mappings.dev/1.20.1/net/minecraft/world/entity/Display.html)

**Mods:**
- [Create Schematic & Quill](https://create.fandom.com/wiki/Schematic_And_Quill)
- [Construction Wand (Fabric)](https://www.curseforge.com/minecraft/mc-mods/construction-wand-fabric)
- [Building Wands](https://www.curseforge.com/minecraft/mc-mods/building-wands)
- [StructSaver](https://modrinth.com/mod/structsaver)
- [Carry On](https://modrinth.com/mod/carry-on)
- [Fluid Pipes](https://modrinth.com/mod/fluidpipes)
- [JustSink](https://moddex.gg/mod/justsink)
- [Simple Copper Pipes](https://moddex.gg/mod/simple-copper-pipes)
- [Create: Pipes'n Physics](https://modrinth.com/mod/CuAT8bVS)
- [MrCrayfish's Furniture Refurbished](https://www.curseforge.com/minecraft/mc-mods/refurbished-furniture)
- [SecurityCraft](https://modrinth.com/mod/security-craft)
- [Elevator mod](https://modrinth.com/mod/elevator-mod)
- [Cable Facades](https://www.modpackindex.com/mod/68401/cable-facades)

**Real-life construction:**
- [Stages of construction](https://www.homeloanexperts.com.au/home-loan-articles/stages-of-construction/)
- [10 phases of construction](https://mycnr.com/guest-column/10-phases-of-construction-what-to-expect-for-your-build/)
- [Formwork stripping time](https://infralens.in/term/stripping-time)
- [Rough-in phase](https://www.flipperforce.com/rough-in-phase)
- [Rough-in inspections](https://open-exam-prep.com/study-guides/icc-cbo/field-inspection-compliance/cbo-12-5)
- [Batter boards](https://inspectapedia.com/decks/Batter-Board-Layout.php)

**Textures:**
- [Forge forum: vanilla textures](https://forums.minecraftforge.net/topic/88526-using-vanilla-minecraft-textures)
- [MCreator note](https://mcreator.net/node/53162)

**Other (less official):**
- [26.3 Fabric status](https://space-node.net/blog/minecraft-26-3-server-update-guide-2026)
- [Java requirements](https://modready.gg/guides/minecraft-java-version-requirements)
