# Items — Site Works

_Every item, tool and block we have **built** (not only planned). Plans are in `docs/game-design.md`. This file shows what is **really in the code**._
_Rule: update this file in the same step as you add or change an item (see `docs/rules.md`)._

## Overview

| Item | ID | Phase | Status |
|---|---|---|---|
| Mattock (Kazma Çapa) | `siteworks:mattock` | 1 | ✅ tested in-game |
| Sledgehammer (Balyoz) | `siteworks:sledgehammer` | 1 | ✅ tested in-game |
| Hard Hat (Baret) | `siteworks:hard_hat` | 1 | ✅ tested in-game (anvil + falling wet concrete, 2026-10-18) |
| Safety Net (Güvenlik Ağı) — block | `siteworks:safety_net` | 1 | ✅ tested in-game |
| Ghost Test (Hayalet Testi) — **TEST ITEM** | `siteworks:ghost_test` | 1 | ✅ tested in-game (512 ghosts = 112–116 FPS). Replaced by the Survey Stake in Phase 5 |
| Cement Mix (Çimento Karışımı) | `siteworks:cement_mix` | 2 | ✅ tested in-game |
| Cement (Çimento) | `siteworks:cement` | 2 | ✅ tested in-game |
| Formwork (Kalıp) — block | `siteworks:formwork` | 2 | ✅ tested in-game |
| Rebar (İnşaat Demiri) — block | `siteworks:rebar` | 2 | ✅ tested in-game |
| Concrete (Beton) — block | `siteworks:concrete` | 2 | ✅ tested in-game |
| Reinforced Concrete (Betonarme) — block | `siteworks:reinforced_concrete` | 2 | ✅ tested in-game |
| Wet Concrete (Taze Beton) — block, **no item** | `siteworks:wet_concrete` | 2 | ✅ tested in-game (with commands) |

**Creative tab:** "Site Works" (`siteworks:site_works`), icon = Hard Hat. Order: Hard Hat, Mattock, Sledgehammer, Safety Net, Cement Mix, Cement, Formwork, Rebar, Concrete, Reinforced Concrete, Ghost Test.

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
- **Later (Phase 2):** concrete → gravel, reinforced concrete → gravel + rebar (recycling).

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
- 3×3 logic: `event/AreaMining.java` (shared with the Mattock)
- Tag key: `ModTags.SLEDGE_BREAKABLE`. Vanilla tags are added with `forceAddTag`

---

## Hard Hat (Baret)

| | |
|---|---|
| ID | `siteworks:hard_hat` |
| Names | EN "Hard Hat", TR "Baret" |
| What it does | Helmet. **Cancels all damage from falling blocks:** falling anvil, falling dripstone, **falling Wet Concrete** (Phase 2), and any falling block that deals damage. Vanilla falling sand/gravel deal no damage. Phase 2 step 7 may add Sledgehammer ceiling debris |
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
| Strength | 0.4 (breaks fast, even by hand), wool sounds, map color = wool, can catch fire from lava |
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
- `registry/ModBlocks.java`: registers the block + `BlockItem`
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
| Mold rule (step 4) | Any solid block can be a mold wall. Formwork is just the easiest one to remove |

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

**Code:** `ModBlocks.REINFORCED_CONCRETE` (plain `Block`). Loot: `ModBlockLootProvider` (pool 1 = `selfOrGravel`, pool 2 = rebar with the same Sledgehammer condition). Datagen: `ModModelProvider.cubeBlock`, `ModBlockTagProvider`, both lang providers.

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
