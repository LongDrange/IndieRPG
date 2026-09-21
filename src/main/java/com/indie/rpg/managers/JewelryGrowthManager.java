package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 讀取每個飾品的逐級成長數值；-1 等級上限代表無限。 */
public class JewelryGrowthManager {
    private final IndieRPG plugin;
    private final Map<String, Map<Long, Map<String, Double>>> levels = new HashMap<>();
    public JewelryGrowthManager(IndieRPG plugin) { this.plugin = plugin; reload(); }
    public void reload() {
        levels.clear();
        FileConfiguration c = plugin.getConfigManager().get("jewelry");
        ConfigurationSection root = c.getConfigurationSection("jewelry.definitions");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id + ".growth.per-level");
            if (section == null) continue;
            Map<Long, Map<String, Double>> values = new HashMap<>();
            for (String level : section.getKeys(false)) {
                ConfigurationSection attributes = section.getConfigurationSection(level);
                if (attributes == null) continue;
                Map<String, Double> increments = new HashMap<>();
                for (String key : attributes.getKeys(false)) increments.put(key, attributes.getDouble(key));
                try { values.put(Long.parseLong(level), increments); } catch (NumberFormatException ignored) { }
            }
            levels.put(id.toLowerCase(), values);
        }
    }
    public Map<String, Double> getIncrement(String id, long level) {
        Map<Long, Map<String, Double>> values = levels.get(id.toLowerCase());
        if (values == null) return Collections.emptyMap();
        Map<String, Double> exact = values.get(level);
        if (exact != null) return Collections.unmodifiableMap(exact);
        return Collections.emptyMap();
    }
}
