# Phases

## Current status

- **Phase:** 3½ — Review fixes, **done** ✅ (2026-10-19). Next: **Phase 3¾ — Foundations** (new v1.0 content, user 2026-10-19), then **Phase 4 — Blueprints, not started** (discuss the questions first, best in a new chat).
- **Phase 0 summary:** JDK 25 + IntelliJ + MC Dev plugin. Fabric template (26.3, datagen ON, split sources ON, Groovy). Loader 0.19.5, Loom 1.18.4 (pinned 2026-10-19, was 1.18-SNAPSHOT), Gradle 9.7.1. Harmless log errors: `Perflib 009` and `401 Failed to fetch user properties`. Game log: `run/logs/latest.log` (Claude can read it). Git: local + GitHub `origin/main`.
- **Phase 1 summary:** Mattock + Sledgehammer (3×3 via `AreaToolItem` + `AreaMining`), Hard Hat (`HardHatProtection`), Safety Net block (`SafetyNetBlock`), Ghost Test item (Block Displays). Datagen for all JSON. First class tweaker. **All built items are documented in `docs/items.md`.**
- **Phase 2 summary:** Formwork, Rebar, Concrete, Reinforced Concrete (Sledgehammer recycling via loot tables), Cement Mix → Cement, Wet Concrete (`WetConcreteBlock`: gravity + one scheduled tick for curing, falls directly in `neighborChanged` because Minecraft keeps only 1 scheduled tick per position), Concrete Bucket + the water-like mold pour (`pour/MoldPour`, pure Java, **14 JUnit tests**: `gradlew test`), Concrete Mixer (first **block entity**, right-click + hopper input via `WorldlyContainer`), Sledgehammer ceiling debris (1 heart, Hard Hat blocks it). Tests: creeper vs reinforced concrete ✅, 200 blocks curing at once = 12.1 ms/tick (13.0 before), 119 FPS ✅.
- **Phase 3 summary:** Mortar (1 Cement + 4 Sand → 8), Steel Ingot (iron ingot smelted, furnace or blast furnace), Steel Beam (`SteelBeamBlock` = `RotatedPillarBlock` + `facing`; lying = full I-beam you walk on, standing = slim column in vanilla `#climbable`, climbed inside like a ladder; `SteelBeamItem` extends the line up to 8 ahead like scaffolding via `updatePlacementContext`; 7 steel → 16), Trowel (`TrowelItem`: Row "from → to" = 2 clicks on brick spots, inclusive, max 8, a wrong 2nd click becomes the new start; mortar charge 1 Mortar = 8 blocks kept on the item; every block via vanilla `BlockItem.place()`; `masonry_blocks` tag), client particle preview (`client/TrowelPreview`, white/yellow/green/red dust, client tick event). **Dropped by the user:** Course mode (step 4), the climbing mixin (replaced by the slim `#climbable` column). New data components: `trowel_mode` (only `row`), `mortar_charge`, `row_start`, `row_block`. Lessons: write every step report and discussion in the chat; ask back when an answer is unclear; only the user marks tests as passed (all in `docs/rules.md`).
- **Code layout:** `registry/` (ModItems, ModBlocks, ModBlockEntities, ModDataComponents, ModTags, ModCreativeTabs), `item/`, `block/`, `block/entity/`, `pour/` (pure logic, unit tested), `foundation/` (`Foundation` pure + unit tested, `FoundationLookup`), `event/`, `util/` (`Messages`: red action bar messages) in `src/main/java/.../siteworks/`. Client-only code (`SiteWorksClient`, `TrowelPreview`, `PourPreview`, `PreviewParticles`) in `src/client/java/.../client/`. No mixins (the mixin configs are empty). Unit tests in `src/test/java/`. Hand-written models (custom shapes) in `src/main/resources/assets/siteworks/models/block/`. Datagen providers in `src/client/java/.../client/datagen/`. Output in `src/main/generated/` (commit it, because CI doesn't run datagen).
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
- **Next step:** Phase 3¾ (Foundations): discussion done (2026-10-19). Step 1 (formwork rule) done ✅ (tested + committed 2026-10-19). Step 2 (finding foundations + right-click info) done ✅ (tested + committed 2026-10-24). Now step 3 (explosion + Wither protection). After that the Phase 4 discussion (where to store blueprints, max size, cost of copying). Run `gradlew test` before each commit.
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
- [x] Full chain works in survival: clay + bone meal → cement → mixer → bucket (tested in steps 2 + 5)
- [x] Pouring into a closed mold fills bottom-up, max 16. An open mold says "The formwork leaks!" and wastes nothing (step 4 + 14 unit tests)
- [x] Wet concrete cures in ~2 min, and falls if its support is removed (step 3)
- [x] Rebar inside → reinforced concrete survives a creeper (poured over rebar, cured, ignited creeper next to it: survived)
- [x] No lag with ~200 blocks curing at once (`/tick query`: 13.0 ms/tick before, 12.1 ms after all 200 cured in the same tick, P99 23.9 ms; target 50 ms; 119 FPS)

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
- **Hard Hat job:** falling wet concrete hurts (1 per block fallen, max 10), the hat blocks it. **Plus** Sledgehammer ceiling debris when you break a 3×3 ceiling without a hat: **1 heart** (planned ½ heart, raised after the step 7 test: "it could lose more").
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

**Content:** Mortar (1 craft = 8 mortar, 1 mortar = 8 blocks), Trowel **Row 8** ("from → to"; ~~Course 16~~ dropped), placement preview with particles, `masonry_blocks` tag, Steel Ingot (blast furnace), Steel Beam (axis + extends up to 8 into the air).

**Discuss before starting:**
- Which blocks are "masonry" (the tag list)?
- Steel Beam recipe and look
- Trowel preview: particles OK for v1.0?

**Done when:**
- [x] The trowel places up to 8 bricks per row and uses mortar correctly (64 blocks = 8 mortar) (steps 3 + 3b: 8 bricks = 1 mortar, a partial charge is kept in the trowel, exact "from → to" rows; ~~16~~ Course mode was dropped)
- [x] It never places blocks inside players/mobs or where vanilla wouldn't allow it (step 3 test: the row stops in front of the player; every block goes through vanilla `BlockItem.place()`, plus spawn protection / world border / adventure mode checks)
- [x] Steel beam extends straight up to 8 and keeps its axis (step 2 bridge screenshot, step 2b column test; plus 16 per craft and climbable standing columns)

**Decisions:** (2026-10-18, the user: "ok for now, maybe we change it later")
- **Masonry tag** `siteworks:masonry_blocks`, **full blocks only:** bricks, all stone bricks (mossy, cracked, chiseled), mud bricks, deepslate bricks + tiles, tuff bricks, nether bricks, red nether bricks, end stone bricks, polished blackstone bricks, quartz bricks, resin bricks, cobblestone + mossy cobblestone, our Concrete + vanilla colored concrete. Stairs, slabs and walls are placed by hand (copying their direction is tricky).
- **Mortar:** shapeless **1 Cement + 4 Sand → 8 Mortar** (the mixer's ratio). Look: `item/clay_ball`.
- **Trowel:**
  - **Block:** places the block you click (copies the wall). A masonry block in the **offhand** overrides it. Takes blocks from the whole inventory.
  - **Mortar charge:** 1 Mortar = 8 blocks. The trowel stores the leftover charge (data component, shown as an item bar), so nothing is wasted.
  - **Row (8):** ~~click a face → up to 8 blocks go straight out from that face~~ → **changed in step 3b (2026-10-19): "from → to"**. 1st click = the start block, 2nd click = where the row ends → exactly that many blocks (1–8; user chose to keep max 8). Reason: the old Row always tried 8, so shorter rows needed breaking blocks. **Fixed after the user's video (2026-10-19):** both clicks mark brick **spots** (in front of the clicked face), the row is start → end **inclusive**, a wrong 2nd click becomes the **new start** (never stuck), sneak + air cancels a start. Block: other hand → masonry of the 1st click → masonry of the 2nd click.
  - ~~**Course (16):** click a face → every connected matching block on that face gets one new block in front of it~~ → **dropped (user, 2026-10-19):** the "from → to" Row is useful enough. Removed from the code; an old Course trowel loads as Row.
  - **Sneak + right-click air:** ~~switch mode~~ → cancels the row start (only one mode now; Phase 5 may add Blueprint fill + a mode switch).
  - **Rules:** vanilla placement checks for every block (no blocks inside players/mobs, spawn protection, adventure mode). Creative = free. Out of blocks/mortar → places what it can + "Out of mortar!".
  - **Recipe** `.I / S.` (1 iron ingot + 1 stick, diagonal). **No durability** (mortar is the cost). Look: `item/iron_shovel`.
- **Preview:** **particles** on the target spots while holding the trowel and looking at a block. Real outlines maybe in Phase 6.
- **Steel Ingot:** smelt an **Iron Ingot** in a **furnace or blast furnace** (user: "it could be a normal furnace" too). Look: `item/netherite_ingot`.
- **Steel Beam:**
  - Recipe `SSS / .S. / SSS` (7 steel) → **16 beams** (one craft = two 8-long bridges). Was 8, changed after the step 2 talk (user: more generous, "it's a game, it shouldn't be boring"). Copper was discussed but not used (copper can't make steel).
  - **Climbing (step 2b, user idea):** a **standing** beam is a **slim column at the far side of its block** (with a `facing`), in vanilla's `#climbable` tag, so you climb **inside** it like a ladder. No mixin. Mobs can climb it too (like ladders). History: the first try was a mixin (climb on the outside of the full I-beam, players only). Claude misread the user's answer, and in the test you got stuck under the next floor's beam, so it was replaced the same day. The look may change later.
  - Look: an **I-profile shape** with the `block/iron_block` texture, axis like logs. Hitbox = the same shape (the top is flat).
  - **Extending:** right-click a beam with a beam → the new beam goes to the **end of that beam line** (searches up to 8 ahead). Sneak = normal placement. No physics, beams never fall.
  - Strength like an iron block: hardness 5, blast 6, stone pickaxe or better, drops itself. Not in `sledge_breakable`.

**Steps:**
1. Mortar + Steel Ingot + Steel Beam (items, block, I-shape, recipes, tags, loot)
2. Steel Beam extending
   - 2b. 16 beams per craft + climbing standing beams (slim column, `#climbable`)
3. Trowel: Row mode + mortar charge + mode switch
   - 3b. Row becomes "from → to" (2 clicks, exact length, max 8)
4. ~~Trowel: Course mode~~ (dropped by the user)
5. Trowel particle preview
6. "Done when" checklist → status → commit

---

## Phase 3½ — Review fixes (1–2 sessions)

**Why:** a full code review after Phase 3 (2026-10-19). No crash bugs and no item duplication were found, but some things feel broken to the player. The user agreed to the list. Claude decided the order (the user asked for it).

**Decisions:** (2026-10-19)
- **Do now (before Phase 4):**
  - The hand "dips" (re-equip animation) every time the Trowel or Concrete Bucket data changes → `allowComponentsUpdateAnimation` = false.
  - Mixer + red sand: the sand slot keeps one kind (hoppers need real item stacks), so we show a message instead of failing silently ("The sand slot has Sand. Use it up first."). In real life any clean sand works, and that's still true: just not both kinds at the same time.
  - **Safety Net burns** (user: "it could burn"), like wool (Fabric `FlammableBlockRegistry`).
  - Mixer: save (`setChanged`) only when something really changes, not every tick.
  - One shared red-message helper (now copied in `TrowelItem` and `SteelBeamItem`).
  - Old comments and docs (`ModItems` "8 beams", `ModDataComponents` "Row / Course", `items.md` "changes the mode", `research.md` versions, README + template comments).
  - Pin Loom `1.18-SNAPSHOT` → `1.18.4` (a snapshot can change overnight and break the build).
  - `items.md`: the Trowel must be in the **main hand** (bricks in the off hand). With the trowel in the off hand, the main hand places one normal brick first (vanilla order).
  - Keep the unused `trowel_mode` component: Phase 5 needs it again, and removing a component type can break old saved trowels.
  - **Comparator output** on the mixer = how much ready concrete is inside (0–15).
  - **Pour preview** for the Concrete Bucket (like the trowel preview, client only, the same `MoldPour` code): gray dots where the concrete will go, a **red dot at the hole** if it leaks. Plus clearer messages: clicking the top of a wall or a full mold no longer says "The formwork leaks!". Protected empty cells (spawn protection, below the world) stop the pour instead of acting as a floor.
- **Later:** shared "can I build here?" helper + a pure `RowPlan` class (start of Phase 5, Blueprint fill needs both), gravel ×4 from recycling (Phase 6 balance), claim mods (Phase 7).
- **Leave:** wet concrete placed in the air cures at once if a block is put under it within 2 ticks (very rare, documented).
- **Testing (Claude decided, user: "you decide"):** every new pure-Java logic gets unit tests (the leak position now, `RowPlan` in Phase 5). Fabric **GameTest** (automatic tests in a real world, part of Fabric API): not now, check again in Phase 6 before the full playthrough, when re-testing everything by hand gets long.

**Steps:**
- R1. Quick fixes: hand animation, red sand message, Safety Net burns, mixer saving, message helper, old comments/docs, pin Loom, `items.md` notes — ✅ done, tested in-game (2026-10-19)
- R2. Mixer comparator output — ✅ done, tested in-game (2026-10-19)
- R3. Pour preview + red leak dot + clearer pour messages + protected cells + new `MoldPour` unit tests — ✅ done, tested in-game (2026-10-19), 20 unit tests ✅

**Done when:**
- [x] The trowel and the bucket no longer dip when used (R1)
- [x] Red sand into a sand-filled mixer shows a message (R1)
- [x] Fire burns a Safety Net (R1)
- [x] A comparator next to the mixer shows the ready concrete (R2)
- [x] The bucket shows where the concrete goes, and a red dot at the hole of a leaking mold (R3)
- [x] Clicking the top of a wall or a full mold gives a correct message (R3)

---

## Phase 3¾ — Foundations (2–3 sessions)

**Goal:** the Foundation rule (`game-design.md` §3.8): Reinforced Concrete on firm ground makes the blocks above it explosion-proof, up to a height limit (hard stop). Added to v1.0 by the user on 2026-10-19. Right after Phase 3½ because the concrete code is fresh, it doesn't depend on Blueprints, and the R3 pour preview can also warn "soft ground: no foundation".

**Learn:** our first **mixin** (Fabric has no explosion event), a fast "column scan" (look down from a block to find its foundation).

**Already decided (2026-10-19):** Reinforced Concrete only · on firm ground only · protects its "shadow" (blocks directly above) · thicker = taller · hard stop above the limit, only in a foundation's shadow (user: "maybe my idea changes later") · replaces the column rule and Cracked Concrete.

**Discuss before starting:**
- The numbers: layers → height (e.g. 1 → 8, 2 → 16, 3 → 32)? Max foundation thickness?
- ~~Which explosions?~~ **Decided (user, 2026-10-19):** TNT **and** explosive mobs (creepers and the like) don't damage a protected building. Players and mobs still get hurt as normal. Open detail: the Wither also breaks blocks by touching them (not an explosion); does the protection cover that too?
- Firm ground: exactly which blocks (stone types, gravel, concrete, deepslate...)? Is the bottom layer checked only, or every cell?
- How does the player **see** the protected area and the limit (message when the foundation cures, particles, Hard Hat view...)?
- Does the foundation need a minimum size (e.g. 3×3), so a single block can't protect a tower?
- Do explosions next to the foundation (from below, from the side) damage the foundation itself? (Reinforced Concrete is already blast resistance 1200.)
- The hard stop and other players / pistons / Endermen / falling blocks: which placements are stopped?
- Unit tests for the pure "shadow + thickness" logic. Phase 7: an on/off switch for PvP servers?

**Done when:**
- [ ] A creeper and TNT next to a protected house break no protected blocks
- [ ] The same house without a foundation (or on dirt) is damaged like vanilla
- [ ] Placing above the limit is stopped with a message; a thicker foundation allows it
- [ ] No lag with a big TNT explosion near a protected base

**Decisions:** (2026-10-19, discussion round 1)
- **Formwork rule (new, user):** the **sides** of a pour must be **Formwork**, not soil and not stone either ("use only formwork for every wet concrete"). Also OK at the side: **our concrete** (wet, cured, reinforced: big slabs are poured in parts, like a real construction joint / döküm derzi) and **Steel Beams** (a column standing in the mold). Everything else at the side (soil, stone, wood, bricks, glass...) = "Wet concrete needs formwork here!". The **floor** can be any solid block (round 2, user OK).
- **Height:** every foundation layer = **8 blocks** of building height. Layers needed = height ÷ 8, rounded up: up to 8 high = 1 layer, 9–16 = 2 layers (e.g. 5 high → 1, 12 high → 2), 17–24 = 3...
- **Measured per column** (each x/z spot, straight down). The foundation layers are **Reinforced Concrete only**. The building's height = how many blocks above the foundation top, **whatever they are made of** (bricks, wood, glass... all count, and all are protected) (round 2, user).
- **No minimum size.**
- ~~**Firm ground** = every solid block the Mattock can't dig, plus gravel.~~ → **Changed (user, 2026-10-24): any solid block under the foundation is OK** (dirt, grass, sand too), because digging down to stone is boring. Only a stack with nothing solid under it (air, water...) is not a foundation. "Firm ground" is saved as an idea in `game-design.md` §5. Known and accepted: a Reinforced Concrete floor on top of walls counts as a new foundation (the height starts again from there).
- **Info:** right-click Reinforced Concrete with an empty hand → "Foundation: 2 layers on firm ground → up to 16 blocks high" / "Not a foundation: ...". (User: OK for now; building planning may work differently later, e.g. with blueprints.)
- **Hard stop:** only blocks placed by **players** (hand, Trowel, Steel Beam, Bucket pour), in Survival and Adventure. **Creative ignores the limit.** Pistons, falling blocks, Endermen, growing trees, liquids are not stopped.
- **The Wither breaking blocks by touching them: also blocked** in a protected building (user chose the alternative). Needs its own small mixin (the Wither's block breaking), next to the explosion mixin.
- **PvP servers:** a game rule to turn the protection off, later (Phase 7).

**Steps:**
1. The formwork rule in the pour (`MoldPour` + tests, message, red preview marker on the bad side block) — ✅ done, tested in-game (2026-10-19), 24 unit tests ✅
2. Finding foundations (pure logic, unit tested) + the right-click info on Reinforced Concrete — ✅ done, tested in-game (2026-10-24), 9 unit tests ✅
3. Explosion + Wither protection (our first mixins)
4. The hard stop + its message
5. "Done when" checklist → status → commit

---

## Phase 4 — Blueprints (5–7 sessions)

**Goal:** save buildings as blueprints.

**Content:** Measuring Tape (2 corners → selection outline + size in the action bar), Blank Blueprint, saving with vanilla **StructureTemplate** (blocks only, **no container contents**), Blueprint item (name, size, material list tooltip), Drafting Table screen (create / copy / rename).
**Learn:** data components, saved data, StructureTemplate, our first **GUI (Menu + Screen)**.

**Discuss before starting:**
- Where to store blueprint data (world saved data vs `.nbt` files in the world folder)
- Max size (32×32×32?)
- Cost of copying (1 paper?)
- **From the Phase 3 review (2026-10-19), plan for these traps:**
  - Tooltips run on the **client**, and the client can't read the server's blueprint files. So save the name, size and material list **on the item** (data components) when the blueprint is made.
  - `StructureTemplate` also saves block entity data (= chest contents). "Blocks only" doesn't happen by itself: the code must remove it. This is the "full chest copies no items" test.
  - Wet Concrete has no item: count it as Concrete / Reinforced Concrete in the material list.
  - Templates never change after saving, so a copy can share the same template ID. Copy and rename only change the item.
- **Cheap code preparation for later features (user: OK, 2026-10-19):** only small choices inside this phase, no extra phase. (1) The blueprint data gets a **version number**, so a later format (e.g. a Services view) can still read old blueprints. (2) The material list is a simple "block → count" list (works for pipes and plaster later). Don't build systems for v1.1+ now.
- **Gameplay question (opened 2026-10-19, user):** why build walls with the trowel, and why lay a foundation? The user wants "some basic rules for reality and fun", not too real and not boring. The candidates are in `game-design.md` §5 ("Light gameplay rules"). Inspection is dropped. Decide here, and write the answer into `game-design.md`.

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
- **From the Phase 3 review (2026-10-19):**
  - Start with 2 refactors that Blueprint fill needs: one shared "can I build here?" helper (Trowel, Beam, Bucket, 3×3 tools, Blueprint fill) and a pure, unit-tested `RowPlan` class (used by `TrowelItem` and `TrowelPreview`, so they always agree).
  - Ghost cleanup, 2 ways: (A) **check when loaded:** each ghost has the stake's ID; when a ghost loads, it checks if its stake still exists and still wants it, otherwise it deletes itself (also removes old Ghost Test leftovers). (B) **never saved:** the server only tells each player's game "draw a ghost here" (network packets), nothing is saved, so there is nothing to clean, but it needs much more networking code. Claude recommends A.
  - Footprint mode shows the foundation layer in its own color (user: OK, 2026-10-19).
  - **Cheap code preparation (user: OK, 2026-10-19):** (3) the ghost filter (Full / Layer / Footprint) is one small function, so a later "Services view" is just one more filter; (4) the "can I build here?" helper is used by every tool (also future pipes, the Hand Truck, and the Foundation hard stop); (5) the trowel keeps its mode system, so a Plaster/Paint mode can be added later.
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
- Mixer: the user said "sneak + right-click with sand" (should place the sand, vanilla behaviour) "didn't work out, or maybe I didn't meet the conditions". Unclear, re-check in play.
- **From the Phase 3 review (2026-10-19):** gravel ×4 from recycling (4 gravel → 16 concrete → 16 gravel with the Sledgehammer). Options: recycling gives less, or keep it. Fabric GameTest: yes or no (see Phase 3½ testing decision).
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
**From the Phase 3 review (2026-10-19), user: "we will consider later":** on servers, claim mods are bypassed. Only spawn protection and the world border are checked for trowel spots 2–8, every pour cell and the beam end spot. Decide if v1.0 needs a fix (the shared "can I build here?" helper from Phase 5 is the one place to change).

**Decisions:**
- _(write answers here)_

---

## After v1.0

See `docs/game-design.md` §5:
- v1.1: Hand Truck
- then building services (pipes, wires, data, gas), optional inspection, finishing/furniture, Architect villager
