package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReputationManager {
    private final IndieRPG plugin;
    private final Map<String, Faction> factions = new HashMap<String, Faction>();

    public ReputationManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadFactions();
    }

    public void loadFactions() {
        factions.clear();
        ConfigurationSection root = plugin.getConfigManager().get("reputation")
                .getConfigurationSection("factions");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            Faction f = new Faction();
            f.id = id;
            f.name = sec.getString("name", id);
            ConfigurationSection levels = sec.getConfigurationSection("levels");
            if (levels != null) {
                for (String lvl : levels.getKeys(false)) {
                    f.levels.put(lvl, levels.getInt(lvl));
                }
            }
            factions.put(id, f);
        }
        plugin.getLogger().info("已載入 " + factions.size() + " 個聲望陣營");
    }

    public int getRep(Player player, String factionId) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Integer v = data.reputation.get(factionId);
        return v == null ? 0 : v;
    }

    public void addRep(Player player, String factionId, int amount) {
        Faction f = factions.get(factionId);
        if (f == null) return;
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int oldVal = getRep(player, factionId);
        int newVal = oldVal + amount;
        data.reputation.put(factionId, newVal);
        String oldStage = getStage(f, oldVal);
        String newStage = getStage(f, newVal);
        if (!oldStage.equals(newStage)) {
            player.sendMessage("§e與 " + f.name + " 的聲望階段：§f" + newStage);
        }
    }

    public String getStage(Faction f, int value) {
        String result = "neutral";
        int best = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> e : f.levels.entrySet()) {
            if (value >= e.getValue() && e.getValue() > best) {
                best = e.getValue();
                result = e.getKey();
            }
        }
        return result;
    }

    public Map<String, Faction> getAll() {
        return Collections.unmodifiableMap(factions);
    }

    public static class Faction {
        public String id;
        public String name;
        public final Map<String, Integer> levels = new LinkedHashMap<String, Integer>();
    }
}