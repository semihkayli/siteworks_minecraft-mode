# Items — Site Works

_Every item, tool and block we have **built** (not only planned). Plans are in `docs/game-design.md`. This file shows what is **really in the code**._
_Rule: update this file in the same step as you add or change an item (see `docs/rules.md`)._

## Overview

| Item | ID | Phase | Status |
|---|---|---|---|
| Mattock (Kazma Çapa) | `siteworks:mattock` | 1 | ✅ tested in-game |
| Sledgehammer (Balyoz) | `siteworks:sledgehammer` | 1 | ✅ tested in-game |
| Hard Hat (Baret) | `siteworks:hard_hat` | 1 | ✅ built, durability choice OK by the user, anvil test pending |
| Safety Net (Güvenlik Ağı) — block | `siteworks:safety_net` | 1 | ✅ tested in-game |
| Ghost Test (Hayalet Testi) — **TEST ITEM** | `siteworks:ghost_test` | 1 | ✅ tested in-game (512 ghosts = 112–116 FPS). Replaced by the Survey Stake in Phase 5 |

**Creative tab:** "Site Works" (`siteworks:site_works`), icon = Hard Hat. Order: Hard Hat, Mattock, Sledgehammer, Safety Net, Ghost Test.

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
| Enchantable | Yes, enchantability 14 (iron). Unbreaking, Efficiency, Fortune, Silk Touch and Mending all work |
| Look (placeholder) | Vanilla `item/iron_hoe` texture, held like a tool. A new look comes in Phase 6 |

**3×3 rules**
- The block you hit always breaks like a normal shovel hit.
- The 3×3 starts only if that block is in `#siteworks:soft_ground`.
- The 8 blocks around it break only if they are in `#siteworks:soft_ground` too.
- Plane: if you look straight ahead, it digs a wall. If you look up or down, it digs the floor or ceiling.
- **Sneak = 1 block only.**
- Every block broken costs 1 durability. A full 3×3 = 9 durability, so about 55 full swings.
- If the tool breaks or you switch items halfway, the digging stops.
- Uses vanilla's normal break (`destroyBlock`). Because of that, drops, enchantments and spawn protection work as normal.

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
| Enchantable | Yes, enchantability 14 (iron) |
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
| What it does | Helmet. **Cancels all damage from falling blocks:** falling anvil, falling block (and our wet concrete in Phase 2), falling dripstone |
| Recipe | Shaped, `XXX / XYX` with X = iron ingot and Y = yellow dye (the iron helmet shape with dye in the hole) |
| Recipe book | Unlocks when the player gets an iron ingot |
| Armor | **2** (same as an iron helmet), toughness 0, knockback resistance 0 |
| Durability | **165** (same as an iron helmet) |
| Wear | When it blocks falling damage, it loses **1 durability per 4 damage** (at least 1), like vanilla armor. A big anvil hit costs about 10. User agreed (2026-10-12) |
| Repair | Iron ingot in an anvil (`#minecraft:repairs_iron_armor`) |
| Enchantable | Yes, like an iron helmet (Protection, Unbreaking, Mending...) |
| Equip sound | Iron armor sound |
| Look (placeholder) | The **golden helmet** look, on the head and in the inventory (yellow = hard hat). A new look comes in Phase 6 |
| Does NOT protect from | Suffocation (being stuck in sand/gravel). That's a different damage type, and vanilla helmets don't protect from it either |

**Code:**
- `ModItems.HARD_HAT` = `Item` + `humanoidArmor(HARD_HAT_MATERIAL, ArmorType.HELMET)`
- `HARD_HAT_MATERIAL` = the 7 values of `ArmorMaterials.IRON`, but `assetId = EquipmentAssets.GOLD`
- Protection: `event/HardHatProtection.java`. It uses the Fabric event `ServerLivingEntityEvents.ALLOW_DAMAGE` and checks the vanilla damage tag `#minecraft:damages_helmet`
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
