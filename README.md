# IndieRPG v4.0.0

Full RPG suite for Minecraft 1.12.2 Paper. **No MythicMobs dependency** (soft-compatible via lore ID detection).

## Systems

| System | Command | Description |
|--------|---------|-------------|
| Space Ring | `/sr` | Virtual storage (9/27/54 slots) |
| Soul Storage | Right-click orb | Multi-page infinite storage with level unlocks |
| Talents | `/talent` | 5 trees with **real stat effects** (damage/armor/health/crit/lifesteal) |
| Tasks | `/task` | Kill-count quests with rewards |
| Crates | `/crate` | 3-tier loot boxes with configurable odds |
| Ranks | Right-click scroll | 6-title system with colors |
| BattlePass | Auto | XP progression with milestones |
| Daily Reward | `/rpg daily` | Daily check-in with streak bonuses |
| Gold Shop | `/rpg shop` | Buy items with gold |
| Monster Cards | `/rpg cards` | Collect cards → potion effects → set bonuses |
| Bestiary Guide | `/rpg guide` | Kill monsters → unlock permanent effects |
| Jewelry Slots | `/rpg jewelry` | Equip jewelry → set bonus effects |
| Item Exchange | `/rpg exchange` | Trade items for other items + trash can |

## MythicMobs Integration

The plugin detects triggers from **both display name AND lore lines**:

- `card-id: zombie` → gives zombie card effect
- `jewelry-id: lucky-ring` → equips jewelry
- `soul-bead-id: skeleton-core` → soul bead
- Any lore keyword matching config triggers

## Configuration

All settings in `plugins/IndieRPG/config.yml` — `/rpg reload` to apply.

### Add your own:
- New talent trees (max level, cost, slot, effect)
- New crate tiers (drop rates, rewards)
- New ranks (colors, prefixes)
- New shop items (price, item)
- New monster cards (effect, set bonuses)
- New guide entries (kill requirements, unlock chance, permanent effect)
- New jewelry sets (bonus effects)
- New exchange rules

## Build

```bash
mvn clean package
```
