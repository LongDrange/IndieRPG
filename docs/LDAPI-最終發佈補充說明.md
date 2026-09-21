# LDAPI 最終發佈版補充說明

## 命令規範

- `ldapi` 是實際主命令名稱
- `ld` 是官方別名
- `LD` 因 Bukkit 命令大小寫不敏感，可直接使用
- `led` 已取消，不再保留

## 版本目標

- 版本：1.0.0
- 核心：1.12.2 Paper
- Java：8
- 兼容：MythicMobs 4.13.0（軟依賴）

## 核心工具

- `IndieRPG.java`：插件入口
- `CommandHandler.java`：命令處理
- `ConfigManager.java`：獨立 YAML 配置管理
- `PlayerDataManager.java`：玩家資料持久化
- `DailyRewardManager.java`：每日獎勵
- `ShopManager.java`：金幣商店
- `AttributeManager.java`：屬性定義
- `ItemManager.java`：自訂物品

## 發佈前檢查

1. `mvn clean package`
2. 檢查 `plugin.yml` 啟動命令
3. 確認 `ld` 指令可用
4. 確認 `led` 不再出現
5. 確認 config 除錯與 YAML 正確
