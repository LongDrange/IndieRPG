package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SoulStorageManager {

    private final IndieRPG plugin;

    public SoulStorageManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openSoulStorage(Player player, int page) {
        int slotsPerPage = plugin.getConfig().getInt("storage.slots-per-page", 45);
        int maxPages = getMaxPages(player);

        page = Math.max(0, Math.min(page, maxPages - 1));
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.translateAlternateColorCodes('&',
                        "&5&lSoul Storage &7[Page " + (page + 1) + "/" + maxPages + "]"));

        ItemStack prev = new ItemStack(Material.WOOD_BUTTON);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        prev.setItemMeta(prevMeta);
        inv.setItem(45, prev);

        ItemStack next = new ItemStack(Material.IRON_DOOR);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);

        player.openInventory(inv);
    }

    public int getMaxPages(Player player) {
        if (!plugin.getConfig().contains("storage.levels")) return 1;

        int maxPages = 1;
        for (String key : plugin.getConfig().getConfigurationSection("storage.levels").getKeys(false)) {
            String path = "storage.levels." + key;
            int pages = plugin.getConfig().getInt(path + ".pages", 1);
            String condition = plugin.getConfig().getString(path + ".condition", "NONE");
            String value = plugin.getConfig().getString(path + ".value", "");

            if (checkCondition(player, condition, value)) {
                maxPages = Math.max(maxPages, pages);
            }
        }
        return maxPages;
    }

    private boolean checkCondition(Player player, String condition, String value) {
        if (condition.equals("NONE")) return true;
        if (condition.equals("PERMISSION")) return player.hasPermission(value);
        if (condition.equals("XP_LEVEL")) return player.getLevel() >= Integer.parseInt(value);
        if (condition.equals("ITEM")) {
            String[] parts = value.split(":");
            Material mat = Material.matchMaterial(parts[0]);
            int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            return player.getInventory().contains(mat, amount);
        }
        return false;
    }

    public boolean matchesLore(List<String> itemLores) {
        List<String> triggers = plugin.getConfig().getStringList("storage.trigger-items");
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
