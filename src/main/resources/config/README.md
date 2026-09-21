# LDAPI 所有配置檔案範例

本目錄的範例已放在 `src/main/resources/config/`，伺服器首次啟動時會複製到 `plugins/LDAPI/config/`。完整註解與欄位解釋請看：

- [`docs/詳細配置範例.md`](詳細配置範例.md)
- [`docs/MythicMobs整合教學.md`](MythicMobs整合教學.md)

使用原則：

1. 先備份配置。
2. 只修改需要的欄位。
3. 保持 YAML 空格縮排。
4. `/ld reload` 後觀察控制台。
5. 出現錯誤時先還原最近一次修改。
