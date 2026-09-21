# LDAPI 1.0.0

LDAPI 是一個以 Minecraft 1.12.2 Paper 為核心的中文 RPG 插件，目標是提供穩定、簡潔、可擴充的 RPG 功能框架。

## 專案概述

- 核心版本：Minecraft 1.12.2
- Java：8
- API：Bukkit / Spigot / Paper 1.12.2
- 依賴：MythicMobs（可選，軟依賴）
- 專案名稱：LDAPI
- 主命令：`/ld`
- 兼容命令：`/LD`
- 已移除：`/led`

## 系統特色

- 自定義物品生成與識別
- 飾品系統與成長值
- 空間戒指與儲存空間
- 天賦系統
- 任務系統
- 寶箱系統
- 戰令系統
- 金幣商店
- 每日獎勵系統
- 玩家資料持久化
- 透過 YAML 分離配置

## 安裝方式

1. 建立一個 Paper 1.12.2 伺服器
2. 使用 Java 8 啟動伺服器
3. 編譯專案：

   ```bash
   mvn clean package
   ```

4. 將輸出的 JAR 放入 `plugins/` 目錄

   ```text
   target/LDAPI-1.0.0.jar
   ```

5. 啟動伺服器一次，讓插件生成資料夾
6. 編輯 `plugins/LDAPI/config/` 下的 YAML 配置
7. 再次啟動伺服器，或輸入：

   ```text
   /ld reload
   ```

## 主要指令

```text
/ld
/ld info
/ld reload
/ld give <item-id>
/ld items
/ld gold
/ld rank
/ld bp
/ld admin reload
/ld admin setlevel <玩家> <飾品ID> <等級>
/ld admin addgrowth <玩家> <飾品ID> <數值>
/ld admin resetdata <玩家>

/sr 或 /ldsr
/talent 或 /ldtalent
/task 或 /ldtask
/crate 或 /ldcrate
```

## 指令詳細使用教學

### 1. /ld

顯示 LDAPI 主幫助，列出常用指令與功能。通常玩家第一次使用時可直接輸入：

```text
/ld
```

它會顯示類似：

```text
=== LDAPI 1.0.0 ===
/ld info - 查看插件資訊
/ld reload - 重新載入配置
/ld give <item-id> - 給予物品
/ld items - 列出自定義物品
/ld gold - 檢查金幣
/ld rank - 查看稱號
/ld bp - 戰令進度
```

### 2. /ld info

查看插件版本、核心資訊與 MythicMobs 狀態。

```text
/ld info
```

輸出範例：

```text
[LDAPI] 版本：1.0.0
核心：Minecraft 1.12.2 Paper
MythicMobs: 已偵測
```

### 3. /ld reload

重新載入配置文件。適合在修改 YAML 後使用。

```text
/ld reload
```

需求：`ldapi.admin` 權限。

### 4. /ld give <item-id>

給予玩家自定義物品。

範例：

```text
/ld give growth_core
```

前提是 `items.yml` 中存在 `growth_core` 物品定義。

### 5. /ld items

列出所有可給予的自定義物品 ID。

```text
/ld items
```

這會列出所有已載入物品的 ID，例如：

```text
/ld give growth_core
/ld give abyss_ring
```

### 6. /ld gold

查看目前玩家持有的金幣。

```text
/ld gold
```

### 7. /ld rank

查看玩家目前的稱號資訊。

```text
/ld rank
```

### 8. /ld bp

查看戰令進度與 XP 狀態。

```text
/ld bp
```

### 9. /ld admin ...

管理員用命令，通常需要 `ldapi.admin` 權限。

#### /ld admin reload

```text
/ld admin reload
```

強制重新載入所有配置。

#### /ld admin setlevel <玩家> <飾品ID> <等級>

例如：

```text
/ld admin setlevel Sky abyss_ring 10
```

設定指定玩家的飾品等級。

#### /ld admin addgrowth <玩家> <飾品ID> <數值>

例如：

```text
/ld admin addgrowth Sky abyss_ring 25
```

增加指定飾品的成長值。

#### /ld admin resetdata <玩家>

重置玩家的飾品資料與成長資料。

```text
/ld admin resetdata Sky
```

## 其他系統指令

### /sr 或 /ldsr

開啟空間戒指相關功能介面。

```text
/sr
```

### /talent 或 /ldtalent

開啟天賦系統介面。

```text
/talent
```

### /task 或 /ldtask

開始或檢查任務狀態。

```text
/task
```

### /crate 或 /ldcrate

打開寶箱介面。

```text
/crate
```

## 配置文件使用教學

專案的 YAML 配置位於：

```text
plugins/LDAPI/config/
```

### 1. general.yml

用於設定前綴、除錯、Lore 識別規則。

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
```

用途：
- 讓插件訊息帶上前綴
- 啟用或關閉除錯
- 設定哪些 Lore Key 可以識別物品

### 2. items.yml

用於設定自定義物品。

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

使用方式：

```text
/ld give growth_core
```

### 3. attributes.yml

設定屬性，例如攻擊、防禦、生命等。

```yaml
attributes:
  attack:
    display-name: "&c攻擊"
    default: 0
    min: 0
    max: 1000000
```

這些數值會被其他模組讀取，例如飾品增益與天賦增益。

### 4. jewelry.yml

定義飾品與成長規則。

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
```

這裡可以設定：
- 飾品名稱
- 材質
- 基礎屬性
- 每級成長值
- 是否無限等級

### 5. shop.yml

設定商店的商品與價格。

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
```

玩家在商店購買後，直接獲得指定物品。

### 6. dailyreward.yml

設定每日獎勵。

```yaml
dailyreward:
  enabled: true
  gold: 100
  xp: 25
  cooldown-hours: 24
  message: "&e[每日獎勵] &7已領取：+{gold} 金幣，+{xp} 經驗。"
```

使用方式：
- 玩家每天可領一次
- 按時間冷卻，通常 24 小時

### 7. tests.yml

測試配置，方便檢查是否有構造錯誤。

```yaml
tests:
  required-files: [general.yml, items.yml, attributes.yml, jewelry.yml, spaces.yml, commands.yml]
```

## 玩家資料保存位置

```text
plugins/LDAPI/playerdata/
```

每位玩家一份 YAML，通常以 UUID 為名稱：

```text
plugins/LDAPI/playerdata/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.yml
```

內容包含：
- 金幣
- 稱號
- 天賦
- 戰令
- 飾品等級
- 成長點數
- 在線時間

## 伺服器使用流程

### 新手流程

1. 啟動伺服器
2. 輸入 `/ld`
3. 查看幫助
4. 進入 `/ld info` 確認版本
5. 依照需要編輯 `config/*.yml`
6. 輸入 `/ld reload`
7. 測試 `/ld give growth_core`
8. 測試寶箱、天賦、飾品與戰令

### 管理員流程

1. 給予管理員權限：

   ```text
   ldapi.admin
   ```

2. 使用：

   ```text
   /ld admin reload
   /ld admin setlevel <玩家> <飾品ID> <等級>
   /ld admin addgrowth <玩家> <飾品ID> <數值>
   ```

3. 監控玩家資料與配置是否正確

## 常見問題與排錯

### 1. /ld 無反應

確認：
- 伺服器已正確載入插件
- `plugin.yml` 有 `ldapi` 指令
- `aliases: [ld]` 存在
- 重啟伺服器後再測試

### 2. 物品無法識別

確認：
- item 的 Lore 格式正確
- `general.yml` 中的 `id-keys` 包含對應 key
- 不要只依賴中文名稱作為唯一識別

### 3. 配置沒有生效

執行：

```text
/ld reload
```

如果仍無效，檢查：
- YAML 縮排是否正確
- 是否寫在 `plugins/LDAPI/config/` 下
- 是否有語法錯誤

### 4. 玩家資料丟失

避免：
- 玩家在線時直接刪除 `playerdata`
- 不在伺服器關機前強制終止 Java

建議：
- 定期備份 `plugins/LDAPI/`

## 開發建議

- 統一使用英文 ID，避免中文 ID 導致錯誤
- 顯示名稱可使用中文，但 ID 不建議中文化
- 新增功能請拆分為獨立 Manager
- 每次修改配置後務必用 `/ld reload`
- 伺服器更新前先備份整個 `plugins/LDAPI/`

## 建置與發行

```bash
mvn clean package
```

生成檔案：

```text
target/LDAPI-1.0.0.jar
```

## 版本說明

- 版本：1.0.0
- 核心：Minecraft 1.12.2 Paper
- 命令：`/ld`
- 相容：`/LD`
- 已移除：`/led`

這份說明適合作為插件的官方使用手冊與維護文件。
