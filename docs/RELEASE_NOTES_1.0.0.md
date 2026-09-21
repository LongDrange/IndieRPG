# LDAPI 1.0.0 Release Notes

## 新增

- Paper 1.12.2 / Java 8 RPG 核心
- `/ld` 主命令與管理員命令
- 自訂物品、Lore ID、飾品成長、屬性
- 任務、天賦、寶箱、稱號、戰令、商店與每日獎勵
- 玩家資料 YAML 持久化
- MythicMobs 4.13.0 軟依賴設計
- MythicMobs 怪物事件對應技能、金幣、BattlePass XP、LDAPI 物品與控制台命令
- 完整中文配置範例與使用教學

## 相容性

- Minecraft：1.12.2
- Paper：1.12.2
- Java：8
- MythicMobs：4.13.0（可選）

## 升級注意

1. 升級前備份 `plugins/LDAPI/`。
2. 不要用新版本 API 替換 1.12.2 伺服器的 API。
3. 修改 YAML 後先檢查縮排，再執行 `/ld reload`。
4. MythicMobs 的 `internal name` 必須和 `mythicmobs.yml` 完全對應。

## 已知限制

- 不使用 1.13+ `PersistentDataContainer`。
- MythicMobs API 在不同 4.x 小版本可能有方法差異；若控制台提示 API 不相容，請使用 4.13.0 並提供啟動日誌。
- MythicMobs 沒有玩家擊殺者時不發放玩家 RPG 獎勵。
