# Phases

## Current status

- **Phase:** 0 — Setup, **done** ✅ (2026-10-12). Next: Phase 1 — Site basics, **not started** (discuss the questions first)
- **Last step done:** steps 1–2 done (2026-10-12). JDK 25 + IntelliJ + MC Dev plugin installed. Project generated with the Fabric template (26.3, datagen ON, split sources ON, Groovy) and unzipped into the repo root. Loader 0.19.5, Loom 1.18-SNAPSHOT, Gradle 9.7.1. Hello line: "Site Works loaded. Time to build!" First `gradlew build` = **BUILD SUCCESSFUL** (24 min, slow internet downloads, not an error). The Turkish letters in the folder path cause no build problem.
Step 3 done: IntelliJ opened the project, the "Minecraft Client" run config works, the log shows the hello line. Harmless log errors: `Perflib 009` (Turkish Windows perf counters) and `401 Failed to fetch user properties` (offline dev account). The game log is `run/logs/latest.log` (Claude can read it directly).
Step 4 done: `git init -b main` + first commit (local only). A creative world loads fine.
Pushed to GitHub (`origin`, branch `main`, the repo name stayed `siteworks_minecraft-mode`).
- **Next step:** discuss the Phase 1 questions (best in a new chat).
- **Template leftovers to clean in Phase 1:** `ExampleMixin.java`, `ExampleClientMixin.java` (and their entries in the `*.mixins.json` files). `.github/workflows/build.yml` runs a build on GitHub Actions after each push (free within GitHub's monthly limit for private repos).
- **Open items:** the template's `LICENSE` file and `fabric.mod.json` say **CC0** (template default, NOT our decision). Decide the real license in Phase 7. `contact` in `fabric.mod.json` still points to Fabric's example repo.
- **Notes for Claude:**
  - The user's mood can be low, so lead with ideas and recommendations, and the user guides with feedback.
  - The user hates **chores** (angarya) in gameplay. Every tool must be functional.
  - v1.0 scope is **locked**. New ideas go to `docs/game-design.md` §5 "Later".
  - The user has **no bought Minecraft account**. Dev testing (`runClient`, offline name "Player") works without one. For LAN tests, run two clients with different `--username`. Phase 7 "test in the normal launcher" needs another way (dev jar test, dedicated server, or a friend). Never suggest cracked launchers.
  - GitHub remote: `https://github.com/semihkayli/siteworks_minecraft-mode.git` (the user may rename it to `…-mod` later, then run `git remote set-url origin <new URL>`). Push only when the user asks.
  - Total estimate: **~28–43 work sessions** (2–3 h each). A guess, update it as we learn.

**How every phase works:** discuss → write decisions → small steps (each step: what changed, which files, how to test) → "Done when" checklist → update "Current status" → git commit.

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
- [ ] All 4 items are in our creative tab, craftable in survival, with en/tr names
- [ ] The Hard Hat blocks falling-anvil damage, and the Safety Net cancels fall damage
- [ ] Mattock digs 3×3 dirt but not stone, Sledgehammer digs 3×3 stone but not ores
- [ ] (Optional) Ghost test: glowing small blocks look OK

**Decisions:**
- _(write answers here)_

---

## Phase 2 — Concrete ⭐ (6–8 sessions)

**Goal:** the core mechanic. After this phase the mod is already fun.

**Content:** Cement Mix → Cement, Concrete Mixer (right-click + hopper), Concrete Bucket (16 blocks), Formwork (instant break), Rebar, Wet Concrete (gravity, slows players, cures in ~2 min = 2400 ticks), Concrete, Reinforced Concrete, the **mold pour** (flood fill bottom-up, stops at leaks), Sledgehammer recycling (concrete → gravel, reinforced → gravel + rebar).
**Learn:** block entities, block states, scheduled ticks, falling blocks, a flood fill algorithm, hopper input. Maybe our first **unit test (birim testi)** for the flood fill.

**Discuss before starting:**
- Exact recipes and mixer capacity
- Strength numbers (concrete ≈ stone, reinforced ≈ ? blast resistance)
- Unit tests for the flood fill: yes/no?

**Done when:**
- [ ] Full chain works in survival: clay + bone meal → cement → mixer → bucket
- [ ] Pouring into a closed mold fills bottom-up, max 16. An open mold says "The formwork leaks!" and wastes nothing
- [ ] Wet concrete cures in ~2 min, and falls if its support is removed
- [ ] Rebar inside → reinforced concrete survives a creeper
- [ ] No lag with ~200 blocks curing at once

**Decisions:**
- _(write answers here)_

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
- Ghost colors and size (e.g. 0.8 scale)

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

**Content:** recipe balance pass, advancements (Solid Ground, Don't Strip Too Early, Iron Will, Architect, By the Book, Bridge Builder), full Turkish translation, vanilla sounds for actions, creative tab order, `runServer` test (no client code on the server), performance test, bug fixing.

**Discuss before starting:**
- Any textures to replace now, or keep vanilla placeholders for v1.0?
- Any balance problems found while playing?

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
