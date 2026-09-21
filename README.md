# LDAPI v1.0.0

Minecraft 1.12.2 Paper 中文 RPG API，支援 MythicMobs 4.13.0。

## 新名稱與指令前綴

- 插件名稱：`LDAPI`
- 版本：`1.0.0`
- 主指令：`/ldapi`
- 短指令：`/ld`
- 兼容別名：`/led`

所有主指令都可以使用 `/ld` 前綴：

```text
/ld info
/ld reload
/ld give <物品ID>
/ld items
/ld gold
/ld rank
/ld bp
/ld admin reload
/ld admin setlevel <玩家> <飾品ID> <等級>
/ld admin addgrowth <玩家> <飾品ID> <數值>
/ld admin resetdata <玩家>
```

其他系統也提供 LD 別名：

```text
/ldsr
/ldtalent
/ldtask
/ldcrate
```

`/sr`、`/talent`、`/task`、`/crate` 仍然保留，方便舊伺服器升級。

## 核心識別備注

`id`、`namespace`、`material`、權限節點與 MythicMobs ID 保留英文；顯示名稱、Lore、GUI 與訊息可使用繁體中文。
