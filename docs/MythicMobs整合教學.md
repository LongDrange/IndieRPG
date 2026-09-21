# MythicMobs 整合教學

LDAPI 會在 MythicMobs 4.x 已載入時啟用 API bridge；未安裝或 API 不相容時會安全停用。MythicMobs jar 不會被打包進 LDAPI。

## 安裝與確認

1. 使用 Paper 1.12.2、Java 8。
2. 安裝 MythicMobs 4.13.0。
3. 啟動伺服器，確認控制台出現 `MythicMobs API 整合已啟用`。
4. 修改 `plugins/LDAPI/config/mythicmobs.yml` 後執行 `/ld reload`。

## 技能觸發

在 MythicMobs 技能檔建立：

```yaml
LDAPI_BOSS_SPAWN:
  Skills:
  - message{m="&cBoss spawned!"} @PlayersInRadius{r=20}
LDAPI_BOSS_DEATH:
  Skills:
  - message{m="&6Boss defeated!"} @PlayersInRadius{r=20}
```

在 LDAPI 配置綁定：

```yaml
mythicmobs:
  events:
    ldapi_boss:
      spawn:
        skill: LDAPI_BOSS_SPAWN
      death:
        skill: LDAPI_BOSS_DEATH
```

技能由 MythicMobs APIHelper 以怪物本身作為 caster 施放。

## RPG 掉落與獎勵

```yaml
death:
  chance: 0.5
  gold: 500
  battlepass-xp: 50
  item: growth_core
  command: "say {player} defeated the boss"
```

- `chance` 使用 0.0–1.0，例如 `0.5` 是 50%。
- `gold` 寫入 LDAPI 玩家金幣。
- `battlepass-xp` 增加戰令 XP。
- `item` 必須是 `items.yml` 中存在的 ID。
- `command` 由控制台執行，`{player}` 會替換為擊殺者名稱。

## 重要限制

- `events` 下的 key 必須是 MythicMobs mob 的 internal name，區分大小寫前會轉為小寫比對。
- 死亡事件沒有玩家擊殺者時，不發放 RPG 獎勵，但仍可執行 death skill。
- MythicMobs 自己的 drops 與 LDAPI 額外獎勵可以同時存在；避免重複配置相同物品。
- 修改 MythicMobs 技能後，使用 MythicMobs 自身的 reload 或重啟伺服器。

## 測試清單

- [ ] 控制台顯示 API bridge 啟用。
- [ ] 召喚 `ldapi_boss`，確認 spawn skill。
- [ ] 玩家擊殺後確認 death skill。
- [ ] 確認金幣、戰令 XP、`growth_core` 掉落。
- [ ] 測試 `chance: 0.0` 不發獎勵、`chance: 1.0` 必定發獎勵。
- [ ] 移除 MythicMobs 後確認 LDAPI 仍能啟動。
