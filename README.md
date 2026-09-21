# LDAPI

> Minecraft 1.12.2 Paper 中文 RPG 核心｜Java 8｜MythicMobs 4.13.0 整合

## 版本資訊

- 版本：`1.0.0`
- 核心：Paper 1.12.2
- Java：8
- 主指令：`/ld`
- Bukkit 命令名稱：`ldapi`
- `/LD`：Bukkit 命令不分大小寫，可用
- `/led`：已移除，不再註冊
- MythicMobs：可選軟依賴

## 功能

- 自訂物品與 Lore ID 識別
- 飾品、屬性與無限成長
- 空間戒指與魂靈儲存
- 天賦、任務、寶箱、稱號
- BattlePass、金幣、商店、每日獎勵
- 玩家資料 YAML 持久化
- MythicMobs 技能、生成、死亡事件與 RPG 獎勵整合

## 安裝

1. 準備 Paper 1.12.2 與 Java 8。
2. 若需要 MythicMobs 整合，安裝 MythicMobs 4.13.0。
3. 在專案根目錄執行：

   ```bash
   mvn clean package
   ```

4. 將 `target/LDAPI-1.0.0.jar` 放入伺服器 `plugins/`。
5. 啟動一次伺服器，確認 `plugins/LDAPI/` 已生成。
6. 編輯 `plugins/LDAPI/config/` 內的 YAML。
7. 重啟，或由有 `ldapi.admin` 權限的管理員執行 `/ld reload`。

## 指令

```text
/ld                         顯示幫助
/ld info                    顯示版本、核心與 MythicMobs 狀態
/ld reload                  重載配置（ldapi.admin）
/ld items                   列出物品 ID
/ld give <item-id>          給予物品
/ld gold                    查看金幣
/ld rank                    查看稱號
/ld bp                      查看戰令
/ld admin reload            管理員重載
/ld admin setlevel <玩家> <飾品ID> <等級>
/ld admin addgrowth <玩家> <飾品ID> <數值>
/ld admin resetdata <玩家>

/sr、/ldsr                 空間戒指
/talent、/ldtalent         天賦
/task、/ldtask             任務
/crate、/ldcrate           寶箱
```

## 配置檔案

```text
plugins/LDAPI/
├─ config.yml                         主配置與舊版相容設定
├─ config/
│  ├─ general.yml                     前綴、Lore ID
│  ├─ items.yml                       自訂物品
│  ├─ attributes.yml                  屬性
│  ├─ jewelry.yml                     飾品與成長
│  ├─ spaces.yml                      空間戒指
│  ├─ commands.yml                    指令文案
│  ├─ tests.yml                       測試開關
│  ├─ shop.yml                        金幣商店
│  ├─ dailyreward.yml                 每日獎勵
│  └─ mythicmobs.yml                  MythicMobs 綁定
├─ playerdata/<UUID>.yml              玩家資料
└─ logs/                              插件記錄
```

> YAML 嚴格使用空格縮排，禁止 Tab。修改前請備份整個 `plugins/LDAPI/`。

## MythicMobs 快速範例

`config/mythicmobs.yml`：

```yaml
mythicmobs:
  enabled: true
  events:
    ldapi_boss:
      death:
        chance: 1.0
        gold: 500
        battlepass-xp: 50
        item: growth_core
        skill: LDAPI_BOSS_DEATH
```

其中 `ldapi_boss` 必須與 MythicMobs 怪物的 internal name 一致，`growth_core` 必須存在於 `items.yml`。詳見 [`docs/MythicMobs整合教學.md`](docs/MythicMobs整合教學.md)。

## 測試流程

```text
/ld info
/ld items
/ld give growth_core
/ld gold
/ld bp
```

完成配置後，測試普通怪物、MythicMobs 怪物、玩家退出登入及伺服器重啟，確認資料仍存在。

## 排錯

- `/ld` 不存在：確認插件成功載入、`plugin.yml` 有 `ldapi` 與 alias `ld`。
- 配置無效：檢查 YAML 縮排，再執行 `/ld reload`。
- 物品無法識別：確認 Lore 使用 `item-id: xxx`，且 key 在 `general.yml`。
- MythicMobs 未觸發：確認插件名稱、internal name、技能名稱與控制台 API 訊息。
- 玩家資料異常：停止伺服器後備份並檢查 `playerdata/<UUID>.yml`，不要在線刪除。

## 文件

- [完整使用教學](docs/完整使用教學.md)
- [詳細配置說明](docs/詳細配置範例.md)
- [MythicMobs 整合教學](docs/MythicMobs整合教學.md)
- [Release Notes](docs/RELEASE_NOTES_1.0.0.md)

## 建置

```bash
mvn clean package
```

輸出：`target/LDAPI-1.0.0.jar`
