package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 職業系統：從 config/jobs.yml 載入職業定義，
 * 負責升級、技能解鎖、屬性加成計算。
 */
public class JobManager {
    private final IndieRPG plugin;
    private final Map<String, JobDefinition> jobs = new LinkedHashMap<String, JobDefinition>();

    public JobManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadJobs();
    }

    public void loadJobs() {
        jobs.clear();
        ConfigurationSection root = plugin.getConfigManager().get("jobs")
                .getConfigurationSection("jobs");
        if (root == null) {
            plugin.getLogger().warning("找不到 config/jobs.yml 的 jobs 節點");
            return;
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;

            JobDefinition def = new JobDefinition();
            def.id = id.toLowerCase();
            def.displayName = ChatColor.translateAlternateColorCodes('&',
                    sec.getString("display-name", id));
            def.maxLevel = sec.getInt("max-level", 50);
            def.expBase = sec.getDouble("exp-formula.base", 100D);
            def.expPower = sec.getDouble("exp-formula.power", 2.0D);

            ConfigurationSection attrs = sec.getConfigurationSection("attributes-per-level");
            if (attrs != null) {
                for (String key : attrs.getKeys(false)) {
                    def.attributesPerLevel.put(key.toLowerCase(), attrs.getDouble(key));
                }
            }

            ConfigurationSection skills = sec.getConfigurationSection("skills");
            if (skills != null) {
                for (String lvlKey : skills.getKeys(false)) {
                    try {
                        int lvl = Integer.parseInt(lvlKey);
                        def.skills.put(lvl, skills.getString(lvlKey));
                    } catch (NumberFormatException ignored) { }
                }
            }

            jobs.put(def.id, def);
        }
        plugin.getLogger().info("已載入 " + jobs.size() + " 個職業");
    }

    public JobDefinition get(String id) {
        return id == null ? null : jobs.get(id.toLowerCase());
    }

    public Map<String, JobDefinition> getAll() {
        return Collections.unmodifiableMap(jobs);
    }

    public boolean setJob(Player player, String id) {
        JobDefinition def = get(id);
        if (def == null) {
            player.sendMessage(ChatColor.RED + "未知職業：" + id);
            return false;
        }
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        boolean allowChange = plugin.getConfigManager().get("jobs")
                .getBoolean("allow-change", true);
        if (data.job != null && !data.job.isEmpty() && !allowChange) {
            player.sendMessage(ChatColor.RED + "你已經選擇了職業，無法更改。");
            return false;
        }
        data.job = def.id;
        if (data.jobLevel < 1) data.jobLevel = 1;
        plugin.getAttributeEngine().recalculate(player);
        player.sendMessage(ChatColor.GREEN + "職業已設定為 " + def.displayName);
        return true;
    }

    public void addExp(Player player, long amount) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        JobDefinition def = get(data.job);
        if (def == null) return;
        if (data.jobLevel >= def.maxLevel) return;

        data.jobExp += amount;
        boolean leveled = false;
        while (data.jobLevel < def.maxLevel && data.jobExp >= expNeeded(def, data.jobLevel)) {
            data.jobExp -= expNeeded(def, data.jobLevel);
            data.jobLevel++;
            leveled = true;
            onLevelUp(player, def, data.jobLevel);
        }
        if (leveled) plugin.getAttributeEngine().recalculate(player);
    }

    private void onLevelUp(Player player, JobDefinition def, int newLevel) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&e[職業] &7等級提升至 &f" + newLevel));
        String skill = def.skills.get(newLevel);
        if (skill != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&b[技能] &7解鎖：&f" + skill));
        }
    }

    public long expNeeded(JobDefinition def, int level) {
        return (long) Math.floor(def.expBase * Math.pow(level, def.expPower));
    }

    public Map<String, Double> calcAttributes(PlayerDataManager.PlayerData data) {
        Map<String, Double> result = new HashMap<String, Double>();
        JobDefinition def = get(data.job);
        if (def == null) return result;
        for (Map.Entry<String, Double> e : def.attributesPerLevel.entrySet()) {
            result.put(e.getKey(), e.getValue() * data.jobLevel);
        }
        return result;
    }

    public static class JobDefinition {
        public String id;
        public String displayName;
        public int maxLevel = 50;
        public double expBase = 100D;
        public double expPower = 2.0D;
        public final Map<String, Double> attributesPerLevel = new LinkedHashMap<String, Double>();
        public final Map<Integer, String> skills = new LinkedHashMap<Integer, String>();
    }
}