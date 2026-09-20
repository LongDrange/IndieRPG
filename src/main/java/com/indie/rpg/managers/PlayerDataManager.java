package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final IndieRPG plugin;
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();
    private final File dataFolder;

    public PlayerDataManager(IndieRPG plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (!dataMap.containsKey(uuid)) {
            dataMap.put(uuid, loadData(uuid));
        }
        return dataMap.get(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    private PlayerData loadData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        PlayerData data = new PlayerData(uuid);
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            data.talentPoints = config.getInt("talent-points", 0);
            data.battlePassLevel = config.getInt("battlepass-level", 0);
            data.battlePassXP = config.getInt("battlepass-xp", 0);
            data.rank = config.getString("rank", "Rookie");
            data.taskKills = config.getInt("task-kills", 0);
            data.taskId = config.getString("task-id", "");
            data.spaceRingSize = config.getInt("spacering-size", 27);
            data.soulPearls = config.getInt("soul-pearls", 0);
            data.gold = config.getInt("gold", 0);
        }
        return data;
    }

    public void saveData(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        if (data == null) return;
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        config.set("talent-points", data.talentPoints);
        config.set("battlepass-level", data.battlePassLevel);
        config.set("battlepass-xp", data.battlePassXP);
        config.set("rank", data.rank);
        config.set("task-kills", data.taskKills);
        config.set("task-id", data.taskId);
        config.set("spacering-size", data.spaceRingSize);
        config.set("soul-pearls", data.soulPearls);
        config.set("gold", data.gold);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save data for " + uuid);
        }
    }

    public void saveAll() {
        for (UUID uuid : dataMap.keySet()) {
            saveData(uuid);
        }
    }

    public static class PlayerData {
        public UUID uuid;
        public int talentPoints;
        public int battlePassLevel;
        public int battlePassXP;
        public String rank;
        public int taskKills;
        public String taskId;
        public int spaceRingSize;
        public int soulPearls;
        public int gold;

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
        }
    }
}
