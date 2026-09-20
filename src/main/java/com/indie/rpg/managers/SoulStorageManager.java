package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SoulStorageManager {

    private final IndieRPG plugin;
    private static final int MAX_PAGES = 100;

    public SoulStorageManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openSoulStorage(Player player, int page) {
        int configuredSize = plugin.getConfig().getInt("soulstorage.size", -1);
        int rowsPerPage = plugin.getConfig().getInt("soulstorage.rows-per-page", 6);

        if (configuredSize == -1) {
            page = Math.max(0, Math.min(page, MAX_PAGES - 1));
            Inventory inv = Bukkit.createInventory(null, 54,
                    ChatColor.translateAlternateColorCodes('&', "&5&lSoul Storage &7[Page " + (page + 1) + "/" + MAX_PAGES + "]"));
            player.openInventory(inv);
            player.sendMessage(ChatColor.GRAY + "[SoulStorage] Page " + (page + 1) + " / " + MAX_PAGES + " (Infinite)");
        } else {
            int size = Math.min(configuredSize, 54);
            Inventory inv = Bukkit.createInventory(null, size,
                    ChatColor.translateAlternateColorCodes('&', "&5&lSoul Storage &7[" + size + " slots]"));
            player.openInventory(inv);
            player.sendMessage(ChatColor.GRAY + "[SoulStorage] Opening soul storage...");
        }
    }

    public boolean matchesLore(java.util.List<String> itemLores) {
        java.util.List<String> triggers = plugin.getConfig().getStringList("soulstorage.trigger-items");
        for (String lore : itemLores) {
            String strippedLore = ChatColor.stripColor(lore).trim().toLowerCase();
            for (String trigger : triggers) {
                String strippedTrigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', trigger)).trim().toLowerCase();
                if (strippedLore.contains(strippedTrigger)) {
                    return true;
                }
            }
        }
        return false;
    }
}
