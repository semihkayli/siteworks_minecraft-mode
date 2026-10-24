# Game Design — "Site Works" (working name)

_v3 — **v1.0 scope LOCKED** on 2026-10-11. v1.0 = **rough construction (kaba inşaat): Structure & Blueprints**. Pipes/wires and furniture come later. Change the scope only if the user asks._
_Technical research (versions, tools, Java) is in `docs/research.md`._

---

## 0. Decisions so far (from the user)

| # | Topic | Decision |
|---|---|---|
| 1 | Loop plan → build → (connect) → (inspect) → reward | ✅ OK |
| 2 | Data network | Cables (when services come) |
| 3 | Optional rules | Pump needed above 8 blocks ✅ · electric shock 🤔 maybe · fuse box ❌ |
| 4 | Scope | Rough construction first, no furniture, every tool **functional** |
| 5 | Architect villager | Maybe, future |
| 6 | Gas | Maybe, later |
| 7 | Name | Not important now |
| 8 | Drawings | Station like the crafting/cartography table |
| 9 | Assets | Vanilla textures as placeholders |
| 10 | **Service networks (pipes, wires, data)** | **Moved to "later"** (not in v1.0) |
| 11 | **No chores (angarya)** | No manual measuring, no random leaks, no mandatory inspection, no inventory-clutter items, no 2-click-per-block building |
| 12 | Site layout tools | Become **hologram tools** that project blueprints |
| 13 | Leaks (later, with pipes) | Only as a **logical result of a wrong connection**, never random wear |
| 14 | Wheelbarrow | Removed. Replaced by a **Hand Truck** that moves full chests |
| 15 | Trowel | Becomes a **Builder's Wand** that uses **mortar** and places many bricks at once |
| 16 | Curing time | **~2 minutes** ✅ (real feel, short wait) |
| 17 | Trowel limits | **Row 8 / ~~Course 16~~ (dropped in Phase 3) / Blueprint fill 16**, **1 Mortar = 8 blocks** ✅ (one craft = 8 mortar = 64 blocks = 1 stack) |
| 18 | Hand Truck | **Moved to v1.1+**: item-duplication risk must be avoided in v1.0 |
| 19 | Ghost look | **Vanilla Block Displays** (slightly smaller glowing blocks) ✅ No custom rendering |
| 20 | More features (crane, insulation…) | ❌ **Scope locked.** A crane needs moving physics, and insulation is a hidden inventory chore |
| 21 | Inspection (Inspector's Clipboard) | ❌ **Dropped** (user, 2026-10-19: "I don't want to do this") |
| 22 | Light gameplay rules | (user, 2026-10-19): "some basic rules for reality and fun", but not too real and not boring. Result: the **Foundation rule** (row 23). Other candidates in §5 |
| 23 | **Foundation rule** | ✅ **In v1.0** (user opened the scope, 2026-10-19). Its own phase after Phase 3½. See §3.8 |

---

## 1. The big idea

> **Plan a building, project it into the world as a hologram, and build it fast with real construction methods: concrete molds, rebar, steel beams and a mortar trowel.**

### What is the "ghost preview" (hayalet önizleme)?
It's a **see-through copy of a saved building, shown in the world**, like tracing paper (aydınger) over the land.
- **See-through blue blocks:** "put a block here".
- **Red blocks:** "this block is wrong".
- **Gone:** correct blocks disappear from the ghost.

You don't need to measure or count anything. You just fill in the ghost. The popular mod Litematica does this, and players love it.

### Core gameplay loop (v1.0)
```
 1. DESIGN            2. SAVE                 3. PROJECT              4. BUILD                 5. REWARD
 build it once     →  Measuring Tape       →  Survey Stake shows   →  Trowel / concrete     →  advancements,
 (or get a copy)      selects it, Drafting    the ghost anywhere      molds / steel beams      a finished building,
                      Table makes Blueprint                           fill the ghost fast      share blueprints
```

---

## 2. Design rules (pillars)

1. **Every tool is functional.** No "for show" items.
2. **No chores (angarya).** If an action is boring and repeated, the tool must do it for you or remove it.
   - The grid is already 1×1, so there is no manual measuring.
   - Systems work immediately, with no random failures.
   - Inspection is never mandatory.
3. **Consequences, not punishment.** Bad things happen only when the player makes a clear, visible mistake. Example: removing support under wet concrete makes it fall.
4. **10-second rule.** Every item is understood in 10 seconds.
5. **Vanilla first.** Vanilla materials, vanilla textures (placeholders), vanilla blocks when they already exist (scaffolding).
6. **Avoid GUIs** except the Drafting Table. Use right-click interaction (composter-style).

---

## 3. v1.0 content

### 3.1 Blueprints — Drafting Table, Measuring Tape, Blueprint

**Measuring Tape (şerit metre) = the selection tool.** It is repurposed. There is no manual measuring.
- Right-click corner 1, then corner 2 → a **selection box hologram** appears. Its size is shown for free in the action bar (e.g. `12 × 8 × 15`).
- With a **Blank Blueprint** in the other hand, right-click → the selected build is **saved** into a Blueprint.
- Max size: 32×32×32 (tunable).

**Drafting Table (çizim masası)** = cartography-table-style screen (2 inputs → 1 output):

| Input 1 | Input 2 | Output |
|---|---|---|
| Paper | Lapis lazuli (blue ink) | **Blank Blueprint** |
| Blueprint | Paper | **Copy** (share with friends) |
| Blueprint | Name tag | **Rename** |

**Blueprint (plan)** – item. Its tooltip shows the name, size and **material list** ("bricks ×210, concrete ×96…").

---

### 3.2 Projection tools (hologram) — the old "site layout" tools, repurposed

| Tool | Functional effect |
|---|---|
| **Survey Stake (kazık) = the projector** | Place a stake and right-click it with a Blueprint → the **ghost of the building appears** there. Right-click with an empty hand = **rotate 90°**. Sneak + right-click = **take the blueprint back** (the ghost disappears). Every player sees the ghost, so it's great for building together. You can place many stakes = many projects |
| **Laser Level (lazer nivo) = layer control** | Right-click a stake with it to cycle 3 modes: **Full** (whole ghost) → **Layer** (only one floor/Y-level, use the laser to move up/down) → **Footprint** (only the outline on the ground, so you know where to dig the foundation). Layer mode keeps big projects clean and helps performance |
| **Measuring Tape** | Selection for saving (see 3.1) |

---

### 3.3 Excavation and demolition (hafriyat ve yıkım)

| Tool | Functional effect |
|---|---|
| **Mattock (kazma)** | Digs **3×3** but only **soft ground** (dirt, sand, gravel, clay, mud). Sneak to dig 1 block |
| **Sledgehammer (balyoz)** | Breaks **3×3** of stone-type blocks (stone, bricks, concrete), but not ores. **Concrete → gravel** (recycling), **reinforced concrete → gravel + rebar back** |

---

### 3.4 Concrete (beton) — ⭐ the heart of the mod

**Materials chain:**
```
clay ball + bone meal (or calcite)  →  Cement Mix  →  smelt  →  Cement
Concrete Mixer  +  cement + sand + gravel + water  →  fills Concrete Buckets
```

| Block / item | Functional effect |
|---|---|
| **Concrete Mixer (betoniyer)** | No GUI. Right-click with ingredients, or feed it with a **hopper** (automation = no chore). It mixes by itself in a few seconds. Right-click with an empty Concrete Bucket to fill it |
| **Concrete Bucket (beton kovası)** | Pours **16 blocks** of wet concrete into a mold |
| **Formwork (kalıp)** | Cheap wooden panel. **Breaks fast (not by accident) and always drops itself.** It's the wall of a mold |
| **Rebar (inşaat demiri)** | Thin bars. Place them inside the mold before pouring |
| **Wet Concrete (taze beton)** | **Has gravity** like sand. Cures in ~2 minutes (tunable), and you can keep building while it cures |
| **Concrete (beton)** | Strong gray block (like stone) |
| **Reinforced Concrete (betonarme)** | Cured **with rebar inside**: very high blast resistance (creeper-proof) |

**The mold rule:**
1. Build a shape with Formwork (the sides must be Formwork, our concrete or Steel Beams since 2026-10-19; the floor can be any solid block). Add Rebar inside if you want.
2. Pour from the top → the mold **fills from the bottom up** (up to 16 blocks per bucket).
3. If the mold is open, the pour stops: *"The formwork leaks!"*. Nothing is wasted.
4. Wait for curing, then break the formwork (instant).
5. ⚠️ Remove the support **under** wet concrete → it falls. This is a logical consequence, not a random punishment.

**Works with the hologram:** if you pour concrete into a mold inside a ghost area, the ghost shows correct concrete blocks as done.

---

### 3.5 Masonry — Trowel = Builder's Wand (mala)

**Mortar (harç)** = sand + cement (crafting table) → 8 Mortar. **1 Mortar = 8 masonry blocks.**

**Trowel (mala):** hold bricks (or any masonry block) in your hotbar and click a wall with the trowel. The trowel uses mortar from your inventory automatically, so you never click twice per block.

| Mode (sneak + right-click air to change) | What it places |
|---|---|
| **Row (sıra)** | "From → to": click where the row starts, then where it ends → up to **8 blocks** in a straight line (Phase 3) |
| ~~**Course (duvar katmanı)**~~ | **Dropped** in Phase 3 (user: the "from → to" Row is enough) |
| **Blueprint fill (plana göre)** ⭐ | Places the **missing ghost blocks** on that wall that match the block in your hand, up to **16 per click**. Building from a blueprint becomes fast and satisfying |

- A **preview outline** shows where the blocks will go before you click.
- **Masonry blocks only** (bricks, stone bricks, mud bricks, concrete blocks, tag-based). For wood and other blocks, players build normally or use other methods.
- In survival, it uses your real blocks plus mortar. It is never free.

---

### 3.6 Structural frame (karkas)

- **Concrete frame:** made with the molds (columns, beams, slabs).
- **Steel Beam (çelik kiriş, I-profil):**
  - Made from **Steel Ingot** = an iron ingot smelted with coal (furnace or blast furnace). 7 steel → **16 beams**.
  - **Functional:** right-click the end of a beam with another beam → it **extends straight into the air** (max 8), like scaffolding. Great for bridges, roof frames and gaps.
  - **Climbable columns:** a standing beam works like a ladder for players, so the same beam that frames the next floor also takes you up there (user idea, Phase 3).
  - Has an axis (X/Y/Z) like logs.
- **Wood frame:** vanilla logs and planks.

---

### 3.7 Site safety and logistics (iş güvenliği ve lojistik)

| Item / block | Functional effect |
|---|---|
| **Hard Hat (baret)** | No damage from falling blocks (anvil, dripstone, sand, gravel, falling wet concrete) |
| **Safety Net (güvenlik ağı)** | Catches falling players and mobs: **no fall damage**, small bounce |
| ~~**Hand Truck (yük arabası)**~~ → **v1.1+** | Replaces the wheelbarrow. Right-click a **full chest or barrel** → you carry it **with all its items**, and right-click again to place it somewhere else. You move slowly while carrying. Great for moving your storage when the site changes. Only vanilla chests, barrels and our own blocks, to avoid bugs with other mods |
| **Scaffolding** | Vanilla, no new block |

### 3.8 Foundation (temel) — added to v1.0 on 2026-10-19

The user's idea: you never *have* to build a foundation, but a good foundation makes your building safe from explosions.

| | Rule |
|---|---|
| **No foundation** | Normal Minecraft: explosions damage the building |
| **What counts** | A layer of **Reinforced Concrete only** (user), standing on **any solid block** (stone, dirt, sand... changed 2026-10-24: "firm ground only" is an idea for later, §5). Floating (air or water under it) = not a foundation |
| **Reward** | Every block **directly above** the foundation (its "shadow") is **explosion-proof** up to the height limit: **TNT and explosive mobs (creepers and the like) don't damage it** (user, 2026-10-19), and **the Wither can't break it by touching** either. Players and mobs still get hurt as normal |
| **Formwork rule** (user, 2026-10-19) | Wet concrete needs **Formwork** at its sides: soil, stone, wood, bricks... don't hold it. Our concrete (to pour big slabs in parts) and Steel Beams also count as sides. The floor can be any solid block. This changes the §3.4 mold rule "any solid block" |
| **Height rule** | A thicker foundation allows a taller building: **every layer = 8 blocks** (up to 8 high = 1 layer, 9–16 = 2, 17–24 = 3...), measured per column (decided 2026-10-19). Real life: taller = heavier = bigger foundation (no single real ratio exists, so the numbers are a game choice) |
| **Hard stop** (user, "maybe my idea changes later") | A player **can't put a block on top of** a protected block that is already at the limit (rule A, 2026-10-24: the building can't grow taller), like the world height limit, with a message ("Foundation too thin for this height! Add a layer."). Blocks with no foundation under them, beside the house, or far above (e.g. over a deep bunker) are never limited. Creative ignores it. Claude's note: this may make players skip foundations; re-check in play |
| **Replaces** | the "tall columns need a foundation" rule and Cracked Concrete (round 2 ideas) |

- Shadow instead of "connected blocks": understood in 10 seconds, fast, and trees/terrain next to the house are never protected by accident.
- Code: Fabric has no explosion event → our first small **mixin** in `ServerExplosion`. Phase 7: maybe an on/off switch for PvP servers.

---

## 4. v1.0 item list (~26)

- **Blueprint:** Drafting Table, Measuring Tape, Blank Blueprint, Blueprint, Survey Stake, Laser Level
- **Tools:** Mattock, Sledgehammer, Trowel, Hard Hat
- **Concrete:** Cement Mix, Cement, Concrete Mixer, Concrete Bucket, Formwork, Rebar, Wet Concrete, Concrete, Reinforced Concrete
- **Masonry/frame:** Mortar, Steel Ingot, Steel Beam
- **Safety:** Safety Net
- **Advancements:**
  - *Solid Ground* (first pour)
  - *Don't Strip Too Early* (your wet concrete fell 😄)
  - *Iron Will* (reinforced concrete)
  - *Architect* (first blueprint)
  - *By the Book* (finish a whole blueprint with 0 red blocks)
  - *Bridge Builder* (8-long steel beam)

---

## 5. Later (not v1.0)

- **v1.1:** Hand Truck (move full chests/barrels), after careful duplication testing.

- **Building services (tesisat):** pipes, wires, data cables, gas. SOURCE → LINE → DEVICE.
  - Water needs a pump above 8 blocks.
  - Leaks happen **only at open or wrongly connected pipe ends** (logical, visible, fixable), never randomly.
  - The Multimeter shows problems.
  - Hollow bricks hide the lines.
  - The Blueprint gets a **Services view**.
- **Idea, undecided (user, 2026-10-18):** a foundation is required before construction can start. Not in v1.0: it's a rule players must check, and it's unclear how it fits with vanilla builds and blueprints.
- ~~**Inspection:** an optional Inspector's Clipboard with a ⭐ certificate.~~ **Dropped** (user, 2026-10-19).
- **Light gameplay rules, undecided (opened 2026-10-19, decide in the Phase 4 discussion).** The user wants a few rules "for reality and fun", not too real, not boring. Test for every rule: you can see it, it acts at once, and it never breaks finished work. Candidates (Claude's ideas):
  - ✅ **Footprint mode shows the foundation layer in its own color** (Phase 5): guidance, not a rule. User: OK.
  - ✅ **The Foundation rule** → moved to **§3.8** (v1.0, user 2026-10-19). It replaces the round 2 ideas ("tall columns need a foundation", Cracked Concrete).
  - ℹ️ **Creeper idea:** vanilla already does a logical version. Explosions lose power in blocks with high blast resistance, so a Reinforced Concrete outer wall also shields what's behind it. Teach it (tooltip / "Iron Will"), no new rule.
  - 🤔 Rebar holds wet concrete (like scaffolding's distance rule). User: not sure, maybe illogical.
  - ❌ Rejected by the user (2026-10-19): monsters can't spawn on concrete ("illogical"), watering helps curing ("not necessary").
  - ❌ Claude advises against: timed breaks or timed warnings for buildings (the player can be offline or away, chunks unload, a friend's build on a server breaks; tracking whole buildings over time is heavy), and "everything on a foundation is blast-proof" (a glass window or wood wall would be blast-proof too: hidden and odd).
- **Trowel later:** a Plaster/Paint mode for the finishing update (user's paint idea; in real life the trowel is also used for plaster).
- **Idea, later (user, 2026-10-24): "firm ground" for foundations.** A foundation would need firm ground under it (stone, a gravel bed / blokaj), not soft ground (dirt, sand, mud). Dropped from v1.0 because digging down to stone can be boring. It would also close a loophole: a Reinforced Concrete floor on top of brick walls counting as a new foundation.
- **Finishing (ince inşaat):** plaster, tiles, fixtures, furniture, comfort and the "Well Rested" buff.
- **Future:** Architect villager + house blueprint catalog, blueprint library (Creative → Survival), elevator, fire safety.
- **Never:** volts/litres/pressure math, structural collapse physics, item pipes/factories, random breakdowns, cranes (moving physics), insulation (hidden chore).

---

## 6. Coding challenges — re-evaluated

### The big change
In v2, blueprints were the "last and hardest" part. Now **3 tools depend on the ghost** (stake, laser level, trowel blueprint mode). So the hardest technology is **in the center**. We need a safer way to build it.

### How to show the ghost — 2 options

| | **A. Block Display entities (vanilla)** ✅ recommended first | B. Custom client rendering |
|---|---|---|
| What | Vanilla has **Block Display** entities (since 1.19.4) that *show* a block without being a block. Map makers use them for holograms. We spawn small, glowing ones where ghost blocks should be | We draw see-through blocks ourselves in the client render code |
| Code difficulty | ⭐⭐⭐ Server code only, no rendering code, a stable vanilla feature | ⭐⭐⭐⭐⭐ The rendering API changed a lot in 2026 |
| Multiplayer | Automatic, everyone sees it | We must send data to each client (networking) |
| Look | Not truly see-through. We use **slightly smaller blocks + a glow outline** (red glow = wrong) | Perfect see-through ghost |
| Performance | Each ghost block = 1 entity, which is heavy for big builds. **Layer mode** (Laser Level) + a limit (e.g. 512 visible) fixes this | Fast, even for big builds |

**Plan:**
1. Build **option A** first, in the "Projection" milestone.
2. Make a small **test (deneme / prototype)** in the first session of that milestone, to check how it looks and performs.
3. Only switch to B later if A isn't good enough.

### Other challenges

| Challenge | Risk | How we handle it |
|---|---|---|
| Wet concrete timers (many blocks curing) | Lag if each block has heavy logic | Use **scheduled ticks** (a light, built-in timer), not block entities |
| Mold pour (flood fill) | Huge or open molds | Limit to 16 per bucket and stop at leaks. It runs once per click |
| Trowel wand | Placing blocks illegally (inside players, protected areas), wrong item use | Use vanilla's normal block-placement logic for every block, and the preview outline shows targets |
| Trowel preview outline | Client drawing | Start with **particles** (easy), upgrade to outlines later |
| Blueprint saving | Big data, multiplayer sync | Use vanilla **StructureTemplate** (`.nbt`), store it in the world, the item only keeps an ID |
| Drafting Table screen | First custom GUI | Copy the cartography table's pattern (Menu + Screen) |
| Hand Truck | **Item duplication bugs** (eşya kopyalama) | Moved to v1.1 (decision 18) |
| Blueprints copying containers | A blueprint of a full chest must not copy items | Blueprints save **blocks only**, never container contents |
| Changing trowel mode with scroll | Scroll input is client-side, so it needs a network packet | Use **sneak + right-click air** to cycle modes (no networking needed) |
| New 2026 APIs | Old tutorials and Claude's memory can be wrong | Check docs.fabricmc.net and the generated Minecraft source every time |

### Difficulty table (1 = easy, 5 = very hard)

| Feature | Diff. |
|---|---|
| Hard Hat, Safety Net, Steel Ingot/Beam | 1–2 |
| Mattock, Sledgehammer | 2 |
| Cement chain, Formwork, Rebar, Concrete blocks, Wet Concrete (gravity + curing) | 2 |
| Mold pour (flood fill) | 3 |
| Concrete Mixer (+ hopper) | 3 |
| Mortar + Trowel Row mode | 3 |
| Hand Truck | 3 |
| Measuring Tape selection + Blueprint saving | 3–4 |
| Drafting Table screen | 3 |
| Projection with Block Displays + Laser Level modes | 3–4 |
| Trowel Blueprint-fill mode | 3 |
| *(Later)* Custom ghost rendering | 5 |

**Overall:** v1.0 is now **smaller and safer** than v2. The service networks (difficulty 4) moved out, and the ghost uses a vanilla feature (4–5 → 3–4).

### Milestones

The full phase plan is in **`docs/phases.md`** (Phases 0–7).

---

## 7. Assets

- **Now:** models point to **vanilla textures** (`minecraft:block/...`), so there's no art work, and it's legally safe because we don't copy Mojang files.
  - Wet concrete → `gray_concrete_powder`
  - concrete → `light_gray_concrete`
  - reinforced → `gray_concrete`
  - formwork → `spruce_planks`
  - rebar → `iron_bars`
  - steel beam → `iron_block`
  - mixer → `iron_block` + `cauldron` parts
- **Shapes** (stake, beam, rebar, net) are made in Blockbench with vanilla textures.
- **Later:** our own 16×16 textures, one by one. No pre-made packs needed.

---

## 8. Scope status

✅ All design questions are answered (see §0, rows 16–20). **The v1.0 scope is locked.** The user opened it once on 2026-10-19 for the Foundation rule (§3.8, §0 row 23).
New ideas go to §5 "Later". Small details (exact recipes, numbers) are decided inside each phase's "Discuss before starting".
