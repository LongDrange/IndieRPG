package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 中文：自定義物品讀取獨立 items.yml，並保留 Lore 識別標記。 */
public class ItemManager {
    private final IndieRPG plugin;
    private final Map<String, CustomItem> items = new HashMap<>();

    public ItemManager(IndieRPG plugin) { this.plugin = plugin; loadItems(); }

    public void loadItems() {
        items.clear();
        ConfigurationSection section = plugin.getConfigManager().get("items").getConfigurationSection("items");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            String path = key;
            CustomItem item = new CustomItem();
            item.id = section.getString(path + ".id", key).toLowerCase(Locale.ENGLISH);
            item.material = Material.matchMaterial(section.getString(path + ".material", "PAPER"));
            item.displayMaterial = Material.matchMaterial(section.getString(path + ".display-material", section.getString(path + ".material", "PAPER")));
            item.displayName = ChatColor.translateAlternateColorCodes('&', section.getString(path + ".display-name", key));
            item.namespace = section.getString(path + ".namespace", "indierpg:item");
            item.lore = new ArrayList<>();
            for (String line : section.getStringList(path + ".lore")) item.lore.add(ChatColor.translateAlternateColorCodes('&', line));
            items.put(item.id, item);
        }
        plugin.getLogger().info("已載入 " + items.size() + " 個自定義物品");
    }

    public ItemStack getItem(String id) {
        CustomItem custom = items.get(id.toLowerCase(Locale.ENGLISH));
        if (custom == null) return null;
        ItemStack item = new ItemStack(custom.material == null ? Material.PAPER : custom.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(custom.displayName);
            if (!custom.lore.isEmpty()) meta.setLore(custom.lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public List<String> getItemIds() { return new ArrayList<>(items.keySet()); }

    public static class CustomItem {
        public String id;
        public String namespace;
        public Material material;
        public Material displayMaterial;
        public String displayName;
        public List<String> lore;
    }
}
