# IndieRPG

Independent RPG plugin for Minecraft 1.12.2 Paper, fully compatible with MythicMobs.

## Features

| System | Command | Description |
|--------|---------|-------------|
| Space Ring | `/sr` | Virtual storage inventory (9/27/54 slots) |
| Talent | `/talent` | Talent point allocation GUI (Attack/Defense/Health) |
| Task | `/task` | Kill-count tasks with rewards |
| Crate | `/crate` | Loot crates with rarity tiers |
| Rank | Right-click title | Title system with color prefixes |
| BattlePass | Auto | XP-based progression with milestone rewards |
| Soul Storage | Right-click item | Infinite storage space |

## Commands

- `/rpg` - Main menu
- `/rpg info` - Plugin info
- `/rpg reload` - Reload config
- `/sr` - Open Space Ring
- `/talent` - Open Talent Menu
- `/task` - Start Slayer Task
- `/crate` - Open Crate

## Build

```bash
mvn clean package
```

Output: `target/IndieRPG-1.0.0.jar`

## Installation

1. Download or build `IndieRPG-1.0.0.jar`
2. Place in `plugins/` folder
3. (Optional) Install MythicMobs 4.13.0
4. Start server

## Requirements

- Minecraft 1.12.2 Paper/Spigot
- Java 8+
- MythicMobs (soft dependency, optional)
