# LDAPI 1.0.0

LDAPI 是以 **Minecraft 1.12.2 Paper** 為主要核心的中文 RPG API，使用 Java 8 與 Spigot/Paper 1.12.2 API，不使用 1.13 以上專屬 API。

## 指令

主指令已統一為：

```text
/ldapi
/ld
/led
```

插件內部主指令名稱是 `ldapi`，`ld` 與 `led` 是別名。系統指令仍支援：

```text
/sr、/ldsr
/talent、/ldtalent
/task、/ldtask
/crate、/ldcrate
```

## 1.12.2 Paper 兼容注意

- 不使用 `PersistentDataContainer`。
- 物品識別使用顯示名稱、Lore、`mythic-id`、`item-id` 與 namespace。
- `Material` 必須使用 1.12.2 存在的名稱。
- 玩家資料使用獨立 YAML 文件保存。
- 在線時間每 60 秒記錄一次。
- 現實時間以秒保存，伺服器重啟後不會歸零。
- MythicMobs 使用軟依賴，未安裝時 LDAPI 仍可啟動。

## 建置

```bash
mvn clean package
```

產出檔案：

```text
target/LDAPI-1.0.0.jar
```

## 首次啟動後資料

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
│  └─ tests.yml
└─ playerdata/<UUID>.yml
```
