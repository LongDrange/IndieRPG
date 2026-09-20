package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ExchangeManager {

    private final IndieRPG plugin;

    public ExchangeManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openExchange(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.translateAlternateColorCodes('&', "&a&lItem Exchange"));

        if (plugin.getConfig().contains("exchange.rules")) {
            int slot = 10;
            for (String matName : plugin.getConfig().getConfigurationSection("exchange.rules").getKeys(false)) {
                String path = "exchange.rules." + matName;
                int cost = plugin.getConfig().getInt(path + ".cost", 1);
                String reward = plugin.getConfig().getString(path + ".reward", "");

                ItemStack item = new ItemStack(Material.matchMaterial(matName));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + matName + " Exchange");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cost: " + cost + " " + matName);
                lore.add(ChatColor.GRAY + "Reward: " + reward);
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to exchange!");
                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.setItem(slot, item);
                slot++;
            }
        }

        ItemStack trash = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta trashMeta = trash.getItemMeta();
        trashMeta.setDisplayName(ChatColor.DARK_RED + "Trash Can");
        trashMeta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Drop items here to delete"));
        trash.setItemMeta(trashMeta);
        inv.setItem(22, trash);

        player.openInventory(inv);
    }
}
