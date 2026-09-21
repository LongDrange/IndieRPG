package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/** 中文：空間戒指使用獨立 spaces.yml 的空間名稱；舊設定仍保留兼容。 */
public class SpaceRingManager {
    private final IndieRPG plugin;
    public SpaceRingManager(IndieRPG plugin) { this.plugin = plugin; }

    public void openSpaceRing(Player player, String matchedName) {
        List<Integer> sizes = plugin.getConfig().getIntegerList("spacering.sizes");
        int tier = 0;
        List<String> triggers = plugin.getConfig().getStringList("spacering.trigger-items");
        String input = ChatColor.stripColor(matchedName).toLowerCase();
        for (int i = 0; i < triggers.size(); i++) {
            String trigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', triggers.get(i))).toLowerCase();
            if (input.contains(trigger)) { tier = i; break; }
        }
        int size = tier < sizes.size() ? sizes.get(tier) : 27;
        openRing(player, size, tier);
    }

    public boolean matchesLore(List<String> lores) {
        for (String lore : lores) {
            String line = ChatColor.stripColor(lore).toLowerCase();
            for (String trigger : plugin.getConfig().getStringList("spacering.trigger-items")) {
                if (line.contains(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', trigger)).toLowerCase())) return true;
            }
        }
        return false;
    }

    public void openRing(Player player, int size, int tier) {
        size = Math.max(9, Math.min(size, 54));
        String title = plugin.getConfigManager().get("spaces").getString("spaces.main.display-name", "&a主空間");
        Inventory inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
        player.openInventory(inventory);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("general.prefix", "&d[IndieRPG] &r") + "&7已開啟空間戒指。"));
    }
}
