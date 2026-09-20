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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentManager {

    private final IndieRPG plugin;
    private final Map<String, TalentTree> trees = new HashMap<>();

    public TalentManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadTrees();
    }

    public void loadTrees() {
        trees.clear();
        if (plugin.getConfig().contains("talent.trees")) {
            for (String key : plugin.getConfig().getConfigurationSection("talent.trees").getKeys(false)) {
                String path = "talent.trees." + key;
                TalentTree tree = new TalentTree();
                tree.id = key;
                tree.displayName = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString(path + ".display-name", key));
                tree.icon = Material.matchMaterial(plugin.getConfig().getString(path + ".icon", "DIAMOND_SWORD"));
                tree.maxLevel = plugin.getConfig().getInt(path + ".max-level", 10);
                tree.pointsPerLevel = plugin.getConfig().getInt(path + ".points-per-level", 1);
                tree.slot = plugin.getConfig().getInt(path + ".slot", 11);
                tree.lore = plugin.getConfig().getStringList(path + ".lore");
                trees.put(key, tree);
            }
        }
        plugin.getLogger().info("Loaded " + trees.size() + " talent trees");
    }

    public void openTalentMenu(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int guiSize = plugin.getConfig().getInt("talent.gui-size", 45);
        String title = plugin.getConfig().getString("talent.menu-title", "&d&lTalent &7[Points: {points}]");
        title = title.replace("{points}", String.valueOf(data.talentPoints));
        title = ChatColor.translateAlternateColorCodes('&', title);

        Inventory inv = Bukkit.createInventory(null, guiSize, title);

        for (TalentTree tree : trees.values()) {
            int currentLevel = data.talentLevels.getOrDefault(tree.id, 0);
            inv.setItem(tree.slot, createTalentItem(tree, currentLevel));
        }

        int resetSlot = plugin.getConfig().getInt("talent.reset-slot", 40);
        ItemStack resetItem = new ItemStack(Material.TNT);
        ItemMeta resetMeta = resetItem.getItemMeta();
        resetMeta.setDisplayName(ChatColor.YELLOW + "Reset Talents");
        List<String> resetLore = new ArrayList<>();
        resetLore.add(ChatColor.GRAY + "Refund " + plugin.getConfig().getInt("talent.reset-refund-percent", 80) + "% of points");
        resetMeta.setLore(resetLore);
        resetItem.setItemMeta(resetMeta);
        inv.setItem(resetSlot, resetItem);

        player.openInventory(inv);
    }

    private ItemStack createTalentItem(TalentTree tree, int currentLevel) {
        ItemStack item = new ItemStack(tree.icon != null ? tree.icon : Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(tree.displayName);
        List<String> lore = new ArrayList<>();
        for (String line : tree.lore) {
            line = line.replace("{level}", String.valueOf(currentLevel));
            line = line.replace("{max}", String.valueOf(tree.maxLevel));
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void addTalentPoint(Player player, int amount) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.talentPoints += amount;
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("general.prefix", "&d[IndieRPG] &r") + "&7+" + amount + " talent points!"));
    }

    public boolean matchesLore(List<String> itemLores) {
        for (String lore : itemLores) {
            String stripped = ChatColor.stripColor(lore).trim().toLowerCase();
            if (stripped.contains("talent point") || stripped.contains("天赋点")) {
                return true;
            }
        }
        return false;
    }

    public static class TalentTree {
        public String id;
        public String displayName;
        public Material icon;
        public int maxLevel;
        public int pointsPerLevel;
        public int slot;
        public List<String> lore;
    }
}
