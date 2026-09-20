package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TalentManager {

    private final IndieRPG plugin;

    public TalentManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openTalentMenu(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Inventory inv = Bukkit.createInventory(null, 27, "§d§lTalent System §7[Points: " + data.talentPoints + "]");

        inv.setItem(11, createTalentItem(Material.IRON_SWORD, "§cAttack Talent",
                Arrays.asList("§7Level: §f0/10", "§7Each level: +5 Attack", "§7Cost: 1 Point")));

        inv.setItem(13, createTalentItem(Material.IRON_CHESTPLATE, "§9Defense Talent",
                Arrays.asList("§7Level: §f0/10", "§7Each level: +5 Defense", "§7Cost: 1 Point")));

        inv.setItem(15, createTalentItem(Material.APPLE, "§aHealth Talent",
                Arrays.asList("§7Level: §f0/10", "§7Each level: +10 Max HP", "§7Cost: 1 Point")));

        inv.setItem(22, createTalentItem(Material.TNT, "§eReset Talents",
                Arrays.asList("§7Refund 80% of points")));

        player.openInventory(inv);
    }

    private ItemStack createTalentItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void addTalentPoint(Player player, int amount) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.talentPoints += amount;
        player.sendMessage("§d[Talent] §7+" + amount + " talent points!");
    }
}
