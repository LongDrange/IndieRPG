package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 中文：提供自定義屬性與飾品每級成長幅度。 */
public class JewelryGrowthManager {
    private final IndieRPG plugin;
    private final Map<String, Map<String, Double>> increments = new LinkedHashMap<>();

    public JewelryGrowthManager(IndieRPG plugin) { this.plugin = plugin; reload(); }

    public void reload() {
        increments.clear();
        FileConfiguration config = plugin.getConfigManager().get("jewelry");
        ConfigurationSection definitions = config.getConfigurationSection("jewelry.definitions");
        if (definitions == null) return;
        for (String id : definitions.getKeys(false)) {
            ConfigurationSection levels = definitions.getConfigurationSection(id + ".growth.per-level");
            if (levels == null) continue;
            for (String level : levels.getKeys(false)) {
                ConfigurationSection values = levels.getConfigurationSection(level);
                if (values == null) continue;
                Map<String, Double> attributes = new LinkedHashMap<>();
                for (String key : values.getKeys(false)) attributes.put(key, values.getDouble(key));
                increments.put(id.toLowerCase() + "." + level, attributes);
            }
        }
    }

    public Map<String, Double> getIncrement(String id, long level) {
        Map<String, Double> result = increments.get(id.toLowerCase() + "." + level);
        if (result != null) return Collections.unmodifiableMap(result);
        return Collections.emptyMap();
    }
}
