# MythicMobs 整合教學

LDAPI 會在 MythicMobs 已載入時啟用 API bridge；未安裝時會安全跳過。整合不把 MythicMobs jar 打包進 LDAPI，適用 MythicMobs 4.x 與 Paper 1.12.2。

## 自訂技能

在 MythicMobs 的技能檔建立技能，例如：

```yaml
LDAPI_BOSS_DEATH:
  Skills:
  - message{m="&cBoss defeated!"} @PlayersInRadius{r=20}
```

在 `plugins/LDAPI/config/mythicmobs.yml` 綁定：

```yaml
mythicmobs:
  events:
    ldapi_boss:
      death:
        skill: LDAPI_BOSS_DEATH
```

事件收到後，LDAPI 會透過 MythicMobs APIHelper 呼叫技能。

## 怪物掉落綁定 RPG

```yaml
mythicmobs:
  events:
    ldapi_boss:
      death:
        gold: 500
        battlepass-xp: 50
        item: growth_core
```

擊殺者可獲得金幣、戰令 XP 與 `items.yml` 中的物品。怪物 ID 必須與 MythicMobs 事件回傳的 mob type 相同。

## 事件觸發

支援的事件配置：

- `spawn.skill`：怪物生成時呼叫技能
- `death.skill`：怪物死亡時呼叫技能
- `death.gold`：擊殺獎勵金幣
- `death.battlepass-xp`：擊殺獎勵戰令 XP
- `death.item`：擊殺獎勵物品 ID

## 測試流程

1. 安裝 MythicMobs 4.13.0。
2. 啟動 Paper 1.12.2，確認控制台出現 `MythicMobs API 整合已啟用`。
3. 建立 MythicMobs 怪物與技能。
4. 在 `mythicmobs.yml` 加入相同 ID。
5. 執行 `/ld reload`；修改 MythicMobs 技能後建議重啟或使用其自身 reload。
6. 召喚怪物並擊殺，確認技能、金幣、物品與戰令 XP。

注意：MythicMobs 不同小版本的事件方法名稱可能不同；若控制台顯示 API 不相容，請使用相容的 4.x 版本並提供啟動日誌以便診斷。
