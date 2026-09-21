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

/**
 * 玩家資料保存：金幣、稱號、天賦、在線時間、飾品成長、每日獎勵紀錄等。
 */
public class PlayerDataManager {
    private final IndieRPG plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private final File folder;

    public PlayerDataManager(IndieRPG plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "playerdata");
        if (!folder.exists()) folder.mkdirs();
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            cache.put(uuid, load(uuid));
        }
        return cache.get(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    private PlayerData load(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        File file = new File(folder, uuid + ".yml");
        if (!file.exists()) return data;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        data.talentPoints = config.getInt("talent-points", 0);
        data.battlePassLevel = config.getInt("battlepass-level", 0);
        data.battlePassXP = config.getInt("battlepass-xp", 0);
        data.rank = config.getString("rank", "default");
        data.taskKills = config.getInt("task-kills", 0);
        data.taskActive = config.getBoolean("task-active", false);
        data.gold = config.getInt("gold", plugin.getConfig().getInt("economy.starting-gold", 0));
        data.totalOnlineMinutes = config.getLong("online.total-minutes", 0L);
        data.lastDailyClaim = config.getLong("daily.last-claim", 0L);
        data.job = config.getString("job", "");
        data.jobLevel = config.getInt("job-level", 1);
        data.jobExp = config.getLong("job-exp", 0L);
        
        if (config.isConfigurationSection("reputation")) {
            for (String k : config.getConfigurationSection("reputation").getKeys(false)) {
                data.reputation.put(k, config.getInt("reputation." + k));
            }
        }

        if (config.isConfigurationSection("talents")) {
            for (String key : config.getConfigurationSection("talents").getKeys(false)) {
                data.talentLevels.put(key, config.getInt("talents." + key, 0));
            }
        }

        if (config.isConfigurationSection("jewelry")) {
            for (String key : config.getConfigurationSection("jewelry").getKeys(false)) {
                data.jewelryLevels.put(key, config.getLong("jewelry." + key + ".level", 0L));
                data.jewelryOnlineMinutes.put(key, config.getLong("jewelry." + key + ".online-minutes", 0L));
                data.jewelryRealSeconds.put(key, config.getLong("jewelry." + key + ".real-seconds", 0L));
                data.jewelryGrowthPoints.put(key, config.getDouble("jewelry." + key + ".growth-points", 0D));
            }
        }

        return data;
    }

    public void saveData(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;

        FileConfiguration config = new YamlConfiguration();
        config.set("talent-points", data.talentPoints);
        config.set("battlepass-level", data.battlePassLevel);
        config.set("battlepass-xp", data.battlePassXP);
        config.set("rank", data.rank);
        config.set("task-kills", data.taskKills);
        config.set("task-active", data.taskActive);
        config.set("gold", data.gold);
        config.set("online.total-minutes", data.totalOnlineMinutes);
        config.set("daily.last-claim", data.lastDailyClaim);
        config.set("job", data.job);
        config.set("job-level", data.jobLevel);
        config.set("job-exp", data.jobExp);
        
        for (Map.Entry<String, Integer> e : data.reputation.entrySet()) {
            config.set("reputation." + e.getKey(), e.getValue());
        }

        for (Map.Entry<String, Integer> e : data.talentLevels.entrySet()) {
            config.set("talents." + e.getKey(), e.getValue());
        }

        for (Map.Entry<String, Long> e : data.jewelryLevels.entrySet()) {
            String key = e.getKey();
            config.set("jewelry." + key + ".level", e.getValue());
            config.set("jewelry." + key + ".online-minutes", data.jewelryOnlineMinutes.getOrDefault(key, 0L));
            config.set("jewelry." + key + ".real-seconds", data.jewelryRealSeconds.getOrDefault(key, 0L));
            config.set("jewelry." + key + ".growth-points", data.jewelryGrowthPoints.getOrDefault(key, 0D));
        }

        try {
            config.save(new File(folder, uuid + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().warning("無法保存玩家資料：" + uuid + " 原因：" + e.getMessage());
        }
    }

    public void saveAll() {
        for (UUID uuid : cache.keySet()) {
            saveData(uuid);
        }
    }

    public void addOnlineMinute(UUID uuid) {
        PlayerData data = getPlayerData(uuid);
        data.totalOnlineMinutes += 1L;
    }

    public static class PlayerData {
        public final UUID uuid;
        public int talentPoints;
        public int battlePassLevel;
        public int battlePassXP;
        public int taskKills;
        public int gold;
        public String rank;
        public boolean taskActive;
        public long totalOnlineMinutes;
        public long lastDailyClaim;
        public Map<String, Integer> talentLevels = new HashMap<>();
        public Map<String, Long> jewelryLevels = new HashMap<>();
        public Map<String, Long> jewelryOnlineMinutes = new HashMap<>();
        public Map<String, Long> jewelryRealSeconds = new HashMap<>();
        public Map<String, Double> jewelryGrowthPoints = new HashMap<>();
        
        // ---- 擴充：職業 ----
        public String job = "";
        public int jobLevel = 1;
        public long jobExp = 0L;

        // ---- 執行時快取（不持久化）----
        public transient Map<String, Double> computedAttributes = new HashMap<>();
        
        // ---- 擴充：聲望 ----
        public Map<String, Integer> reputation = new HashMap<>();

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
            this.rank = "default";
        }
    }
}
