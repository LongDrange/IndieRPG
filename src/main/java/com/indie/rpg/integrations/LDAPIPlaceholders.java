package com.indie.rpg.integrations;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;

/**
 * PlaceholderAPI 註冊器。
 * 檢查 PAPI 是否存在，再交給 LDAPIPlaceholderExpansion 註冊。
 * 這樣沒裝 PAPI 的伺服器不會觸發 NoClassDefFoundError。
 */
public class LDAPIPlaceholders {
    private final IndieRPG plugin;
    private boolean registered = false;

    public LDAPIPlaceholders(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().info("未偵測到 PlaceholderAPI，跳過變數註冊");
            return;
        }
        try {
            new LDAPIPlaceholderExpansion(plugin).register();
            registered = true;
            plugin.getLogger().info("PlaceholderAPI 變數已註冊（識別碼：ldapi）");
        } catch (Throwable t) {
            plugin.getLogger().warning("註冊 PlaceholderAPI 失敗：" + t.getMessage());
        }
    }

    public boolean isRegistered() {
        return registered;
    }
}