package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SpaceRingManager {

    private final IndieRPG plugin;

    public SpaceRingManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openSpaceRing(Player player, String matchedName) {
        List<String> triggerItems = plugin.getConfig().getStringList("spacering.trigger-items");
        List<Integer> sizes = plugin.getConfig().getIntegerList("spacering.sizes");

        int tierIndex = -1;
        String strippedInput = ChatColor.stripColor(matchedName).trim().toLowerCase();
        for (int i = 0; i < triggerItems.size(); i++) {
            String strippedTrigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', triggerItems.get(i))).trim().toLowerCase();
            if (strippedInput.contains(strippedTrigger)) {
                tierIndex = i;
                break;
            }
        }

        int size = 27;
        if (tierIndex >= 0 && tierIndex < sizes.size()) {
            size = sizes.get(tierIndex);
        }

        openRing(player, size, tierIndex);
    }

    public boolean matchesLore(List<String> itemLores) {
        List<String> triggerItems = plugin.getConfig().getStringList("spacering.trigger-items");
        for (String lore : itemLores) {
            String strippedLore = ChatColor.stripColor(lore).trim().toLowerCase();
            for (String trigger : triggerItems) {
                String strippedTrigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', trigger)).trim().toLowerCase();
                if (strippedLore.contains(strippedTrigger)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void openRing(Player player, int size, int tier) {
        size = Math.min(size, 54);
        Inventory inv = Bukkit.createInventory(null, size,
                ChatColor.translateAlternateColorCodes('&', "&d&lSpace Ring &7[" + size + " slots]"));
        player.openInventory(inv);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("general.prefix", "&d[IndieRPG] &r") + "&7Opening space ring..."));
    }
}
