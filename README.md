# LDAPI (IndieRPG)

Paper 1.12.2 独立 RPG 核心插件，相容 MythicMobs 4.13.0。

## 功能总览

- **自定義物品**：Lore ID 識別，支援 `indierpg-id`、`item-id` 等標籤
- **屬性引擎**：基礎 / 職業 / 天賦 / 公會 / 裝備 / 寵物 六層疊加
- **職業系統**：戰士、法師、刺客、牧師，每級自動加屬性，升級解鎖 MythicMobs 技能
- **天賦系統**：GUI 加點，多棵天賦樹，即時反映到屬性
- **戰鬥計算**：攻擊力加成、爆擊、防禦減傷
- **隊伍系統**：邀請、接受、經驗共享（30 格內）
- **公會系統**：建立、邀請、公會等級、公會 Buff、公會銀行
- **副本系統**：世界模板複製、波次生成、通關獎勵
- **寵物系統**：MythicMobs 召喚、跟隨、屬性加成
- **聲望系統**：多陣營、階段變化
- **NPC 對話**：文字對話、獎勵金幣與聲望
- **裝備屬性**：Lore 中 `attribute-attack: 15.0` 自動生效
- **GUI 框架**：可擴充的通用介面
- **PlaceholderAPI**：支援 `%ldapi_gold%` 等變數

## 安裝

### 前置需求

- **Paper 1.12.2**（或 Spigot 1.12.2）
- **Java 8**
- （可選）**MythicMobs 4.13.0**
- （可選）**PlaceholderAPI**

### 編譯

```bash
git clone https://github.com/LongDrange/IndieRPG.git
cd IndieRPG
mvn clean package
```

產出：

```
target/LDAPI-1.0.0.jar
target/LDAPI-1.0.0-javadoc.jar
target/LDAPI-1.0.0-sources.jar
```

### 部署

1. 把 `LDAPI-1.0.0.jar` 放入伺服器的 `plugins/` 目錄
2. 啟動伺服器一次，生成 `plugins/LDAPI/` 配置目錄
3. 編輯 `plugins/LDAPI/config/` 下的 YAML
4. 執行 `/ld reload` 或重啟伺服器

### Maven 依賴

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.LongDrange</groupId>
  <artifactId>IndieRPG</artifactId>
  <version>v1.0.0</version>
</dependency>
```

## 指令

| 指令 | 說明 | 權限 |
|---|---|---|
| `/ld` | 主指令 | `ldapi.user` |
| `/job` | 職業系統（無參數開 GUI） | `ldapi.user` |
| `/talent` | 天賦系統 | `ldapi.user` |
| `/party` | 隊伍 | `ldapi.user` |
| `/guild` | 公會（無參數開 GUI） | `ldapi.user` |
| `/dungeon` | 副本 | `ldapi.user` |
| `/pet` | 寵物（無參數開 GUI） | `ldapi.user` |
| `/rep` | 聲望 | `ldapi.user` |
| `/npc talk <id>` | NPC 對話 | `ldapi.user` |
| `/task` | 任務 | `ldapi.user` |
| `/crate` | 寶箱 | `ldapi.user` |
| `/spacering` | 空間戒指 | `ldapi.user` |
| `/ld reload` | 重載配置 | `ldapi.admin` |

## 權限

```yaml
permissions:
  ldapi.admin:
    description: 管理員權限
    default: op
  ldapi.user:
    description: 一般使用者
    default: true
```

## PlaceholderAPI 變數

| 變數 | 說明 |
|---|---|
| `%ldapi_gold%` | 金幣數量 |
| `%ldapi_job%` | 職業名稱 |
| `%ldapi_job_level%` | 職業等級 |
| `%ldapi_job_exp%` | 職業經驗 |
| `%ldapi_battlepass_level%` | 戰令等級 |
| `%ldapi_battlepass_xp%` | 戰令經驗 |
| `%ldapi_talent_points%` | 天賦點 |
| `%ldapi_rank%` | 稱號 |
| `%ldapi_guild%` | 公會名稱 |
| `%ldapi_guild_level%` | 公會等級 |
| `%ldapi_party_size%` | 隊伍人數 |
| `%ldapi_rep_<faction>%` | 指定陣營聲望 |
| `%ldapi_attack%` | 攻擊力 |
| `%ldapi_defense%` | 防禦力 |
| `%ldapi_health%` | 生命值 |

## 配置檔結構

```
plugins/LDAPI/
├─ config.yml              主配置
├─ config/
│  ├─ general.yml          通用設定
│  ├─ items.yml            自訂物品
│  ├─ attributes.yml       屬性定義
│  ├─ jobs.yml             職業定義
│  ├─ parties.yml          隊伍設定
│  ├─ guilds.yml           公會設定
│  ├─ dungeons.yml         副本設定
│  ├─ pets.yml             寵物設定
│  ├─ reputation.yml       聲望陣營
│  ├─ npc-dialogues.yml    NPC 對話
│  ├─ jewelry.yml          飾品
│  ├─ spaces.yml           空間戒指
│  ├─ shop.yml             商店
│  ├─ dailyreward.yml      每日獎勵
│  └─ mythicmobs.yml       MM 整合
├─ playerdata/             玩家資料（YAML）
├─ dungeon-templates/      副本模板世界
└─ logs/                   插件日誌
```

## 副本模板世界準備

1. 建立新世界（如 `dungeon_goblin`），設好地形與出生點
2. 關閉伺服器
3. 把世界資料夾搬到 `plugins/LDAPI/dungeon-templates/dungeon_goblin/`
4. 從 `server.properties` 移除自動載入
5. 重啟伺服器

之後 `/dungeon enter goblin_cave` 就能進入。

## MythicMobs 技能範例

```yaml
WarriorSlash:
  Skills:
    - damage{amount=20} @target
    - effect:particles{p=crit;amount=20} @target

PetBabyWolf:
  Type: WOLF
  Display: '&7小狼'
  Health: 20
  Skills:
    - skill{s=PetAttack} @target ~onAttack
```

## 開發

- **Java 8**
- **Maven 3.6+**
- **Spigot API 1.12.2**

```bash
mvn clean package          # 編譯
mvn javadoc:javadoc        # 產出 Javadoc
mvn clean package javadoc:jar source:jar  # 三個 JAR
```

## 授權

依原倉庫。