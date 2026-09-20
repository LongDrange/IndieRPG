# IndieRPG v2.0.0

Fully config-driven RPG plugin for Minecraft 1.12.2 Paper. **No MythicMobs dependency required** (soft-compatible).

## What's New in v2.0.0

- ✅ **100% config-driven** — all values in `config.yml`, no code changes needed
- ✅ **Zero MythicMobs dependency** — works standalone, also detects MythicMobs items
- ✅ **Lore keyword detection** — triggers on both display name AND lore lines (works with MythicMobs custom items)
- ✅ **Infinite soul storage** — set `size: -1` for unlimited paginated storage
- ✅ **5 talent trees** — Attack/Defense/Health/Crit/Lifesteal, all configurable
- ✅ **3 crate tiers** — Common/Rare/Legendary, fully customizable drop rates
- ✅ **6 ranks** — Rookie/Warrior/Knight/Elite/Legend/Mythic
- ✅ **12 built-in custom items** — `/rpg give <id>` to obtain
- ✅ **Extensible** — add new trees, tiers, ranks, items purely via config

## Commands

| Command | Description |
|---------|-------------|
| `/rpg` | Main menu |
| `/rpg info` | Plugin info |
| `/rpg reload` | Reload config |
| `/rpg give <id>` | Give custom item |
| `/rpg items` | List all item IDs |
| `/rpg gold` | Check gold |
| `/rpg rank` | Check rank |
| `/rpg bp` | BattlePass progress |
| `/sr` | Open Space Ring |
| `/talent` | Open Talent Menu |
| `/task` | Start Slayer Task |
| `/crate` | Open Common Crate |

## Quick Start

1. Place `IndieRPG-2.0.0.jar` in `plugins/`
2. Start server
3. `/rpg items` to see all obtainable items
4. `/rpg give space-ring-advanced` to get a ring
5. Right-click the ring to open virtual storage

## Configuration

All settings are in `plugins/IndieRPG/config.yml` — edit and `/rpg reload`.

### Key customizable sections:

- **spacering.trigger-items** — add your own ring names
- **spacering.sizes** — ring sizes per tier
- **soulstorage.size** — `-1` for infinite, or any number
- **talent.trees** — add/remove talent trees, set max level, slot, icon
- **crate.rewards** — add tiers, set drop chances and rewards
- **rank.ranks** — add custom ranks with colors
- **battlepass** — XP per level, max level, milestone rewards
- **items** — define your own custom items (material, name, lore)

## MythicMobs Compatibility

The plugin detects item triggers by matching:
1. **Display name** (any color format)
2. **Lore lines** — keywords in lore (works with MythicMobs YAML items)

If you create a MythicMobs item with `lore:
  - "&7Right-click: Open Space Ring"`, it will trigger automatically.
