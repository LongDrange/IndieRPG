package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定義屬性管理器：讀取 config/attributes.yml，支援基本屬性定義、上下限與 GUI 展示。
 */
public class AttributeManager {
    private final IndieRPG plugin;
    private final Map<String, AttributeEntry> attributes = new LinkedHashMap<>();

    public AttributeManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadAttributes();
    }

    public void loadAttributes() {
        attributes.clear();
        ConfigurationSection root = plugin.getConfigManager().get("attributes").getConfigurationSection("attributes");
        if (root == null) {
            plugin.getLogger().warning("未找到 attributes.yml 中的 attributes 節點");
            return;
        }
        for (String key : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(key);
            if (section == null) continue;
            AttributeEntry entry = new AttributeEntry();
            entry.id = key;
            entry.displayName = ChatColor.translateAlternateColorCodes('&', section.getString("display-name", key));
            entry.defaultValue = section.getDouble("default", 0D);
            entry.minValue = section.getDouble("min", 0D);
            entry.maxValue = section.getDouble("max", 1000000D);
            attributes.put(key.toLowerCase(), entry);
        }
        plugin.getLogger().info("已載入 " + attributes.size() + " 個屬性定義");
    }

    public double getDefaultValue(String attributeId) {
        AttributeEntry entry = attributes.get(attributeId.toLowerCase());
        return entry == null ? 0D : entry.defaultValue;
    }

    public double getMinValue(String attributeId) {
        AttributeEntry entry = attributes.get(attributeId.toLowerCase());
        return entry == null ? 0D : entry.minValue;
    }

    public double getMaxValue(String attributeId) {
        AttributeEntry entry = attributes.get(attributeId.toLowerCase());
        return entry == null ? 1000000D : entry.maxValue;
    }

    public Inventory openAttributeGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "LDAPI 屬性列表");
        int slot = 0;
        for (AttributeEntry entry : attributes.values()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(entry.displayName);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "預設值：" + entry.defaultValue);
                lore.add(ChatColor.GRAY + "範圍：" + entry.minValue + " ~ " + entry.maxValue);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(slot++, item);
            if (slot >= inventory.getSize()) break;
        }
        player.openInventory(inventory);
        return inventory;
    }

    public static class AttributeEntry {
        public String id;
        public String displayName;
        public double defaultValue;
        public double minValue;
        public double maxValue;
    }
}
