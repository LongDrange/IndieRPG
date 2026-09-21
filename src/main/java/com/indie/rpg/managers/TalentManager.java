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

                org.bukkit.configuration.ConfigurationSection attrs =
                        plugin.getConfig().getConfigurationSection(path + ".attributes-per-level");
                if (attrs != null) {
                    for (String k : attrs.getKeys(false)) {
                        tree.attributesPerLevel.put(k.toLowerCase(), attrs.getDouble(k));
                    }
                }

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
    public Map<String, Double> calcAttributes(PlayerDataManager.PlayerData data) {
        Map<String, Double> result = new HashMap<String, Double>();
        for (TalentTree tree : trees.values()) {
            int level = data.talentLevels.containsKey(tree.id)
                    ? data.talentLevels.get(tree.id) : 0;
            if (level <= 0) continue;
            for (Map.Entry<String, Double> e : tree.attributesPerLevel.entrySet()) {
                String key = e.getKey().toLowerCase();
                Double old = result.get(key);
                result.put(key, old == null ? e.getValue() * level : old + e.getValue() * level);
            }
        }
        return result;
    }

    public boolean upgradeTalent(Player player, String treeId) {
        TalentTree tree = trees.get(treeId);
        if (tree == null) return false;
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int current = data.talentLevels.containsKey(treeId) ? data.talentLevels.get(treeId) : 0;
        if (current >= tree.maxLevel) {
            player.sendMessage(ChatColor.RED + "該天賦已滿級。");
            return false;
        }
        int cost = tree.pointsPerLevel;
        if (data.talentPoints < cost) {
            player.sendMessage(ChatColor.RED + "天賦點不足（需要 " + cost + " 點）。");
            return false;
        }
        data.talentPoints -= cost;
        data.talentLevels.put(treeId, current + 1);
        if (plugin.getAttributeEngine() != null) {
            plugin.getAttributeEngine().recalculate(player);
        }
        player.sendMessage(ChatColor.GREEN + "已升級天賦：" + tree.displayName
                + " (" + (current + 1) + "/" + tree.maxLevel + ")");
        return true;
    }

    public void handleTalentClick(Player player, int slot) {
        for (TalentTree tree : trees.values()) {
            if (tree.slot == slot) {
                upgradeTalent(player, tree.id);
                openTalentMenu(player);
                return;
            }
        }
    }

    public static class TalentTree {
        public String id;
        public String displayName;
        public Material icon;
        public int maxLevel;
        public int pointsPerLevel;
        public int slot;
        public List<String> lore;
        public final Map<String, Double> attributesPerLevel = new HashMap<String, Double>();
    }
}
