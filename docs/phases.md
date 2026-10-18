# Phases

## Current status

- **Phase:** 2 — Concrete, **in progress** (discussed and decided 2026-10-18, see Phase 2 "Decisions"). **Step 1 (Formwork, Rebar, Concrete, Reinforced Concrete) done ✅ (tested, committed).** **Step 2 (Cement Mix + Cement) done ✅.** **Step 3 (Wet Concrete) done ✅** (incl. the Hard Hat anvil test). **Step 4 (Concrete Bucket + mold pour) done ✅** (14 unit tests). **Step 5 (Concrete Mixer) done ✅.** Step 6 (Mixer hopper input) in progress. Phase 1 is fully done ✅.
- **Phase 0 summary:** JDK 25 + IntelliJ + MC Dev plugin. Fabric template (26.3, datagen ON, split sources ON, Groovy). Loader 0.19.5, Loom 1.18-SNAPSHOT (1.18.4), Gradle 9.7.1. Harmless log errors: `Perflib 009` and `401 Failed to fetch user properties`. Game log: `run/logs/latest.log` (Claude can read it). Git: local + GitHub `origin/main`.
- **Phase 1 summary:** Mattock + Sledgehammer (3×3 via `AreaToolItem` + `AreaMining`), Hard Hat (`HardHatProtection`), Safety Net block (`SafetyNetBlock`), Ghost Test item (Block Displays). Datagen for all JSON. First class tweaker. **All built items are documented in `docs/items.md`.**
- **Code layout:** `registry/` (ModItems, ModBlocks, ModTags, ModCreativeTabs), `item/`, `block/`, `event/` in `src/main/java/.../siteworks/`. Datagen providers in `src/client/java/.../client/datagen/`. Output in `src/main/generated/` (commit it, because CI doesn't run datagen).
- **Datagen notes:** run `gradlew runDatagen` after changing any provider. Never edit `src/main/generated/` by hand. Vanilla tags must be added with `forceAddTag` (plain `addTag` fails because datagen can't see vanilla tags). A JNA `Win32Exception: Parametre hatalı` line in the datagen log is harmless.
- **26.3 API notes (old tutorials are wrong here):**
  - Dyes: `Items.DYE.yellow()` (a `ColorCollection`), not `Items.YELLOW_DYE`.
  - Colored blocks too: `Blocks.CONCRETE_POWDER.gray()`, not `Blocks.GRAY_CONCRETE_POWDER`. (26.3 also has vanilla `CONCRETE_STAIRS` and `CONCRETE_SLAB`.)
  - Entity types: `EntityTypes.BLOCK_DISPLAY` (plural class).
  - Block/item IDs for tags: `BlockItemIds.X`.
  - Tools: plain `Item` + `Properties.shovel/pickaxe(...)` (no `ShovelItem`).
  - Block bounce: `Properties.bounceRestitution(float)`.
  - Render layers are picked from the texture's transparency (no `BlockRenderLayerMap`).
  - Block codecs are gone.
  - Private Minecraft methods: add them to `src/main/resources/siteworks.classtweaker` (header `classTweaker v1 official`).
- **Checking APIs:** `gradlew genSources` fails while Minecraft/IntelliJ holds the jar ("file used by another process"). Either close them and run it, or use `javap` on `~/.gradle/caches/fabric-loom/minecraftMaven/.../minecraft-common-deobf-26.3.jar` (and `minecraft-clientonly-deobf`).
- **Phase 1 review (2026-10-12), fixes done and tested OK (enchanting, floor + wall 3×3):** (1) tools + Hard Hat added to the vanilla tags `#shovels`/`#pickaxes`/`#head_armor` (`ModItemTagProvider`), so enchanting works; (2) 3×3 checks `level.mayInteract()` (spawn protection + world border); (3) the 3×3 plane uses the face you hit. The other review notes are in the Phase 2, 5 and 6 discussion lists.
- **Next step:** step 6 (Mixer hopper input with `WorldlyContainer`). Run `gradlew test` before each commit.
- `.github/workflows/build.yml` runs a build on GitHub Actions after each push (free within GitHub's monthly limit for private repos).
- **Open items:** the template's `LICENSE` file and `fabric.mod.json` say **CC0** (template default, NOT our decision). Decide the real license in Phase 7. `contact` in `fabric.mod.json` still points to Fabric's example repo.
- **Notes for Claude:**
  - The user's mood can be low, so lead with ideas and recommendations, and the user guides with feedback.
  - The user hates **chores** (angarya) in gameplay. Every tool must be functional.
  - v1.0 scope is **locked**. New ideas go to `docs/game-design.md` §5 "Later".
  - The user has **no bought Minecraft account**. Dev testing (`runClient`, offline name "Player") works without one. For LAN tests, run two clients with different `--username`. Phase 7 "test in the normal launcher" needs another way (dev jar test, dedicated server, or a friend). Never suggest cracked launchers.
  - GitHub remote: `https://github.com/semihkayli/siteworks_minecraft-mode.git` (the user may rename it to `…-mod` later, then run `git remote set-url origin <new URL>`). Push only when the user asks.
  - Total estimate: **~28–43 work sessions** (2–3 h each). A guess, update it as we learn.

**How every phase works:** discuss → write decisions → small steps (each step: what changed, which files, how to test, **update `docs/items.md`**, **then a small git commit after the user's test is OK**) → "Done when" checklist → update "Current status" → final phase commit.

---

## Phase 0 — Setup (1–2 sessions)

**Goal:** an empty mod starts in Minecraft from IntelliJ, and the project is saved in Git.

**Steps:**
1. Install JDK 25 (Temurin) + IntelliJ IDEA (free) + Minecraft Development plugin.
2. Generate the project with the Fabric Template Mod Generator (split client/common sources).
3. Open it in IntelliJ, run `runClient`.
4. `git init` + first commit.

**Discuss before starting:**
- Minecraft **26.3** (recommended) or 26.2?
- **Mod ID** (e.g. `siteworks`): it's used in every file and is **hard to change later**. The display name can change any time.
- **Package name** (e.g. `com.<yourname>.siteworks`)
- Git: local only, or also GitHub (free)?
- License: MIT (open), or All Rights Reserved? (Can wait until release.)

**Done when:**
- [x] `java -version` shows 25
- [x] `runClient` opens Minecraft 26.x with no crash
- [x] The log shows our mod's "hello" line (vanilla has no mod list, and we don't add Mod Menu)
- [x] First git commit exists

**Decisions:** (2026-10-11)
- Minecraft **26.3**
- Mod ID **`siteworks`** (display name "Site Works", can change later)
- Package **`io.github.semihkayli.siteworks`**
- Git: local + **private GitHub repo** `https://github.com/semihkayli/siteworks_minecraft-mode.git` (Claude pushes only when asked)
- License: decide at release (Phase 7)

---

## Phase 1 — Site basics (3–5 sessions)

**Goal:** first real content. Learn registration, items, blocks, tags, events and translations.

**Content:** Hard Hat, Safety Net, Mattock (3×3 soft ground), Sledgehammer (3×3 stone-type, no ores), our creative tab, English + Turkish names, recipes.
**Optional extra (recommended): Ghost test (1 session).** A test item that spawns a few small glowing **Block Display** entities. It checks the riskiest idea of the whole mod early ("risk first").

**Discuss before starting:**
- Hand-written JSON or **datagen**? (Recommended: write the first item by hand to understand it, then switch to datagen.)
- Recipes for the 4 items
- Hard Hat strength (like iron helmet?)
- Do the ghost test here, or wait for Phase 5?

**Done when:**
- [x] All 4 items are in our creative tab, craftable in survival, with en/tr names
- [x] The Safety Net cancels fall damage (tested ✅). The Hard Hat blocks falling-anvil damage (tested ✅ in Phase 2 step 3)
- [x] Mattock digs 3×3 dirt but not stone, Sledgehammer digs 3×3 stone but not ores
- [x] (Optional) Ghost test: glowing small blocks look OK (user: "not sure, can change later". Results are under Phase 5)

**Decisions:** (2026-10-12)
- **JSON:** the Mattock is written **by hand** (to learn the files), then we switch to **datagen** for everything else.
- **Recipes (iron set):**
  - Hard Hat = `III / IYI` (5 iron + yellow dye)
  - Safety Net ×4 = `S.S / .S. / S.S` (5 string)
  - Mattock = `III / IS. / .S.` (4 iron + 2 sticks)
  - Sledgehammer = `III / III / .S.` (6 iron + 1 stick)
- **Tools:** Mattock and Sledgehammer have 500 durability, and each block broken costs 1. Repair with iron in an anvil, enchantable. The block you hit breaks like a normal shovel/pickaxe hit, and only the 8 blocks around it are filtered by a tag (`soft_ground` / `sledge_breakable`). Sneak = 1 block.
- **Hard Hat:** like an iron helmet (armor 2, durability 165) + full immunity to falling blocks (vanilla damage type tag `damages_helmet`). Placeholder look = golden helmet (yellow). The hat still loses durability when it blocks a hit (1 per 4 damage), user agreed.
- **Safety Net:** ~~a thin 2-pixel pad~~ → **changed in step 5 (user agreed): a 3-pixel net at the TOP of its block space** (like a trapdoor "up"), because vanilla's landing checks look 0.2/0.5 blocks under the feet and miss a thin pad at the bottom. Can be placed in the air without support. No fall damage, small vanilla bounce (`bounceRestitution 0.3`).
- **Creative tab:** "Site Works", with the Hard Hat as its icon.
- **Ghost test:** yes, at the **end of Phase 1** (a creative-only test item).
- **No mixins needed** (events + block methods). The template's example mixins are deleted.

**Steps:**
1. Clean the template mixins. Add `ModItems` + the creative tab + the **Mattock item, all JSON by hand**.
2. Mattock 3×3 digging + the `soft_ground` tag.
3. Switch to datagen. Add the Sledgehammer + the `sledge_breakable` tag.
4. Hard Hat (armor material, equipment asset, damage event).
5. Safety Net block.
6. Ghost test item.
7. "Done when" checklist → update status → commit.

---

## Phase 2 — Concrete ⭐ (6–8 sessions)

**Goal:** the core mechanic. After this phase the mod is already fun.

**Content:** Cement Mix → Cement, Concrete Mixer (right-click + hopper), Concrete Bucket (16 blocks), Formwork (instant break), Rebar, Wet Concrete (gravity, slows players, cures in ~2 min = 2400 ticks), Concrete, Reinforced Concrete, the **mold pour** (flood fill bottom-up, stops at leaks), Sledgehammer recycling (concrete → gravel, reinforced → gravel + rebar).
**Learn:** block entities, block states, scheduled ticks, falling blocks, a flood fill algorithm, hopper input. Maybe our first **unit test (birim testi)** for the flood fill.

**Discuss before starting:**
- Exact recipes and mixer capacity
- Strength numbers (concrete ≈ stone, reinforced ≈ ? blast resistance)
- Unit tests for the flood fill: yes/no?
- **From the Phase 1 review:**
  - **Give the Hard Hat a job.** Vanilla falling sand/gravel deal no damage, so today the hat only helps against anvils and dripstone. Options: (a) falling wet concrete hurts a little (`setHurtsEntities`), (b) the Sledgehammer drops "debris" damage when you break blocks above your head (user idea, about 5–10 lines), (c) remove the Hard Hat. Both (a) and (b) use the `falling_block` damage type, so the hat blocks them automatically.
  - **Concrete Bucket reusable?** User: yes, probably. The chain (clay + bone meal → cement mix → smelt → cement → mixer → bucket) may feel long: play it, then decide.
  - **Mixer in 2 steps:** right-click input first, hopper input second. Hoppers work with any block entity that uses vanilla's `Container` (or `WorldlyContainer`) interface, so we may not need Fabric's Transfer API.
  - **Sledgehammer recycling via loot tables:** a `match_tool` condition ("broken with `siteworks:sledgehammer`") → gravel (+ rebar), otherwise the block drops itself. No Java code. Also add concrete + reinforced concrete to `sledge_breakable`.

**Done when:**
- [ ] Full chain works in survival: clay + bone meal → cement → mixer → bucket
- [ ] Pouring into a closed mold fills bottom-up, max 16. An open mold says "The formwork leaks!" and wastes nothing
- [ ] Wet concrete cures in ~2 min, and falls if its support is removed
- [ ] Rebar inside → reinforced concrete survives a creeper
- [ ] No lag with ~200 blocks curing at once

**Decisions:** (2026-10-18, the user said "if I don't like one, we change it later")
- **Recipes:**
  - Cement Mix = shapeless clay ball + bone meal → 2 (or clay ball + calcite → 2), smelt → Cement (furnace or blast furnace). **2 steps kept** (user, 2026-10-18: "keep it for now"). If it feels boring in play, switch to crafting Cement directly.
  - Formwork ×4 = `PS / SP` (any planks + sticks, diagonal)
  - Rebar ×8 = `I / I / I` (3 iron ingots in a column)
  - Concrete Mixer = `.R. / ICI / I.I` (cauldron + 4 iron + 1 redstone)
  - **No new empty bucket:** the mixer fills a **vanilla bucket** → Concrete Bucket. Pouring gives the empty bucket back (reusable).
- **Mixer:** 1 batch = 1 cement + 4 sand + 4 gravel + water → 16 blocks. Holds 64 of each input + 64 blocks of ready concrete. 5 s per batch, sound + particles, no power. **Water both ways:** a water bucket = 8 batches, or a water source block next to it refills by itself. No GUI: right-click puts the held stack in, empty hand shows the contents in the action bar. Right-click input first, hopper input (`WorldlyContainer`, by item type) as a second step. No hopper output in v1.0.
- **Bucket:** remembers what's left (bar like durability). The mixer tops up half-full buckets.
- **Pour = like water:** starts at the lowest point, rises layer by layer until 16 are used or the mold is full to its lowest wall. Open first layer → "The formwork leaks!" (nothing used). Higher layer would spill → stops ("The mold is full."). Walls = any solid block. Air, water and plants are filled, so a hole in the ground is a mold too. Max **32×32 per layer of one mold** (not a building limit; bigger = leak). Checks spawn protection like the 3×3 tools.
- **Unit tests: yes** (JUnit 5, test-only, not in the jar). Claude writes and runs them.
- **Wet Concrete:** no item (only from a bucket), gravity, walking ×0.4, breaking drops nothing (rebar comes back), cures in 2400 ticks, the timer restarts after a fall.
- **Rebar:** a thin floating block. The pour fills rebar cells like air, and they cure into Reinforced Concrete.
- **Hard Hat job:** falling wet concrete hurts (1 per block fallen, max 10), the hat blocks it. **Plus** Sledgehammer ceiling debris (½ heart when you break a 3×3 ceiling without a hat) as an optional last step.
- **Strength:** Concrete = hardness 1.5 / blast 6 (stone), any pickaxe. Reinforced = hardness 5 / blast 1200 (obsidian-level), iron pickaxe or better. Formwork = hardness 0.5 (changed from instant after the step 1 test: it broke by accident), always drops itself.
- **Recycling (loot tables):** Sledgehammer without Silk Touch → concrete gives gravel, reinforced gives gravel + 1 rebar. Other tools or Silk Touch → the block itself. Both join `sledge_breakable`.
- **No structural collapse, no "foundation required" rule** in v1.0 (user agreed: rebar is a bonus, support matters only while wet). The user's "foundation required to start" idea is in `game-design.md` §5 as undecided.

**Steps:**
1. Simple blocks: Formwork, Rebar, Concrete, Reinforced Concrete (recipes, loot + recycling, tags)
2. Cement Mix + Cement
3. Wet Concrete: gravity, slow walking, curing, rebar, falling damage + the Hard Hat anvil test
4. Concrete Bucket + mold pour + unit tests ⭐ (tested with creative buckets first)
5. Mixer: right-click input, water, mixing, filling buckets
6. Mixer hopper input
7. (optional) Sledgehammer ceiling debris
8. "Done when" checklist (incl. the 200-block lag test) → status → commit

---

## Phase 3 — Masonry & frame (4–6 sessions)

**Goal:** fast, satisfying wall building and steel frames.

**Content:** Mortar (1 craft = 8 mortar, 1 mortar = 8 blocks), Trowel modes **Row 8 / Course 16** (sneak + right-click air to switch), placement preview with particles, `masonry_blocks` tag, Steel Ingot (blast furnace), Steel Beam (axis + extends up to 8 into the air).

**Discuss before starting:**
- Which blocks are "masonry" (the tag list)?
- Steel Beam recipe and look
- Trowel preview: particles OK for v1.0?

**Done when:**
- [ ] The trowel places up to 8/16 bricks per click and uses mortar correctly (64 blocks = 8 mortar)
- [ ] It never places blocks inside players/mobs or where vanilla wouldn't allow it
- [ ] Steel beam extends straight up to 8 and keeps its axis

**Decisions:**
- _(write answers here)_

---

## Phase 4 — Blueprints (5–7 sessions)

**Goal:** save buildings as blueprints.

**Content:** Measuring Tape (2 corners → selection outline + size in the action bar), Blank Blueprint, saving with vanilla **StructureTemplate** (blocks only, **no container contents**), Blueprint item (name, size, material list tooltip), Drafting Table screen (create / copy / rename).
**Learn:** data components, saved data, StructureTemplate, our first **GUI (Menu + Screen)**.

**Discuss before starting:**
- Where to store blueprint data (world saved data vs `.nbt` files in the world folder)
- Max size (32×32×32?)
- Cost of copying (1 paper?)

**Done when:**
- [ ] Select → save → the blueprint shows the correct size and material list
- [ ] Copy and rename work in the Drafting Table
- [ ] Blueprints survive a world restart
- [ ] A blueprint of a full chest copies **no items**

**Decisions:**
- _(write answers here)_

---

## Phase 5 — Projection ⭐ (5–8 sessions)

**Goal:** the hologram ghost, which makes building from blueprints fast.

**Content:** Survey Stake (holds a blueprint → spawns small glowing **Block Display** ghosts; correct = removed, wrong = red glow; rotate 90°; take back), Laser Level modes (Full / Layer / Footprint), max ~512 visible ghost blocks, cleanup of left-behind ghost entities, Trowel **Blueprint fill** mode (16 per click).

**Discuss before starting:**
- What counts as "correct": only the block type, or also the direction (stairs facing)?
- Who can remove someone else's stake on a server?
- **From the Phase 1 review:** the old Ghost Test entities (tag `siteworks_ghost_test`) stay in test worlds. The orphan cleanup must also remove this old tag, and the Ghost Test item must be removed or hidden before release.
- Ghost colors and size (e.g. 0.8 scale). **Phase 1 ghost test results (2026-10-12):**
  - **Performance OK:** 512 ghosts = 112–116 FPS, server 9.5 ms/tick (RTX 5060 laptop; weaker PCs may differ, so keep the 512 limit).
  - **Night OK:** full brightness makes ghosts stand out.
  - **Ghosts survive a world restart** (as expected) → cleanup is needed.
  - Problem 1: vanilla merges glow outlines of the same color into **one silhouette**, so a red "wrong" block **inside** a big ghost is hard to see. Idea: show wrong blocks as a **red stained glass** ghost (not only a red outline).
  - Problem 2: in daylight, 0.8-scale ghosts look almost like real blocks. Ideas: smaller scale (0.6–0.7), or no outline + glass-like look. The user was not sure. Decide here.

**Done when:**
- [ ] The ghost appears, updates while building, and disappears when the blueprint is taken back
- [ ] Rotation works, and Layer/Footprint modes work
- [ ] No ghost leftovers after a world restart or chunk reload
- [ ] Trowel Blueprint fill places only the missing matching blocks
- [ ] A 2nd player (LAN) sees the same ghost

**Decisions:**
- _(write answers here)_

---

## Phase 6 — Polish & balance (3–5 sessions)

**Goal:** make v1.0 feel complete and stable.

**Content:** **new looks for all our tools** (user's wish, 2026-10-12: own original textures/models instead of vanilla placeholders, e.g. the Mattock now looks exactly like an iron hoe; done when all tools exist), recipe balance pass, advancements (Solid Ground, Don't Strip Too Early, Iron Will, Architect, By the Book, Bridge Builder), full Turkish translation, vanilla sounds for actions, creative tab order, `runServer` test (no client code on the server), performance test, bug fixing.

**Discuss before starting:**
- How should the new tool looks be made (simple original pixel textures by script, Piskel by hand, Blockbench 3D models)? Blocks too, or only tools?
- Any balance problems found while playing?
- **From the Phase 1 review:** the 3×3 tools have no speed cost (9 blocks in the time of 1), and ring blocks harder than the center break for free (hit stone → deepslate around it breaks too). Test in play. Options: skip ring blocks harder than the center, and/or lower the mining speed (e.g. 4.0 instead of 6.0).
- `sledge_breakable` misses some blocks: smooth/cut/chiseled sandstone, red nether bricks, cracked/chiseled deepslate bricks and tiles, cracked nether bricks, polished blackstone variants, quartz, prismarine, purpur, packed mud, smooth basalt.
- Code cleanup: remove the empty mixin configs (`siteworks.mixins.json`, `siteworks.client.mixins.json` + the `"mixins"` entry in `fabric.mod.json`) if still unused. Merge `flatItem`/`handheldTool` in `ModModelProvider` into one helper.

**Done when:**
- [ ] A full survival playthrough (dig → foundation → frame → walls from a blueprint) works with no crashes
- [ ] The dedicated server starts and works with the mod

**Decisions:**
- _(write answers here)_

---

## Phase 7 — Release v1.0 (1–2 sessions, optional)

**Goal:** a jar that friends can install.

**Content:** `build` → test the jar in the normal Minecraft launcher with Fabric Loader + Fabric API, README, license, a Modrinth page (free).
⚠️ Publishing is public, so Claude asks before uploading anything.

**Decisions:**
- _(write answers here)_

---

## After v1.0

See `docs/game-design.md` §5:
- v1.1: Hand Truck
- then building services (pipes, wires, data, gas), optional inspection, finishing/furniture, Architect villager
