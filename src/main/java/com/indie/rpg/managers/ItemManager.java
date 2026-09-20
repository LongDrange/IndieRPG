package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {

    private final IndieRPG plugin;
    private final Map<String, CustomItem> items = new HashMap<>();

    public ItemManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadItems();
    }

    public void loadItems() {
        items.clear();
        if (plugin.getConfig().contains("items")) {
            for (String key : plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
                String path = "items." + key;
                CustomItem item = new CustomItem();
                item.id = key;
                item.material = Material.matchMaterial(plugin.getConfig().getString(path + ".material", "PAPER"));
                item.displayName = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString(path + ".display-name", key));
                item.lore = new ArrayList<>();
                for (String line : plugin.getConfig().getStringList(path + ".lore")) {
                    item.lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                items.put(key, item);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items");
    }

    public ItemStack getItem(String id) {
        CustomItem custom = items.get(id.toLowerCase());
        if (custom == null) return null;

        Material mat = custom.material != null ? custom.material : Material.PAPER;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(custom.displayName);
        if (!custom.lore.isEmpty()) {
            meta.setLore(custom.lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public List<String> getItemIds() {
        return new ArrayList<>(items.keySet());
    }

    public static class CustomItem {
        public String id;
        public Material material;
        public String displayName;
        public List<String> lore;
    }
}
