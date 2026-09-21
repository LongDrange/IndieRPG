# LDAPI YAML 詳細配置與備注

本文件說明 `plugins/LDAPI/` 下所有 YAML 的用途、欄位與完整範例。

## 使用前必讀

- 使用 Paper 1.12.2、Java 8。
- YAML 只能使用空格縮排，禁止使用 Tab。
- ID 建議使用小寫英文、數字與底線，例如 `growth_core`。
- 顯示名稱與 Lore 可以使用中文和 `&` 顏色碼。
- 修改後執行 `/ld reload`；若插件沒有重新載入某項功能，請重啟伺服器。
- 修改前請備份 `plugins/LDAPI/`。
- 不要把不存在的 Material、物品 ID 或權限名稱寫進配置。

---

## 一、主配置：`config.yml`

此文件保存主 RPG 系統設定，包括天賦、任務、寶箱、稱號、戰令、經濟、商店、卡牌、圖鑑、魂珠與舊版相容設定。

```yaml
# 全局訊息設定
# prefix：插件訊息的前綴；&d 是紫色，&r 是重置顏色。
general:
  prefix: "&d[LDAPI] &r"
  # true 會輸出額外除錯訊息；正式環境建議 false。
  debug: false

# 空間戒指設定
spacering:
  # 可用名稱觸發的物品。若使用自訂物品，建議同時在 items.yml 加入唯一 ID。
  trigger-items:
    - "&aBasic Space Ring"
    - "&9Advanced Space Ring"
    - "&6Supreme Space Ring"
    - "&dLegendary Space Ring"
  # 不同戒指對應的容量；建議使用 9、27、54 等 9 的倍數。
  sizes: [9, 27, 54, 54]
  # true：關閉介面時保存物品；建議保持 true。
  save-contents: true

# 魂靈儲存設定
soulstorage:
  enabled: true
  trigger-items: ["&5Soul Storage Orb", "&dSoul Pearl"]
  # -1 代表依頁面配置；不要填 0。
  size: -1
  rows-per-page: 6

# 天賦系統
talent:
  enabled: true
  menu-title: "&d&l天賦系統 &7[點數：{points}]"
  # 必須是 9 的倍數，最大建議 54。
  gui-size: 45
  # 重置按鈕所在槽位，從 0 開始計算。
  reset-slot: 40
  # 重置時返還已花點數的百分比。
  reset-refund-percent: 80
  effects:
    attack-per-level: 0.5
    defense-per-level: 0.5
    health-per-level: 2
    # 0.02 = 2%，不是 0.02%。
    crit-per-level: 0.02
    lifesteal-per-level: 0.01
  trees:
    attack:
      display-name: "&c&l攻擊"
      # 必須是 1.12.2 可用的 Bukkit Material。
      icon: DIAMOND_SWORD
      max-level: 20
      points-per-level: 1
      lore: ["&7每級：&c+0.5 傷害", "&7目前：&f{level}/{max}"]
      slot: 11
    defense:
      display-name: "&9&l防禦"
      icon: IRON_CHESTPLATE
      max-level: 20
      points-per-level: 1
      lore: ["&7每級：&9+0.5 防禦", "&7目前：&f{level}/{max}"]
      slot: 13
    health:
      display-name: "&a&l生命"
      icon: APPLE
      max-level: 20
      points-per-level: 1
      lore: ["&7每級：&a+2 生命", "&7目前：&f{level}/{max}"]
      slot: 15

# 任務設定
task:
  enabled: true
  required-kills: 15
  rewards:
    gold: 150
    talent-points: 5
    battlepass-xp: 50
  start-message: "&a[任務] &7擊殺 {count} 隻怪物。"
  progress-message: "&a[任務] &7進度：{kills}/{count}"
  complete-message: "&6[任務] &e完成！+{gold} 金幣"

# 寶箱設定
crate:
  enabled: true
  trigger-items:
    common: ["&7Common Crate"]
    rare: ["&9Rare Crate"]
    legendary: ["&6Legendary Crate"]
  # 每一組 chance 建議總和為 1.0。
  rewards:
    common:
      - {chance: 0.60, gold: 10, talent-points: 0, battlepass-xp: 5}
      - {chance: 0.30, gold: 50, talent-points: 0, battlepass-xp: 10}
      - {chance: 0.10, gold: 100, talent-points: 1, battlepass-xp: 20}
    rare:
      - {chance: 0.50, gold: 100, talent-points: 1, battlepass-xp: 20}
      - {chance: 0.35, gold: 250, talent-points: 2, battlepass-xp: 40}
      - {chance: 0.15, gold: 500, talent-points: 3, battlepass-xp: 80}
    legendary:
      - {chance: 0.40, gold: 500, talent-points: 3, battlepass-xp: 80}
      - {chance: 0.35, gold: 1000, talent-points: 5, battlepass-xp: 150}
      - {chance: 0.25, gold: 2500, talent-points: 10, battlepass-xp: 300}

# 稱號設定
rank:
  enabled: true
  # 物品顯示名稱對應稱號 ID。
  items:
    "Warrior Title": warrior
    "Knight Title": knight
    "Legend Title": legend
  ranks:
    default: {display: "&7&l[新手]", chat-prefix: "&7[新手] "}
    warrior: {display: "&c&l[戰士]", chat-prefix: "&c[戰士] "}
    knight: {display: "&f&l[騎士]", chat-prefix: "&f[騎士] "}
    legend: {display: "&6&l[傳說]", chat-prefix: "&6[傳說] "}

# 戰令設定
battlepass:
  enabled: true
  xp-per-level: 100
  max-level: 100
  kill-xp: 5
  milestone-every: 10
  milestone-rewards: {talent-points: 5, gold: 500}
  gold-per-level: 10

# 經濟系統
economy:
  enabled: true
  starting-gold: 0
```

> 主配置的完整系統區塊可直接參考目前的 `src/main/resources/config.yml`。若某個 Manager 讀取的是主配置，請不要只修改同名的分離配置檔。

---

## 二、`config/general.yml`

```yaml
general:
  # 所有訊息前綴。
  prefix: "&d[LDAPI] &r"
  # 除錯模式；正式伺服器建議關閉。
  debug: false
  # 文檔使用的命令前綴。
  command-prefix: ld
  lore:
    # 是否啟用 Lore 身份識別。
    enabled: true
    # 比對時忽略顏色碼。
    ignore-color: true
    # Lore 中允許作為身份標記的 key。
    id-keys:
      - indierpg-id
      - item-id
      - mythic-id
      - namespace
      - jewelry-id
      - card-id
      - soul-bead-id
```

Lore 建議格式：`&7item-id: growth_core`。不要只依靠顯示名稱辨識物品，因為玩家可以改名或不同物品重複使用名稱。

---

## 三、`config/items.yml`

```yaml
items:
  # ID 必須唯一；建議只使用小寫英文、數字與底線。
  growth_core:
    id: growth_core
    namespace: ldapi:item
    # 使用 1.12.2 存在的 Material。
    material: EMERALD
    display-name: "&b成長核心"
    lore:
      - "&7item-id: growth_core"
      - "&7namespace: ldapi:item"
      - "&7用途：提升飾品成長"
    amount: 1

  abyss_ring:
    id: abyss_ring
    namespace: ldapi:jewelry
    material: DIAMOND
    display-name: "&5深淵戒指"
    lore:
      - "&7jewelry-id: abyss_ring"
      - "&7namespace: ldapi:jewelry"
    amount: 1
```

測試：`/ld give growth_core`。如果顯示未知物品，檢查 ID、縮排與 `/ld reload`。

---

## 四、`config/attributes.yml`

```yaml
attributes:
  # default 是初始值，min/max 是允許範圍。
  attack:
    display-name: "&c攻擊力"
    default: 0
    min: 0
    max: 1000000
  defense:
    display-name: "&9防禦力"
    default: 0
    min: 0
    max: 1000000
  health:
    display-name: "&a生命值"
    default: 20
    min: 0
    max: 1000000
```

屬性 ID 必須與飾品或天賦的 `base-attributes`、`per-level` 使用相同拼法。

---

## 五、`config/jewelry.yml`

```yaml
jewelry:
  enabled: true
  # 飾品 GUI 槽位，使用 9 的倍數。
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
        # -1 表示不限制等級；例如 50 表示最高 50 級。
        max-level: -1
        sources: [online-time, real-time, special-item]
        per-level:
          "1": {attack: 1.0, defense: 0.5}
          "2": {attack: 1.5, defense: 0.8}
          "3": {attack: 2.5, defense: 1.2}
        # 未列出的等級使用此數值。
        default-per-level: {attack: 1.0, defense: 0.5}
```

測試：`/ld admin setlevel 玩家 abyss_ring 10` 與 `/ld admin addgrowth 玩家 abyss_ring 25`。

---

## 六、`config/spaces.yml`

```yaml
spaces:
  enabled: true
  default-space: basic
  definitions:
    basic:
      id: basic
      display-name: "&a基礎空間"
      # 必須是 9 的倍數。
      size: 27
      permission: ""
    advanced:
      id: advanced
      display-name: "&9進階空間"
      size: 54
      permission: ldapi.space.advanced
  # 建議使用 item-id 標記，不要只使用名稱。
  trigger-items:
    - "item-id: space_ring_basic"
    - "item-id: space_ring_advanced"
```

---

## 七、`config/shop.yml`

```yaml
shop:
  enabled: true
  menu-title: "&6&lLDAPI 商店 &7[金幣：{gold}]"
  items:
    growth_core:
      price: 80
      material: EMERALD
      display-name: "&b成長核心"
      # 必須對應 items.yml 的 ID。
      item: growth_core
      slot: 11
    abyss_ring:
      price: 300
      material: DIAMOND
      display-name: "&5深淵戒指"
      item: abyss_ring
      slot: 15
```

`price` 不可為負數；`slot` 從 0 開始計算，GUI 槽位不要重複。

---

## 八、`config/dailyreward.yml`

```yaml
dailyreward:
  enabled: true
  # 兩次領取的冷卻時間，單位為小時。
  cooldown-hours: 24
  message: "&e[每日獎勵] &7第 {day} 天：+{gold} 金幣，+{xp} XP"
  rewards:
    day1: {gold: 100, xp: 25, item: growth_core}
    day2: {gold: 150, xp: 30, item: ""}
    day3: {gold: 200, xp: 50, item: growth_core}
    day7: {gold: 1000, xp: 200, item: abyss_ring}
```

`item: ""` 代表該天不派發物品；如果填寫 ID，該 ID 必須存在於 `items.yml`。

---

## 九、`config/mythicmobs.yml`

```yaml
mythicmobs:
  # false 時停用 MythicMobs 事件處理，但 LDAPI 其他功能仍可使用。
  enabled: true
  api-version: "4.13.0"
  events:
    ldapi_boss:
      # 必須是 MythicMobs 怪物 internal name。
      spawn:
        skill: LDAPI_BOSS_SPAWN
      death:
        # 0.0 至 1.0；0.25 = 25%。
        chance: 1.0
        skill: LDAPI_BOSS_DEATH
        gold: 500
        battlepass-xp: 50
        # 對應 items.yml 的 ID。
        item: growth_core
        # 可留空；非空時由控制台執行。
        command: ""
    mythic_zombie:
      death:
        chance: 0.25
        gold: 25
        battlepass-xp: 10
        item: growth_core
```

注意：沒有玩家擊殺者時，LDAPI 不會發放玩家金幣、戰令 XP 或物品獎勵。

---

## 十、`config/commands.yml` 與 `config/tests.yml`

```yaml
# commands.yml
commands:
  main: ld
  aliases: [ld]
  messages:
    no-permission: "&c你沒有權限。"
    reload-success: "&aLDAPI 配置已重新載入。"
    unknown-item: "&c找不到物品：{id}"
    unknown-command: "&c未知指令，輸入 /ld 查看幫助。"
```

```yaml
# tests.yml
tests:
  # 是否啟用配置測試。
  enabled: true
  required-files:
    - general.yml
    - items.yml
    - attributes.yml
    - jewelry.yml
    - spaces.yml
    - commands.yml
    - shop.yml
    - dailyreward.yml
    - mythicmobs.yml
  verbose: false
```

---

## 配置完成後的測試順序

```text
/ld info
/ld reload
/ld items
/ld give growth_core
/ld gold
/ld bp
/sr
/talent
/task
/crate
```

MythicMobs 額外測試：

1. 確認控制台顯示 API 整合已啟用。
2. 確認 `events` 的 ID 與 MythicMobs internal name 相同。
3. 召喚怪物並確認 spawn skill。
4. 玩家擊殺怪物並確認 death skill、金幣、XP 與物品。
5. 將 `chance` 改為 `0.0` 和 `1.0` 測試機率。
