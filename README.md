# LDAPI 1.0.0

LDAPI 是一個以 Minecraft 1.12.2 Paper 為核心的中文 RPG 插件，目標是提供穩定、簡潔、易擴充的 RPG 功能框架。

## 核心特色

- Minecraft 1.12.2 / Paper 1.12.2 相容
- Java 8 編譯
- 使用 Bukkit 1.12.2 API，不使用 1.13+ 新 API
- 支援自訂物品、排行、戰令、飾品成長、任務和寶箱
- 配置獨立化，全部放在 `plugins/LDAPI/config/`
- MythicMobs 為軟依賴，可選安裝
- `ld` 為主要命令，`/LD` 也可使用
- `/led` 已取消，不再保留

## 安裝方式

1. 建立 Paper 1.12.2 伺服器
2. 使用 Java 8 啟動
3. 編譯：
   ```bash
   mvn clean package
   ```
4. 將生成的 `target/LDAPI-1.0.0.jar` 放進 `plugins/`
5. 啟動伺服器一次以生成資料夾
6. 編輯 `plugins/LDAPI/config/` 內容
7. 使用 `/ld reload` 或重啟伺服器套用設定

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

## 資料夾結構

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

## 配置規範

- `id`、`namespace`、`material`、`permission` 建議保留英文
- 顯示名稱、Lore、GUI、訊息可以使用中文
- YAML 縮排必須為空白，不得使用 Tab
- 不要使用 1.13+ 的 `PersistentDataContainer`
- 物品識別的推薦方式：`item-id` / `jewelry-id` / `mythic-id` / `namespace`

## 物品識別範例

```text
&7item-id: growth_core
&7jewelry-id: abyss_ring
&7mythic-id: AbyssRing
&7namespace: ldapi:jewelry
```

## 伺服器兼容與排錯

### 命令不顯示

檢查 `plugin.yml` 是否有：

```yaml
commands:
  ldapi:
    aliases: [ld]
```

### 配置不生效

執行：

```text
/ld reload
```

若仍不生效，請檢查 YAML 是否有縮排問題，並確認配置位於：

```text
plugins/LDAPI/config/
```

### Lore 無法判定

確認每一行都符合 `key: value` 格式，並位於 `general.yml` 的 `id-keys` 中。

## 維護建議

- 每次升級前請備份 `plugins/LDAPI/`
- 玩家在線時不要直接刪除 `playerdata`
- 新功能請先拆成獨立 Manager
- 修改配置後務必使用 `/ld reload`

## 建置命令

```bash
mvn clean package
```

輸出檔：

```text
target/LDAPI-1.0.0.jar
```
