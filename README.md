# LDAPI 1.0.0

LDAPI 是以 Minecraft 1.12.2 Paper 為核心的中文 RPG 插件，目標是以 1.12.2 API 為基準，保留 Java 8 兼容性，支援自定義物品、飾品成長、天賦、任務、寶箱、戰令與 MythicMobs 4.13.0 互動。

## 核心設計規範

- 伺服器核心：Minecraft 1.12.2
- API 基底：Paper 1.12.2
- Java：8
- 物品識別：使用 Lore、顯示名稱、namespace、item-id、mythic-id
- 不使用：PersistentDataContainer、CustomModelData 等 1.13+ API
- 指令前綴：`/ld`
- 可用大小寫：`/LD`
- 移除：`/led`

## 指令列表

```text
/ld                              顯示主幫助
/ld info                         查看版本與插件狀態
/ld reload                      重載配置（需要 ldapi.admin）
/ld give <物品ID>               給予自定義物品
/ld items                       列出所有物品
/ld gold                        查看玩家金幣
/ld rank                        查看玩家稱號
/ld bp                          查看戰令進度
/ld admin reload                管理員重載
/ld admin setlevel <玩家> <飾品ID> <等級>
/ld admin addgrowth <玩家> <飾品ID> <數值>
/ld admin resetdata <玩家>

/sr 或 /ldsr                    開啟空間戒指
/talent 或 /ldtalent            開啟天賦
/task 或 /ldtask                開始任務
/crate 或 /ldcrate              開啟寶箱
```

## 安裝方式

1. 準備 Paper 1.12.2 伺服器並使用 Java 8 啟動。
2. 編譯專案：
   ```bash
   mvn clean package
   ```
3. 將 `target/LDAPI-1.0.0.jar` 放入 `plugins`。
4. 啟動伺服器一次，讓記錄檔生成資料夾。
5. 編輯 `plugins/LDAPI/config/` 下的 YAML 配置。
6. 輸入 `/ld reload` 或重啟伺服器套用配置。

## 配置資料夾

```text
plugins/LDAPI/
├─ config.yml
├─ config/
│  ├─ general.yml
│  ├─ items.yml
│  ├─ attributes.yml
│  ├─ jewelry.yml
│  ├─ spaces.yml
│  ├─ commands.yml
│  ├─ tests.yml
│  ├─ shop.yml
│  └─ dailyreward.yml
├─ playerdata/
│  └─ <UUID>.yml
└─ logs/
```

## 核心配置範例

### general.yml

```yaml
general:
  prefix: "&d[LDAPI] &r"
  debug: false
  command-prefix: ld
  lore:
    enabled: true
    ignore-color: true
    id-keys:
      - indierpg-id
      - item-id
      - mythic-id
      - namespace
      - jewelry-id
      - card-id
      - soul-bead-id
```

### items.yml

```yaml
items:
  growth_core:
    id: growth_core
    namespace: ldapi:item
    material: EMERALD
    display-name: "&b成長核心"
    lore:
      - "&7item-id: growth_core"
      - "&7用途：提升飾品成長"
```

### attributes.yml

```yaml
attributes:
  attack:
    display-name: "&c攻擊"
    default: 0
    min: 0
    max: 1000000
  defense:
    display-name: "&9防禦"
    default: 0
    min: 0
    max: 1000000
```

### jewelry.yml

```yaml
jewelry:
  enabled: true
  slots: 9
  definitions:
    abyss_ring:
      id: abyss_ring
      namespace: ldapi:jewelry
      name: "&5深淵戒指"
      material: DIAMOND
      base-attributes:
        attack: 10
        defense: 5
      growth:
        max-level: -1
        sources: [online-time, real-time, special-item]
        per-level:
          "1": {attack: 1.0, defense: 0.5}
          "2": {attack: 1.5, defense: 0.8}
          "3": {attack: 2.5, defense: 1.2}
        default-per-level:
          attack: 1.0
          defense: 0.5
```

### shop.yml

```yaml
shop:
  enabled: true
  menu-title: "&6&lLDAPI 商店"
  items:
    growth_core:
      price: 80
      material: EMERALD
      display-name: "&b成長核心"
      item: growth_core
    abyss_ring:
      price: 300
      material: DIAMOND
      display-name: "&5深淵戒指"
      item: abyss_ring
```

### dailyreward.yml

```yaml
dailyreward:
  enabled: true
  gold: 100
  xp: 25
  cooldown-hours: 24
  message: "&e[每日獎勵] &7已領取：+{gold} 金幣，+{xp} 經驗。"
  rewards:
    day1:
      gold: 100
      xp: 10
    day2:
      gold: 150
      xp: 15
    day3:
      gold: 200
      xp: 20
```

## 玩家資料保存

玩家資料位於：

```text
plugins/LDAPI/playerdata/<UUID>.yml
```

包含：
- 金幣
- 稱號
- 天賦點數
- 戰令等級與 XP
- 飾品等級
- 飾品成長點數
- 在線分鐘數
- 每日簽到紀錄

## 服務器排錯

### 命令無法使用

確認 `plugin.yml` 中 `ldapi` 的 `aliases: [ld]` 存在，重啟伺服器後再測試：

```text
/ld
/LD
```

### Lore 無法識別

確認 Lore 存在以下格式：

```text
&7item-id: growth_core
&7jewelry-id: abyss_ring
&7mythic-id: AbyssRing
```

### 配置沒有被載入

執行：

```text
/ld reload
```

若仍未生效，請檢查 YAML 是否有縮排錯誤，並確認檔案位於：

```text
plugins/LDAPI/config/
```

### 玩家資料丟失

備份 `plugins/LDAPI/`，不要在玩家在線時直接刪除 `playerdata`。

## 開發與維護

- 不使用 1.13+ 新 API
- 所有 ID 保持英文
- 顯示名稱與 GUI 可用中文
- 新增功能時優先同一個系統做管理器層拆分
- 強烈建議每次升級前備份資料夾

## 建置命令

```bash
mvn clean package
```

完整 JAR：

```text
target/LDAPI-1.0.0.jar
```
