# Items — Site Works

_Every item, tool and block we have **built** (not only planned). Plans are in `docs/game-design.md`. This file shows what is **really in the code**._
_Rule: update this file in the same step as you add or change an item (see `docs/rules.md`)._

## Overview

| Item | ID | Phase | Status |
|---|---|---|---|
| Mattock (Kazma Çapa) | `siteworks:mattock` | 1 | ✅ tested in-game |
| Sledgehammer (Balyoz) | `siteworks:sledgehammer` | 1 | ✅ tested in-game (incl. Phase 2 ceiling debris) |
| Hard Hat (Baret) | `siteworks:hard_hat` | 1 | ✅ tested in-game (anvil + falling wet concrete, 2026-10-18) |
| Safety Net (Güvenlik Ağı) — block | `siteworks:safety_net` | 1 | ✅ tested in-game. Burning (Phase 3½ R1): ✅ tested in-game (2026-10-19) |
| Ghost Test (Hayalet Testi) — **TEST ITEM** | `siteworks:ghost_test` | 1 | ✅ tested in-game (512 ghosts = 112–116 FPS). Replaced by the Survey Stake in Phase 5 |
| Cement Mix (Çimento Karışımı) | `siteworks:cement_mix` | 2 | ✅ tested in-game |
| Cement (Çimento) | `siteworks:cement` | 2 | ✅ tested in-game |
| Concrete Mixer (Betoniyer) — block | `siteworks:concrete_mixer` | 2 | ✅ tested in-game (right-click, water, mixing, buckets, save, drops, hopper input). "Slot taken" message (Phase 3½ R1): ✅ tested in-game (2026-10-19). Comparator (R2): ✅ tested in-game (2026-10-19) |
| Concrete Bucket (Beton Kovası) | `siteworks:concrete_bucket` | 2 | ✅ tested in-game + 14 unit tests ✅. No hand dip (Phase 3½ R1): ✅ tested in-game (2026-10-19). Pour preview + aim help + new messages (R3): ✅ tested in-game (2026-10-19). Formwork rule (Phase 3¾ step 1): ✅ tested in-game (2026-10-19). 24 unit tests ✅ |
| Formwork (Kalıp) — block | `siteworks:formwork` | 2 | ✅ tested in-game. The only good mold side (Phase 3¾ step 1): ✅ tested in-game (2026-10-19) |
| Rebar (İnşaat Demiri) — block | `siteworks:rebar` | 2 | ✅ tested in-game |
| Concrete (Beton) — block | `siteworks:concrete` | 2 | ✅ tested in-game |
| Reinforced Concrete (Betonarme) — block | `siteworks:reinforced_concrete` | 2 | ✅ tested in-game. Foundation info (Phase 3¾ step 2): ✅ tested in-game (2026-10-24). Explosion protection (step 3): ✅ tested in-game (2026-10-24: TNT, creeper, limit). Wither (optional test): not confirmed. Hard stop (step 4): ✅ tested in-game (2026-10-24). Ground under the foundation protected (step 5 fix): ✅ tested in-game (2026-10-24) |
| Wet Concrete (Taze Beton) — block, **no item** | `siteworks:wet_concrete` | 2 | ✅ tested in-game (with commands) |
| Mortar (Harç) | `siteworks:mortar` | 3 | ✅ tested in-game (2026-10-18) |
| Trowel (Mala) | `siteworks:trowel` | 3 | ✅ tested in-game: mortar charge (step 3), Row "from → to" (step 3b), particle preview (step 5, 2026-10-19). Course mode dropped (step 4). No hand dip (Phase 3½ R1): ✅ tested in-game (2026-10-19) |
| Steel Ingot (Çelik Külçe) | `siteworks:steel_ingot` | 3 | ✅ tested in-game (2026-10-18) |
| Steel Beam (Çelik Kiriş) — block | `siteworks:steel_beam` | 3 | ✅ tested in-game: block, extending bridge (step 2), 16 per craft + climbable column (step 2b, 2026-10-19) |

**Creative tab:** "Site Works" (`siteworks:site_works`), icon = Hard Hat. Order: Hard Hat, Mattock, Sledgehammer, Safety Net, Cement Mix, Cement, Concrete Mixer, Concrete Bucket, Formwork, Rebar, Concrete, Reinforced Concrete, Trowel, Mortar, Steel Ingot, Steel Beam, Ghost Test.

**How to read the stats:**
- "Attack damage" and "attack speed" are the numbers in the item's tooltip.
- Code values are added to the player's base values: damage = 1 + material bonus + item value, speed = 4 + item value.

---

## Mattock (Kazma Çapa)

| | |
|---|---|
| ID | `siteworks:mattock` |
| Names | EN "Mattock", TR "Kazma Çapa" ("Kazma" alone is the vanilla pickaxe) |
| What it does | Digs **3×3 soft ground**, like an iron shovel |
| Recipe | Shaped, `XXX / X#. / .#.` with X = iron ingot (`#minecraft:iron_tool_materials`) and # = stick. The mirrored shape works too |
| Recipe book | Unlocks when the player gets an iron ingot |
| Tool type | Shovel, iron tier (mines what an iron shovel mines) |
| Durability | **500** (iron shovel = 250) |
| Mining speed | 6.0 (iron) |
| Attack | damage 4.5, speed 1.0 (same as the iron shovel) |
| Repair | Iron ingot in an anvil |
| Enchantable | Yes, enchantability 14 (iron). Efficiency, Fortune, Silk Touch, Unbreaking and Mending work, because the Mattock is in the vanilla tag `#minecraft:shovels` (fixed after the Phase 1 review; before that, nothing could be enchanted) |
| Look (placeholder) | Vanilla `item/iron_hoe` texture, held like a tool. A new look comes in Phase 6 |

**3×3 rules**
- The block you hit always breaks like a normal shovel hit.
- The 3×3 starts only if that block is in `#siteworks:soft_ground`.
- The 8 blocks around it break only if they are in `#siteworks:soft_ground` too.
- Plane: flat against the **face you hit**. Top or bottom face = a floor or ceiling, side face = a wall. (Fixed after the Phase 1 review. Before, it used the look direction, which turned the plane wrong at shallow angles.)
- **Sneak = 1 block only.**
- Every block broken costs 1 durability. A full 3×3 = 9 durability, so about 55 full swings.
- If the tool breaks or you switch items halfway, the digging stops.
- Uses vanilla's normal break (`destroyBlock`), so drops, enchantments and Fabric break events (claim mods) work. `destroyBlock` itself skips spawn protection and the world border, so we check `level.mayInteract()` for each extra block (fixed after the Phase 1 review).

**Tag `#siteworks:soft_ground`** = vanilla's shovel list **minus suspicious sand and suspicious gravel** (protects archaeology). It contains:
- grass block, dirt, coarse dirt, rooted dirt, podzol, mycelium, dirt path, farmland
- mud, muddy mangrove roots
- sand, red sand, gravel, clay
- soul sand, soul soil
- snow block, snow layer
- `#minecraft:concrete_powders`

**Code:**
- `ModItems.MATTOCK` = `AreaToolItem(SOFT_GROUND)` + `Item.Properties().shovel(ToolMaterial.IRON, 1.5f, -3.0f).durability(500)`
- 3×3 logic: `event/AreaMining.java`
- Tag key: `ModTags.SOFT_GROUND`
- Item tag: `ModItemTagProvider` puts the Mattock into `#minecraft:shovels` and the Sledgehammer into `#minecraft:pickaxes` (needed for enchanting)
- Datagen: `ModModelProvider`, `ModRecipeProvider`, `ModBlockTagProvider`, both lang providers

---

## Sledgehammer (Balyoz)

| | |
|---|---|
| ID | `siteworks:sledgehammer` |
| Names | EN "Sledgehammer", TR "Balyoz" |
| What it does | Breaks **3×3 stone-type blocks and masonry**, but **never ores** |
| Recipe | Shaped, `XXX / XXX / .#.` with X = iron ingot (`#minecraft:iron_tool_materials`) and # = stick |
| Recipe book | Unlocks when the player gets an iron ingot |
| Tool type | Pickaxe, iron tier (mines what an iron pickaxe mines, e.g. diamond ore, but not obsidian) |
| Durability | **500** (iron pickaxe = 250) |
| Mining speed | 6.0 (iron) |
| Attack | damage 7, speed 0.8 (a heavy, slow hit) |
| Repair | Iron ingot in an anvil |
| Enchantable | Yes, enchantability 14 (iron). Efficiency, Fortune, Silk Touch, Unbreaking and Mending work, because the Sledgehammer is in the vanilla tag `#minecraft:pickaxes` |
| Look (placeholder) | Vanilla `item/mace` texture, held like a tool. A new look comes in Phase 6 |

**3×3 rules:** the same as the Mattock (same `AreaToolItem` code), but with the tag `#siteworks:sledge_breakable`.
- If you hit an ore directly, it breaks normally, like with a pickaxe. Ores **around** the center are never broken.
- **Recycling (Phase 2):** concrete → gravel, reinforced concrete → gravel + rebar (loot tables, see Concrete / Reinforced Concrete).
- **Ceiling debris (Phase 2 step 7):** when you hit the **bottom face** of a block above you (a ceiling) and the 3×3 breaks **at least 1 extra block**, dust of the broken block falls down (vanilla `falling_dust` particles) and you take **2 damage (1 heart;** was ½ heart, the user wanted more after the test**)**, damage type `minecraft:falling_block`. **The Hard Hat blocks it** (and loses 1 durability). Not in creative mode (vanilla: no damage). Sneak (1 block) = no debris. The Mattock has no debris

**Tag `#siteworks:sledge_breakable`** contains:
- Vanilla tags:
  - `#minecraft:base_stone_overworld` (stone, granite, diorite, andesite, tuff, deepslate)
  - `#minecraft:base_stone_nether` (netherrack, basalt, blackstone)
  - `#minecraft:stone_bricks`
  - `#minecraft:terracotta`
  - `#minecraft:concrete`
- Blocks:
  - cobblestone, mossy cobblestone, cobbled deepslate, smooth stone
  - polished granite, diorite, andesite, deepslate, tuff
  - deepslate bricks, deepslate tiles, tuff bricks, bricks, mud bricks
  - sandstone, red sandstone, calcite, dripstone block
  - end stone, end stone bricks, nether bricks
  - polished blackstone, polished blackstone bricks

**Code:**
- `ModItems.SLEDGEHAMMER` = `AreaToolItem(SLEDGE_BREAKABLE)` + `Item.Properties().pickaxe(ToolMaterial.IRON, 4.0f, -3.2f).durability(500)`
- 3×3 logic + ceiling debris: `event/AreaMining.java` (shared with the Mattock; debris = `dropCeilingDebris`, `CEILING_DEBRIS_DAMAGE = 2`)
- Tag key: `ModTags.SLEDGE_BREAKABLE`. Vanilla tags are added with `forceAddTag`

---

## Hard Hat (Baret)

| | |
|---|---|
| ID | `siteworks:hard_hat` |
| Names | EN "Hard Hat", TR "Baret" |
| What it does | Helmet. **Cancels all damage from falling blocks:** falling anvil, falling dripstone, **falling Wet Concrete** (Phase 2), **Sledgehammer ceiling debris** (Phase 2 step 7), and any falling block that deals damage. Vanilla falling sand/gravel deal no damage |
| Recipe | Shaped, `XXX / XYX` with X = iron ingot and Y = yellow dye (the iron helmet shape with dye in the hole) |
| Recipe book | Unlocks when the player gets an iron ingot |
| Armor | **2** (same as an iron helmet), toughness 0, knockback resistance 0 |
| Durability | **165** (same as an iron helmet) |
| Wear | When it blocks falling damage, it loses **1 durability per 4 damage** (at least 1), like vanilla armor. A big anvil hit costs about 10. User agreed (2026-10-12) |
| Repair | Iron ingot in an anvil (`#minecraft:repairs_iron_armor`) |
| Enchantable | Yes, like an iron helmet: Protection, Respiration, Aqua Affinity, Unbreaking, Mending..., because the Hard Hat is in the vanilla tag `#minecraft:head_armor` |
| Equip sound | Iron armor sound |
| Look (placeholder) | The **golden helmet** look, on the head and in the inventory (yellow = hard hat). A new look comes in Phase 6 |
| Does NOT protect from | Suffocation (being stuck in sand/gravel). That's a different damage type, and vanilla helmets don't protect from it either |

**Code:**
- `ModItems.HARD_HAT` = `Item` + `humanoidArmor(HARD_HAT_MATERIAL, ArmorType.HELMET)`
- `HARD_HAT_MATERIAL` = the 7 values of `ArmorMaterials.IRON`, but `assetId = EquipmentAssets.GOLD`
- Protection: `event/HardHatProtection.java`. It uses the Fabric event `ServerLivingEntityEvents.ALLOW_DAMAGE` and checks the vanilla damage tag `#minecraft:damages_helmet`
- Item tag: `ModItemTagProvider` puts the Hard Hat into `#minecraft:head_armor` (needed for enchanting)
- Works for **any entity** wearing it (players, and mobs too)

---

## Safety Net (Güvenlik Ağı) — block

| | |
|---|---|
| ID | `siteworks:safety_net` (block + block item) |
| Names | EN "Safety Net", TR "Güvenlik Ağı" (lang key `block.siteworks.safety_net`) |
| What it does | Catches falling players and mobs: **no fall damage at all**, plus a **small bounce** |
| Shape | A **3-pixel net at the TOP of its block space** (pixels 13–16), like a trapdoor in the "up" position. Collision = that slab |
| Placement | Anywhere, with no support needed. It stays when the blocks around it are removed. Placed next to a floor, it's level with the floor top. Placed on the ground, it floats 1 block up like a small trampoline |
| Recipe | Shaped, `S.S / .S. / S.S` (5 string) → **4** Safety Nets |
| Recipe book | Unlocks when the player gets string |
| Bounce | `bounceRestitution(0.3)`, handled by vanilla (slime = 1.0, bed = 0.75). Vanilla stops the bounce while sneaking |
| Strength | 0.4 (breaks fast, even by hand), wool sounds, map color = wool |
| Fire | **Burns like wool** (Phase 3½ R1, user: "it could burn"): catches fire easily (30) and burns away fast (60), also from lava next to it. A burnt net drops nothing |
| Drops | Itself (also by hand). Not when destroyed by an explosion (vanilla `dropSelf`) |
| Look (placeholder) | Block = vanilla `template_trapdoor_top` model with the `block/cobweb` texture. The holes render correctly (26.x picks the cutout layer from the texture). Inventory icon = flat cobweb. A new look comes in Phase 6 |

**Why at the top? (design change, user agreed 2026-10-12)**
- The first plan was a 2-pixel pad on the floor of its block, but vanilla looks for the landing block **0.2 blocks below the feet** (for fall damage, `fallOn`) and **0.5 blocks below** (for the bounce).
- On a thin pad at the bottom, both checks find the block **under** the net. That's the same vanilla behaviour as "a carpet on a hay bale still reduces fall damage".
- With the net at the top, both checks find the net, so we need almost no custom code.

**Code:**
- `block/SafetyNetBlock.java`:
  - `getShape` = `Block.box(0, 13, 0, 16, 16, 16)`
  - `fallOn` does nothing, so there's no damage
- `registry/ModBlocks.java`: registers the block + `BlockItem`; `init()` registers the flammability (Fabric `FlammableBlockRegistry`)
- Datagen:
  - `ModModelProvider` (blockstate + `safety_net_top` model + flat item icon)
  - `ModBlockLootProvider` (`dropSelf`)
  - `ModRecipeProvider`
  - both lang providers

---

## Ghost Test (Hayalet Testi) — TEST ITEM

| | |
|---|---|
| ID | `siteworks:ghost_test` |
| Names | EN "Ghost Test (test item)", TR "Hayalet Testi (test eşyası)" |
| Why it exists | The Phase 1 "risk first" test of the hologram ghost idea (Block Display entities). **Not a real game item.** It will be removed or replaced when the Survey Stake comes in Phase 5 |
| How to get it | **Creative tab only.** No recipe |
| Stack / rarity | Stacks to 1, rarity Epic (purple name) |
| Look (placeholder) | Flat `item/amethyst_shard` icon |

**Controls**
- **Right-click a block:** spawns a hollow 3×3×3 demo "house" (26 ghosts) on the clicked face.
  - Floor = stone bricks, walls = bricks with glass windows, roof = oak planks.
  - 24 ghosts glow **blue** (`#55CCFF`, "place here") and 2 glow **red** (`#FF3333`, "wrong block").
- **Sneak + right-click a block:** spawns an **8×8×8 cube = 512 ghosts** of stone bricks. 512 is the planned max for one projection, so this is the lag test.
- **Right-click the air:** removes every test ghost within 128 blocks.
- Every action shows a message in the action bar ("Spawned 26 ghost blocks" / "Removed 512 ghost blocks").

**One ghost =** a vanilla Block Display entity with:
- scale 0.8, moved 0.1 inward so it sits in the middle of its block cell
- the glow outline on (`setGlowingTag`) with a custom glow color
- full brightness, so it's never dark at night or in caves
- the entity tag `siteworks_ghost_test`
- no hitbox, so you walk through it and it can't be hit

**Known on purpose:** the ghosts are saved with the world, so they are still there after a restart until you remove them. Phase 5 must clean up left-behind ghosts.

**Code:**
- `item/GhostTestItem.java`
- `ModItems.GHOST_TEST`
- `src/main/resources/siteworks.classtweaker`: makes the private `Display` setters usable (`setBlockState`, `setTransformation`, `setGlowColorOverride`, `setBrightnessOverride`). It's registered in `build.gradle` (`accessWidenerPath`) and in `fabric.mod.json` (`accessWidener`)

---

## Formwork (Kalıp) — block

| | |
|---|---|
| ID | `siteworks:formwork` (block + block item) |
| Names | EN "Formwork", TR "Kalıp" |
| What it does | The wall of a concrete mold. Cheap and reusable: it **breaks fast** and **always drops itself** (by hand too) |
| Recipe | Shaped, `PS / SP` (P = any planks `#minecraft:planks`, S = stick, diagonal) → **4**. The mirrored shape works too |
| Recipe book | Unlocks when the player gets any planks |
| Strength | Hardness **0.5** (like dirt): ~0.75 s by hand, ~0.15 s with an iron axe. Changed from 0 (instant) after the user test (2026-10-18): it broke too easily, even by accident. Wood sounds, map color = wood. Does not burn (not decided, can change later) |
| Drops | Itself (vanilla `dropSelf`) |
| Tags | `#minecraft:mineable/axe` |
| Look (placeholder) | Full cube with the vanilla `block/spruce_planks` texture (looks exactly like spruce planks for now). A new look comes in Phase 6 |
| Mold rule | **The mold sides must be Formwork** (Phase 3¾ step 1, user's formwork rule; before: any solid block). Also OK at a side: our concrete (pour big slabs in parts) and Steel Beams. Soil, stone, wood, bricks... at a side = "Wet concrete needs formwork here!". The floor can be any solid block |

**Code:** `ModBlocks.FORMWORK` (plain `Block` + `strength(0.5f)`). Datagen: `ModModelProvider.cubeBlock`, `ModBlockLootProvider`, `ModRecipeProvider`, `ModBlockTagProvider`, both lang providers.

---

## Rebar (İnşaat Demiri) — block

| | |
|---|---|
| ID | `siteworks:rebar` (block + block item) |
| Names | EN "Rebar", TR "İnşaat Demiri" |
| What it does | A small cage of steel bars. Place it inside a mold **before pouring**. The pour fills rebar cells like air, and they cure into **Reinforced Concrete** (comes in steps 3–4) |
| Recipe | Shaped, `I / I / I` (3 iron ingots in a column) → **8** (the same iron per piece as vanilla iron bars) |
| Recipe book | Unlocks when the player gets an iron ingot |
| Placement | Anywhere. It **floats** (needs no support) and stays when the blocks around it are removed |
| Shape | Outline + collision = one box, pixels 3–13 wide, full height. You can't walk through it |
| Strength | Hardness 1.0 (fast by hand, faster with a pickaxe), iron sounds, map color = metal |
| Drops | Itself, with any tool or by hand |
| Tags | `#minecraft:mineable/pickaxe` |
| Look (placeholder) | 4 thin vertical bars + 2 rings (stirrups / etriye), vanilla `block/iron_bars` texture. Hand-written model (datagen can't draw shapes). The item shows the same 3D model |

**Code:**
- `block/RebarBlock.java` (`getShape` = `Block.box(3, 0, 3, 13, 16, 13)`)
- `ModBlocks.REBAR`
- Model by hand: `src/main/resources/assets/siteworks/models/block/rebar.json`. The blockstate + item model come from datagen (`ModModelProvider`)
- Datagen: `ModBlockLootProvider`, `ModRecipeProvider`, `ModBlockTagProvider`, both lang providers

---

## Concrete (Beton) — block

| | |
|---|---|
| ID | `siteworks:concrete` (block + block item) |
| Names | EN "Concrete", TR "Beton" |
| What it does | A strong gray building block, like stone. In survival you get it **only by pouring** (wet concrete cures into it, steps 3–4). There is no crafting recipe. Once you have it, it's a normal block you can mine and place |
| Recipe | None (comes from curing). Creative tab for testing |
| Strength | Hardness **1.5**, blast resistance **6** (both like stone). Stone sounds, map color = light gray, note block = bass drum |
| Tool | Needs a **pickaxe** (any tier) to drop. `#minecraft:mineable/pickaxe` |
| Drops | Any pickaxe → **itself**. **Sledgehammer without Silk Touch → 1 gravel** (recycling). Sledgehammer with Silk Touch → itself |
| Tags | `#minecraft:mineable/pickaxe`, `#siteworks:sledge_breakable` (the Sledgehammer breaks it 3×3) |
| Look (placeholder) | Vanilla `block/light_gray_concrete` texture. ⚠️ It looks exactly like vanilla Light Gray Concrete for now. A new look comes in Phase 6 |

**Code:** `ModBlocks.CONCRETE` (plain `Block`). Loot: `ModBlockLootProvider.selfOrGravel` (an `alternatives` entry: gravel when `match_tool` = Sledgehammer and no Silk Touch, otherwise the block). Datagen: `ModModelProvider.cubeBlock`, `ModBlockTagProvider`, both lang providers.

---

## Reinforced Concrete (Betonarme) — block

| | |
|---|---|
| ID | `siteworks:reinforced_concrete` (block + block item) |
| Names | EN "Reinforced Concrete", TR "Betonarme" |
| What it does | Concrete with rebar inside. Mines like an iron block, and **creepers and TNT can't break it** |
| Recipe | None (wet concrete poured over rebar cures into it, steps 3–4). Creative tab for testing |
| Strength | Hardness **5** (like an iron block), blast resistance **1200** (like obsidian). Stone sounds, map color = gray, note block = bass drum |
| Tool | Needs an **iron pickaxe or better** to drop (`#minecraft:needs_iron_tool`). The Sledgehammer is iron tier, so it works |
| Drops | Pickaxe → **itself**. **Sledgehammer without Silk Touch → 1 gravel + 1 rebar** (recycling, you get the rebar back). Sledgehammer with Silk Touch → itself |
| Tags | `#minecraft:mineable/pickaxe`, `#minecraft:needs_iron_tool`, `#siteworks:sledge_breakable` |
| Look (placeholder) | Vanilla `block/gray_concrete` texture (darker than Concrete). A new look comes in Phase 6 |
| Known (Phase 6 balance note) | In a Sledgehammer 3×3, ring blocks harder than the center still break for free. Hit stone → reinforced concrete around it breaks too |
| Foundation (Phase 3¾ step 2) | A stack of Reinforced Concrete layers in one column that **stands on a solid block** = a **foundation**. Any solid block under it is OK: stone, dirt, grass, sand, gravel, bricks... (a block with a full top; changed 2026-10-24, user: digging to stone is boring). Nothing solid under it (air, water, a plant) = not a foundation. **Every layer = 8 blocks** of building height above its top (1 layer: up to 8 high, 2: 9–16, 3: 17–24...). A block's foundation = the first real foundation straight under it (through rooms and walls; a reinforced floor over a room is skipped). |
| Protection (Phase 3¾ step 3) | Every block **carried** by a foundation (directly above it, up to its limit) is **explosion-proof**: TNT, creepers, ghast fireballs, Wither skulls, beds, End Crystals... don't break it (and don't set fire there). **The Wither can't break it by touching** either. Players and mobs still get hurt as normal. **Also protected (step 5 fix, 2026-10-24): the one block directly under each foundation column** (the ground it stands on), because a chain of TNT first dug away the ground under the foundation's edge, that part stopped being a foundation, and the next explosions broke the walls above. Blocks beside the foundation, above the limit, or deeper under it are vanilla. Code: 2 mixins (`mixin/ServerExplosionMixin` takes protected blocks out of the explosion's list; `mixin/WitherBossMixin` wraps the Wither's `destroyBlock`), `FoundationLookup.removeProtected` / `isProtected`, fast per-column check `Foundation.protectedBlocks` + `holdsUp` (2 unit tests). Lag test (125 TNT next to a 16×16 base): 9.5 ms per tick, P99 27.5 ms (before: 8.7 / 12.4; limit 50) |
| Hard stop (Phase 3¾ step 4, user's rule A) | A **player** can't put a block **on top of** a protected block that is already at its foundation's limit (= the building can't grow taller than its foundation allows): the block isn't placed, red message "Foundation too thin for this height! Add a layer (max y 71)." Works for placing by hand, the **Trowel** (the row stops there), **Steel Beam** extending and the **Concrete Bucket** (the pour stops at that cell; the preview shows a red dot there). **Not stopped:** Creative mode, building beside the house, blocks that don't sit on the building (e.g. far above a deep bunker), pistons, falling blocks, Endermen, trees, liquids, dispensers. Known and accepted: a separate tower next to the house can bridge over the limit (those blocks aren't protected anyway). Code: `mixin/BlockItemMixin` (wraps `placeBlock` in `BlockItem.place`, client + server), `Foundation.tooTall` (3 unit tests), `FoundationLookup.refusedFor` / `tooTallMessage`, `TrowelItem.placeLine`, `ConcreteBucketItem.allowedCells`, `PourPreview` |
| Right-click with **empty hands** (step 2) | Info in the action bar: green "Foundation: 2 layer(s) → buildings up to 16 blocks high (up to y 81)", or red "Not a foundation: nothing solid under it (air, water...)." Click any layer of the stack. With something in a hand, it acts like a normal block (you can build on it) |

**Code:** `ModBlocks.REINFORCED_CONCRETE` = `block/ReinforcedConcreteBlock.java` (right-click info; was a plain `Block` until Phase 3¾ step 2). Foundation logic: `foundation/Foundation.java` (pure, 9 unit tests in `FoundationTest`), `foundation/FoundationLookup.java` (real blocks → cells). Messages: `message.siteworks.foundation.ok/none` (both lang providers). Loot: `ModBlockLootProvider` (pool 1 = `selfOrGravel`, pool 2 = rebar with the same Sledgehammer condition). Datagen: `ModModelProvider.cubeBlock`, `ModBlockTagProvider`, both lang providers.

---

## Cement Mix (Çimento Karışımı)

| | |
|---|---|
| ID | `siteworks:cement_mix` |
| Names | EN "Cement Mix", TR "Çimento Karışımı" |
| What it does | The raw mix. Smelt it to get Cement. It has no other use |
| Recipes | Shapeless **clay ball + bone meal → 2**. Or shapeless **clay ball + calcite → 2** (calcite is like real limestone / kireçtaşı) |
| Recipe book | Both unlock when the player gets a clay ball |
| Stack | 64 |
| Look (placeholder) | Flat vanilla `item/sugar` icon (a white powder). A new look comes in Phase 6 |
| Decision note | The user asked whether 2 steps (craft + smelt) is boring. Kept for now (2026-10-18). If it's boring in play, switch to crafting Cement directly |

**Code:** `ModItems.CEMENT_MIX` (plain `Item`). Datagen: `ModRecipeProvider` (recipe IDs `siteworks:cement_mix` and `siteworks:cement_mix_from_calcite`), `ModModelProvider.flatItem`, both lang providers.

---

## Cement (Çimento)

| | |
|---|---|
| ID | `siteworks:cement` |
| Names | EN "Cement", TR "Çimento" |
| What it does | Ingredient for the Concrete Mixer (step 5): **1 cement + 4 sand + 4 gravel + water = 16 blocks of concrete** |
| Recipe | Smelt Cement Mix: **furnace 10 s** (200 ticks, like ores) or **blast furnace 5 s** (100 ticks). 0.1 XP each. Recipe book group `cement` |
| Stack | 64 |
| Look (placeholder) | Flat vanilla `item/gunpowder` icon (a gray powder: white mix → gray cement after the fire). A new look comes in Phase 6 |

**Code:** `ModItems.CEMENT` (plain `Item`). Datagen: `ModRecipeProvider` (`oreSmelting` + `oreBlasting` → recipe IDs `siteworks:cement_from_smelting_cement_mix` and `siteworks:cement_from_blasting_cement_mix`), `ModModelProvider.flatItem`, both lang providers.

---

## Wet Concrete (Taze Beton) — block, no item

| | |
|---|---|
| ID | `siteworks:wet_concrete` (block only, **no item**) |
| Names | EN "Wet Concrete", TR "Taze Beton" (lang key `block.siteworks.wet_concrete`) |
| How to get it | Only by **pouring** from a Concrete Bucket (step 4). For tests: `/setblock ~ ~ ~ siteworks:wet_concrete` or `/fill ...`. Not in the creative tab (it has no item) |
| Block state | `reinforced` = `false` / `true`. `true` = rebar inside |
| Curing | After **2400 ticks (2 minutes)** it turns into **Concrete** (`reinforced=false`) or **Reinforced Concrete** (`reinforced=true`). You can keep building while it cures. Only in loaded chunks (the timer is saved with the world and continues later) |
| Gravity | Like sand. If the block below is removed (or it's placed in the air), it falls. After landing, **the 2 minutes start again** |
| Falling damage | 1 damage per block fallen (minus 1), max **10** (5 hearts), damage type `minecraft:falling_block`. **The Hard Hat blocks it** |
| Walking on it | Slow: speed **×0.4** (like soul sand) |
| Strength | Hardness 0.5 (like dirt), fast with a shovel, mud sounds, map color = gray |
| Drops | **Nothing** (wet concrete has no item). If `reinforced=true`, the **rebar comes back**. If it falls onto a torch or another block it can't stay on, it breaks, and the rebar comes back too |
| Tags | `#minecraft:mineable/shovel` |
| Look (placeholder) | Vanilla `block/gray_concrete_powder` texture, the same for both states |

**How the timer works (technical):**
- It's **one scheduled tick** per block (a light, built-in timer), no block entity.
- Minecraft keeps **only one** scheduled tick per block position. While the 2-minute cure tick waits, a 2nd "should I fall?" tick would be ignored.
- So wet concrete **falls directly** when a neighbor changes and the block below is free (`neighborChanged`), not with a 2-tick delay like vanilla sand.
- `onPlace` removes any old tick at that position first (`clearArea`), so a block that lands where another one fell away from gets a fresh 2 minutes.
- Known tiny edge case: wet concrete placed **in the air** waits 2 ticks before falling. If a block is put under it within those 2 ticks (0.1 s), it cures right away.

**Code:**
- `block/WetConcreteBlock.java` (extends vanilla `FallingBlock`, `CURE_TICKS = 2400`, `REINFORCED` property)
- `ModBlocks.WET_CONCRETE`, registered with `registerBlockOnly` (no `BlockItem`)
- Loot: `ModBlockLootProvider` (rebar when `match_block` with `reinforced=true`)
- Datagen: `ModModelProvider.cubeBlock`, `ModBlockTagProvider`, both lang providers

---

## Concrete Bucket (Beton Kovası)

| | |
|---|---|
| ID | `siteworks:concrete_bucket` |
| Names | EN "Concrete Bucket", TR "Beton Kovası" |
| What it does | Right-click into a mold → pours up to **16 blocks** of Wet Concrete, from the bottom up (the **mold pour**, rules below) |
| How to get it | At the **Concrete Mixer**: right-click it with a **vanilla empty bucket** (takes up to 16 ready blocks). A half-full bucket can be topped up there. No crafting recipe. Creative tab or `/give @s siteworks:concrete_bucket` for tests |
| Amount | Data component `siteworks:concrete_amount` (1–16, default 16). The bucket **keeps what's left**: tooltip "Concrete: 11/16 blocks" + a gray bar under the icon (hidden when full) |
| Empty | At 0 it turns into a **vanilla empty bucket** (reusable) |
| Creative mode | Like vanilla buckets: the bucket **stays full** in creative. Test the amount/bar in survival |
| Stack | 1 |
| Sounds | Mud place + bucket empty, when something was poured |
| Stats | Counts as "used" in the vanilla statistics (for advancements later) |
| Hand animation | The hand does **not** dip when the amount changes (`allowComponentsUpdateAnimation` = false, Phase 3½ R1) |
| Look (placeholder) | Flat vanilla `item/powder_snow_bucket` icon. A new look comes in Phase 6 |

**Mold pour rules** (concrete behaves like water):
- **Where it starts:** click rebar or a plant inside the mold → right there. Click a wall, the floor or wet concrete → the cell next to the clicked face.
- It **drops** to the lowest open cell under that point (max 32 down, deeper = leaks).
- It fills that layer, **nearest cells first** (flood fill / taşma doldurma, sideways only), then **rises** layer by layer.
- If a layer has an **open cell under it**, the concrete flows down there first. Example: the 2nd bucket poured on top of a half-filled floor finishes the floor first.
- **Open** = air, water, lava, plants, snow layers (blocks a placed block would replace) and **Rebar**. **Good sides** = **Formwork**, our **concrete** (wet, cured, reinforced) and **Steel Beams**. **Floor** = any solid block (soil too). **Formwork rule (Phase 3¾ step 1, user):** any other block at a SIDE of the concrete (soil, stone, wood, bricks, glass...) stops the pour with the red message "Wet concrete needs formwork here! (red dots)": nothing is poured if it's on the first layer, otherwise the layers below it are poured. The preview shows red dots on the face of that block. So **a hole dug in the ground needs formwork against its sides**.
- Rebar cells become `wet_concrete[reinforced=true]` → Reinforced Concrete.
- **One mold layer is max 32 wide** in x and in z. Wider counts as open.
- **First layer too wide = "The formwork leaks!"** (red, action bar). Nothing is used.
- **Higher layer too wide** (it would spill over the lowest wall) **or a closed top = "The mold is full."** The bucket keeps the rest.
- **Aim help (Phase 3½ R3):** if the pour would leak, it also tries the open spots up to **2 blocks to the side** that have room below. So **clicking the top of a mold wall still pours into the mold**.
- **Clicking the top of a full mold** (the concrete would land on wet or cured concrete, nothing to fill) = **"The mold is full."**, not "leaks" (Phase 3½ R3).
- Every placed cell stands on a solid block or on concrete placed earlier in the same pour, so nothing falls.
- **Protection (changed in Phase 3½ R3):** a protected spot (spawn protection, world border, below/above the world) that has a block stays a normal wall. An **open** protected spot **stops the pour**: "You can't pour concrete here (protected area or the edge of the world)." (red), nothing is used. Before, it counted as a floor, and concrete could fall into the protected area. The start cell also checks `mayUseItemAt` (claim mods).

**Pour preview (Phase 3½ R3):** while you hold the bucket (either hand) and look at a block, dust particles show what a click would do, **only for you** (made on your computer), 5 times per second. **Gray** dots = where the concrete goes. A **red** dot = the **hole** where the mold leaks (or the protected spot that stops it). Nothing = the mold is full. It uses the same start spot and the same plan as the real pour (`ConcreteBucketItem.startFor` + `plan`). How the hole is found: the leak path from the start to the far end is walked back; the hole = the last narrow spot on it (walls on both sides, like a missing formwork block), else the last spot next to a wall (an open side), else the start (no mold at all). Deeper than 32 = the red dot is where it starts to fall.

**Unit tests** (`gradlew test`, `src/test/java/.../pour/MoldPourTest.java`), **24 tests**, all ✅ (2026-10-19):
closed mold bottom-up (16 = 9 + 7), pour from above drops to the floor, small mold full + keeps the rest, open mold leaks + uses nothing, no mold leaks, gap higher up stops at that level, rebar cells filled, L-shaped mold, 2nd bucket finishes the lower layer first, 32 wide OK / 33 leaks, no floor leaks (red dot at the start), closed top, every cell supported. New in R3: the leak points at the missing wall block, the open end of a 1-wide channel, clicking the top of a wall pours into the mold, clicking the top of a full mold = full, a protected cell under the mold / beside the layer stops the pour. New in Phase 3¾ step 1: a pit in the ground needs formwork, a pit lined with formwork is a mold, a stone side stops the pour (and says where), a side without formwork higher up pours the layers below, concrete and beams count as sides.

**Code:**
- `pour/MoldPour.java`: the pure pour logic (no Minecraft classes, so it can be unit tested). `MAX_LAYER_SIZE = 32`, `MAX_DROP = 32`, `AIM_HELP = 2`. Cells: `OPEN`, `REBAR`, `FORMWORK` (formwork or a steel beam), `CONCRETE`, `SOLID` (floor only), `BLOCKED`. Statuses: `POURED`, `FULL`, `LEAKS`, `PROTECTED`, `NO_FORMWORK`. `Result.problem` = the hole / protected spot / the open cell next to a bad side; `Result.wall` = the bad side block
- `item/ConcreteBucketItem.java`: `startFor` + `plan` (also used by the preview), reads the real blocks into the pour grid (`cellAt`), places the wet concrete, messages, the bar and tooltip (`appendHoverText` is marked deprecated in 26.3 but still works)
- `client/PourPreview.java` (client only): the particle preview, client tick event (every 4 ticks). `client/PreviewParticles.java`: the dust-dot helper shared with `TrowelPreview`
- `registry/ModDataComponents.java`: `CONCRETE_AMOUNT`
- `ModItems.CONCRETE_BUCKET`. Datagen: `ModModelProvider.flatItem`, both lang providers (also the message and tooltip keys)
- `build.gradle`: JUnit 5.13.4 (test only, not in the jar)

---

## Concrete Mixer (Betoniyer) — block

| | |
|---|---|
| ID | `siteworks:concrete_mixer` (block + block item, with a block entity) |
| Names | EN "Concrete Mixer", TR "Betoniyer" |
| What it does | Mixes concrete by itself. **1 batch = 1 cement + 4 sand + 4 gravel + 1 water → 16 blocks** of ready concrete, in **5 seconds**. No power, no redstone signal needed |
| Recipe | Shaped, `.R. / ICI / I.I` (R = redstone, C = cauldron, I = iron ingot) → 1. Unlocks when the player gets a cauldron |
| Storage | Cement, sand, gravel: up to **64 each** (one item type per slot). Water: up to **8 batches**. Ready concrete: up to **64 blocks** (4 buckets). It stops mixing when the ready concrete is full |
| No GUI (right-click) | **Cement / sand / red sand / gravel** → the whole held stack goes in (as much as fits). **Water bucket** → water = 8/8, you get the empty bucket back. **Empty bucket** → a Concrete Bucket with up to 16 blocks ("No concrete ready yet." if 0). **Half-full Concrete Bucket** → topped up to 16. **Empty hand** (or any other item) → status in the action bar: `Cement 3 · Sand 12 · Gravel 12 · Water 6/8 · Ready 32/64 · Mixing...` |
| Sneak + right-click | Vanilla behaviour (e.g. place sand next to the mixer instead of putting it in) |
| Water source | A **water source block next to it** (any of the 6 sides, waterlogged blocks too) refills the water to 8/8 by itself, once per second |
| Creative mode | Inserting doesn't use up your items (like vanilla) |
| Sand slot | Holds sand **or** red sand, not both at the same time (a slot keeps one item type, because hoppers need real item stacks). Right-click with the other kind → red message "The mixer already holds Sand. Use it up first." (Phase 3½ R1; before, nothing happened) |
| Look | Vanilla cauldron shape with iron block sides. The **ready concrete shows inside**: state `fill` = 0 (empty), 1 (1–21 blocks), 2 (22–42), 3 (43–64), with the `gray_concrete_powder` texture. Particles (gray dust) + a grinding sound while mixing, a mud sound when a batch is done |
| Strength | Hardness 2 (like a cauldron), metal sounds, needs a pickaxe to drop (`#minecraft:mineable/pickaxe`) |
| Drops | Itself. The **stored cement, sand and gravel drop too**. Water and ready concrete are lost (they have no item) |
| Hoppers | **Input from every side** (top and sides; also droppers and hopper minecarts). Each item goes into its own slot automatically: cement, sand/red sand, gravel. Other items stay in the hopper. **Nothing can be taken out** by a hopper (no hopper output in v1.0). Water still comes from a water bucket or a water source next to the mixer, so a hopper + water source setup runs fully automatic |
| Comparator (Phase 3½ R2) | A comparator reading the mixer shows the **ready concrete**: 0 = none, 15 = full (64 blocks). Any concrete = at least 1. One batch (16) = 4, two = 8, three = 11, four = 15. It updates when a batch is done or a bucket takes concrete. Use it e.g. for a lamp "concrete ready", or to stop a hopper line when full |

**Code:**
- `block/ConcreteMixerBlock.java`: `BaseEntityBlock`, `FILL` property, right-click (`useItemOn` / `useWithoutItem`), ticker, comparator (`hasAnalogOutputSignal` / `getAnalogOutputSignal` → `ConcreteMixerBlockEntity.comparatorSignal()`)
- `block/entity/ConcreteMixerBlockEntity.java`: storage (3 item slots + water + ready + progress), mixing every server tick (marked for saving only when a batch is done, Phase 3½ R1), saving (`saveAdditional` / `loadAdditional`), drops on break (`preRemoveSideEffects`), hopper input (implements vanilla `WorldlyContainer`: `canPlaceItemThroughFace` = the right slot only, `canTakeItemThroughFace` = always false). All numbers are constants at the top
- `registry/ModBlockEntities.java`: `CONCRETE_MIXER` block entity type
- Models by hand: `src/main/resources/assets/siteworks/models/block/concrete_mixer{,_level1,_level2,_full}.json` (parents = vanilla cauldron models). Blockstate from datagen (`ModModelProvider`, `PropertyDispatch` on `fill`)
- Datagen: `ModBlockLootProvider` (`dropSelf`), `ModBlockTagProvider`, `ModRecipeProvider`, both lang providers (also the status messages)

---

## Mortar (Harç)

| | |
|---|---|
| ID | `siteworks:mortar` |
| Names | EN "Mortar", TR "Harç" |
| What it does | Fuel for the Trowel (Phase 3 steps 3–4): **1 Mortar = 8 masonry blocks**. No other use yet |
| Recipe | Shapeless **1 Cement + 4 Sand → 8** (sand or red sand, the same sand as the mixer; not the vanilla `#sand` tag, because it has suspicious sand too). One craft = 8 mortar = 64 blocks = 1 stack |
| Recipe book | Unlocks when the player gets Cement |
| Stack | 64 |
| Look (placeholder) | Flat vanilla `item/clay_ball` icon. A new look comes in Phase 6 |

**Code:** `ModItems.MORTAR` (plain `Item`). Datagen: `ModRecipeProvider` (recipe ID `siteworks:mortar`), `ModModelProvider.flatItem`, both lang providers.

---

## Steel Ingot (Çelik Külçe)

| | |
|---|---|
| ID | `siteworks:steel_ingot` |
| Names | EN "Steel Ingot", TR "Çelik Külçe" |
| What it does | Ingredient for the Steel Beam |
| Recipe | Smelt an **Iron Ingot**: **furnace 10 s** (200 ticks) or **blast furnace 5 s** (100 ticks), 0.1 XP each. The coal you burn = "iron + coal", with no extra item. Recipe book group `steel_ingot`. (Planned: blast furnace only. The user allowed the normal furnace too) |
| Recipe book | Unlocks when the player gets an iron ingot |
| Stack | 64 |
| Look (placeholder) | Flat vanilla `item/netherite_ingot` icon (dark gray, like steel). A new look comes in Phase 6 |

**Code:** `ModItems.STEEL_INGOT` (plain `Item`). Datagen: `ModRecipeProvider` (`oreSmelting` + `oreBlasting` → recipe IDs `siteworks:steel_ingot_from_smelting_iron_ingot` and `siteworks:steel_ingot_from_blasting_iron_ingot`), `ModModelProvider.flatItem`, both lang providers.

---

## Steel Beam (Çelik Kiriş) — block

| | |
|---|---|
| ID | `siteworks:steel_beam` (block + block item) |
| Names | EN "Steel Beam", TR "Çelik Kiriş" |
| What it does | A steel I-beam (I-profil) for frames, bridges and roofs. **Extending:** right-click a beam with a beam, and the new beam goes to the **end of that beam line**, so you can build an 8-long bridge from safe ground (like vanilla scaffolding) |
| Extending rules | **Direction:** you click an end of the beam → out of that end. You click the side of a **standing** beam → **up**. You click the side or top of a **lying** beam → the way you **look** along it. **Reach:** the new spot can be up to **8 blocks** from the beam you click. Only beams with the **same axis** count as the line. **Stops** with a red action bar message: "Something is in the way." (a solid block, another beam with a different axis, spawn protection, world border) or "Too far! Walk closer to the end of the beam." (the line is longer than 8). Above the build height = vanilla's height limit message. Water, grass, snow layers are replaced like normal placement. Not inside players/mobs (vanilla check). Creative = no item used. **Sneak** + right-click = normal placement next to the face |
| Recipe | Shaped, `SSS / .S. / SSS` (S = Steel Ingot, an "I" shape, 7 steel) → **16** (one craft = two 8-long bridges; about the iron per piece of vanilla iron bars). Was 8, made more generous in step 2b (user: "it's a game, it shouldn't be boring") |
| Climbing (step 2b) | A **standing** beam is a slim column at one side of its block, with room for you in front, and the block is in vanilla's `#minecraft:climbable` tag. So it works **exactly like a ladder**: walk into the column → you stand **inside its block space** and climb up. Sneak → stay in place. Let go → slide down slowly, **no fall damage**. Because you climb inside the column's own block, a beam of the next floor next to the column doesn't block your head. At the top you stand on the column's top and step onto the floor. Leave the space above the column free (like a ladder hole). Lying beams can't be climbed (no room inside). ⚠️ Like ladders, **mobs** can climb it too (vanilla tag, no mixin). First version used a mixin (climbing on the outside of the full I-beam); replaced the same day, because the user wanted to try the ladder version, and the outside version got stuck under the next floor's beam |
| Recipe book | Unlocks when the player gets a Steel Ingot |
| Placement | Has an **axis** like a log (`axis` = x / y / z): it points along the face you click. Click the top/bottom of a block = **standing** (column), click a side = **lying** (beam). A standing beam also has a **facing** (`facing` = north/east/south/west): it stands at the **far side** of its block (the way you look), so you can walk straight into it. On top of or under another column it copies that column's facing, so a column stays one straight line (extending too). Needs no support, never falls |
| Shape | **Lying:** full width, 2 flanges (3-pixel plates) on the **top and bottom** + a web (4 pixels), so the top is flat and you can walk on it. **Standing:** a slim I at one side of the block (flanges 12×2 pixels, web 2×2, 6 pixels deep); the free 10 pixels (0.625 blocks) fit a player (0.6). Outline + collision = the same shapes |
| Strength | Like an iron block: hardness 5, blast resistance 6, metal sounds, map color = metal, note block = iron xylophone |
| Drops | Itself, with a **stone pickaxe or better**. A wooden pickaxe or a hand breaks it but drops nothing |
| Tags | `#minecraft:mineable/pickaxe`, `#minecraft:needs_stone_tool`, `#minecraft:climbable`. **Not** in `sledge_breakable` (it's metal) |
| Mold | A good mold side for the Concrete Bucket, like Formwork (a column standing in the mold doesn't stop the pour; Phase 3¾ step 1) |
| Look (placeholder) | I-profile, vanilla `block/iron_block` texture. 2 hand-written models (datagen can't draw shapes): `steel_beam.json` (full I, used for lying beams and the item) and `steel_beam_column.json` (the slim standing column). The user may change the look later |

**Code:**
- `block/SteelBeamBlock.java`: extends vanilla `RotatedPillarBlock` (axis + placement + rotation). Adds `FACING` for standing beams (`getStateForPlacement`: copies a column above/below, else the way you look). `getShape`: lying = 3 boxes per axis, standing = `Shapes.rotateHorizontal` of the north column (like vanilla ladders). `rotate`/`mirror` turn the facing
- `item/SteelBeamItem.java`: the block item. Overrides `updatePlacementContext` (like vanilla `ScaffoldingBlockItem`): walks along the line and returns a placement context at the end, with the extend direction as the "clicked face" (so the axis stays the same). `MAX_REACH = 8`
- `ModBlocks.STEEL_BEAM` (registered with `SteelBeamItem::new` instead of a plain `BlockItem`)
- Models by hand: `src/main/resources/assets/siteworks/models/block/steel_beam.json` (full I, standing) and `steel_beam_column.json` (slim column at the north side). The blockstate (datagen, `ModModelProvider`, dispatch on `axis` + `facing`): lying Z = `steel_beam` with `x: 90`, lying X = `x: 90, y: 90`, standing = `steel_beam_column` turned by facing (`y: 0/90/180/270`). The hitboxes in `SteelBeamBlock` match these turns
- Datagen: `ModBlockLootProvider` (`dropSelf`), `ModRecipeProvider`, `ModBlockTagProvider`, both lang providers (also `message.siteworks.beam.blocked` / `too_far`)

---

## Trowel (Mala)

| | |
|---|---|
| ID | `siteworks:trowel` |
| Names | EN "Trowel", TR "Mala" |
| What it does | A **builder's wand for masonry**. Right-click a masonry block: it places many blocks at once, from your inventory, and uses **Mortar** (1 Mortar = 8 blocks) |
| Which block | 1. a **masonry block in your other hand**; 2. else the **masonry block you clicked at the 1st click** (it copies the wall; saved on the trowel as `siteworks:row_block`); 3. else the masonry block you clicked at the 2nd click. None → "Hold the bricks in your other hand, or click a masonry block." |
| Blocks from | Your **whole inventory** (hotbar, main inventory, off hand), not only the hand |
| Which hand | Hold the trowel in the **main hand** (bricks in the off hand if you want). With the trowel in the off hand and bricks in the main hand, vanilla uses the main hand first: it places 1 normal brick and the trowel does nothing |
| Hand animation | The hand does **not** dip when the charge or the row start changes (`allowComponentsUpdateAnimation` = false, Phase 3½ R1) |
| Mode: **Row (8)** = "from → to" (step 3b, fixed after the user's video) | **Both clicks mark a spot where a brick goes:** the spot in front of the clicked face (grass/snow: the clicked spot itself), like the Measuring Tape corners. **1st click** = the start spot → green sparkles there + "Row start set. Now click where the row ends (max 8)." (saved as `siteworks:row_start` with the dimension, shown in the tooltip). **2nd click** = the end spot → the straight line **from the start spot to the end spot, both included**, 1–8 blocks. Same spot twice = 1 block. Examples: click the **top** of a wall at both ends = a new layer on top; click the **top of the ground** at both ends = a first row on bare ground. **A wrong 2nd click never gets you stuck:** not straight → "Not a straight line. New row start set here.", longer than 8 → "Too long (max 8)! New row start set here." (red, and that click becomes the new start). **Cancel:** sneak + right-click the air while a start is set → "Row start removed.". The first blocked spot still ends the row early. A start in another dimension is ignored. History: the 1st version used the start **block** (row after it, start kept after a wrong click) → in the user's video every click said "Not a straight line." (stuck with an invisible old start, and rows on top of a wall were impossible) |
| ~~Mode: Course (16)~~ | **Dropped** (user, 2026-10-19: the "from → to" Row is enough). The trowel has only Row now. A trowel saved in the old Course mode loads as Row |
| Sneak + right-click the air | **Cancels the row start** ("Row start removed."); does nothing when no start is set. (It switched modes while Course mode existed; Phase 5 may bring a mode switch back for "Blueprint fill") |
| Preview (step 5) | While you hold the trowel (either hand), small **dust particles** show the row, **only for you** (made on your computer), 5 times per second. **No start yet:** a **white** dot on the spot you look at (= where the row would start). **Start set:** a **yellow** dot on the start, **green** dots on every spot from the start to the spot you look at (stops at the first spot that isn't free, like the real placing), or a **red** dot on the spot you look at if it is not in a straight line or more than 8 (a click there makes it the new start). It uses the same spot + line rules as the placing code (`TrowelItem.spotFor`, `straightDirection`) |
| Mortar charge | **1 Mortar = 8 blocks.** When the charge is 0 and a block can really be placed, the trowel takes 1 Mortar from your inventory → charge 8. Each placed block costs 1 charge. The rest stays in the trowel (data component `siteworks:mortar_charge`, 0–8), so **nothing is wasted**: 64 blocks = exactly 8 Mortar. A beige bar under the icon shows the charge (hidden at 0) |
| Messages (red, action bar) | "Out of mortar!", "No more Bricks!" (the block's name), "Something is in the way." (only when **nothing** was placed), "Not a straight line. New row start set here.", "Too long (max 8)! New row start set here.", "Hold the bricks in your other hand, or click a masonry block." Normal (white): "Row start set...", "Row start removed." |
| Placement rules | Every block is placed by vanilla's `BlockItem.place()`, like a normal right-click on that spot: not inside players or mobs, the normal place sound, 1 block used per placement. Adventure mode can't use it |
| Creative mode | Free: no blocks and no mortar needed, the charge doesn't change |
| Tooltip | "Mortar: 5/8 blocks", (yellow, only when set) "Row start: x y z", "Row: click where the row starts, then where it ends", "Sneak + right-click the air: cancel the row start" |
| Recipe | Shaped, `.I / S.` (1 iron ingot + 1 stick, diagonal). The mirrored shape works too |
| Recipe book | Unlocks when the player gets an iron ingot |
| Stats | Stack 1, **no durability** (the mortar is the cost), no enchanting, no repair |
| Look (placeholder) | Vanilla `item/iron_shovel` texture, held like a tool. A new look comes in Phase 6 |

**Masonry tag** `siteworks:masonry_blocks` (full blocks only; stairs, slabs and walls are placed by hand): bricks, `#minecraft:stone_bricks` (stone, mossy, cracked, chiseled stone bricks), mud bricks, deepslate bricks + tiles, tuff bricks, nether bricks, red nether bricks, end stone bricks, polished blackstone bricks, quartz bricks, resin bricks, cobblestone, mossy cobblestone, `siteworks:concrete`, `#minecraft:concrete` (the 16 vanilla colors).

**Code:**
- `item/TrowelItem.java`: `use` (sneak + air = cancel a row start), `useOn` (Row: 1st click → `setRowStart`, 2nd click checks the line, picks the block → `placeLine`), `setRowStart` (start spot + block + sparkles + message), `clearRowStart`, `placeLine` (the placing loop from the start spot, mortar charge, messages), `straightDirection`, `otherHandBlock`, `masonryBlock`, `findStack`, bar + tooltip. `BLOCKS_PER_MORTAR = 8`
- `item/TrowelMode.java`: the enum, only `ROW` (8) now (Phase 5 adds Blueprint fill). Save codec = the name; an unknown name (the old `course`) loads as `ROW`. Network codec = the index
- `client/TrowelPreview.java` (client only): the particle preview, on vanilla-style client ticks (`ClientTickEvents.END_CLIENT_TICK`, every 4 ticks), dust particles via `ClientLevel.addParticle`. Registered in `client/SiteWorksClient.java`
- `registry/ModDataComponents.java`: `TROWEL_MODE`, `MORTAR_CHARGE`, `ROW_START` (a vanilla `GlobalPos`: dimension + the start spot), `ROW_BLOCK` (the masonry item of the 1st click)
- `registry/ModItems.java` (`TROWEL`), `registry/ModTags.java` (`MASONRY_BLOCKS`), `registry/ModCreativeTabs.java`
- Datagen: `ModBlockTagProvider` (masonry tag), `ModRecipeProvider`, `ModModelProvider.handheldTool`, both lang providers (name, modes, tooltip, messages)
