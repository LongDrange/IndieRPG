package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 副本排行榜：記錄每個副本最快的前 10 名。 */
public class DungeonLeaderboardManager {
    private final IndieRPG plugin;
    private final File file;
    private final Map<String, List<Record>> records = new HashMap<String, List<Record>>();

    public DungeonLeaderboardManager(IndieRPG plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "dungeon-leaderboard.yml");
        load();
    }

    public void load() {
        records.clear();
        if (!file.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = config.getConfigurationSection("records");
        if (root == null) return;
        for (String dungeonId : root.getKeys(false)) {
            List<Record> list = new ArrayList<Record>();
            for (Map<?, ?> raw : root.getMapList(dungeonId)) {
                Record r = new Record();
                r.player = String.valueOf(raw.get("player"));
                Object ms = raw.get("millis");
                r.millis = ms instanceof Number ? ((Number) ms).longValue() : 0L;
                Object size = raw.get("party-size");
                r.partySize = size instanceof Number ? ((Number) size).intValue() : 1;
                list.add(r);
            }
            records.put(dungeonId, list);
        }
    }

    public void save() {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, List<Record>> e : records.entrySet()) {
            List<Map<String, Object>> raw = new ArrayList<Map<String, Object>>();
            for (Record r : e.getValue()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("player", r.player);
                m.put("millis", r.millis);
                m.put("party-size", r.partySize);
                raw.add(m);
            }
            config.set("records." + e.getKey(), raw);
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("儲存副本排行榜失敗：" + ex.getMessage());
        }
    }

    public void record(String dungeonId, String leader, long millis, int partySize) {
        List<Record> list = records.get(dungeonId);
        if (list == null) {
            list = new ArrayList<Record>();
            records.put(dungeonId, list);
        }
        Record r = new Record();
        r.player = leader == null ? "Unknown" : leader;
        r.millis = millis;
        r.partySize = partySize;
        list.add(r);
        Collections.sort(list, new Comparator<Record>() {
            @Override public int compare(Record a, Record b) {
                return Long.compare(a.millis, b.millis);
            }
        });
        while (list.size() > 10) list.remove(list.size() - 1);
        save();
    }

    public List<Record> getTop(String dungeonId, int count) {
        List<Record> list = records.get(dungeonId);
        if (list == null) return Collections.emptyList();
        return list.subList(0, Math.min(count, list.size()));
    }

    public static class Record {
        public String player;
        public long millis;
        public int partySize;
    }
}