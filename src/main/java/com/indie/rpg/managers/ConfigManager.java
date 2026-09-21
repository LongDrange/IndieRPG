package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 集中管理 plugins/LDAPI/config 下的 YAML。 */
public class ConfigManager {
    private final IndieRPG plugin;
    private final File directory;
    private final Map<String, FileConfiguration> files = new LinkedHashMap<>();

    public ConfigManager(IndieRPG plugin) {
        this.plugin = plugin;
        this.directory = new File(plugin.getDataFolder(), "config");
        if (!directory.exists()) directory.mkdirs();
        reloadAll();
    }

    public void reloadAll() {
        files.clear();
        String[] names = {"general", "items", "attributes", "jewelry", "spaces", "commands", "tests", "shop", "dailyreward", "mythicmobs", "jobs", "parties", "guilds", "dungeons", "pets", "reputation", "npc-dialogues"};
        for (String name : names) {
            File file = new File(directory, name + ".yml");
            if (!file.exists()) {
                try { plugin.saveResource("config/" + name + ".yml", false); }
                catch (IllegalArgumentException ignored) { plugin.getLogger().warning("找不到內置配置：config/" + name + ".yml"); }
            }
            if (file.exists()) files.put(name, YamlConfiguration.loadConfiguration(file));
        }
    }

    public FileConfiguration get(String name) {
        FileConfiguration config = files.get(name);
        return config == null ? new YamlConfiguration() : config;
    }

    public Map<String, FileConfiguration> getAll() { return Collections.unmodifiableMap(files); }

    public void save(String name) throws IOException {
        FileConfiguration config = files.get(name);
        if (config != null) config.save(new File(directory, name + ".yml"));
    }
}
