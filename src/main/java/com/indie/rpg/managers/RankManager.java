package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankManager {

    private final IndieRPG plugin;
    private final Map<String, RankData> ranks = new HashMap<>();

    public RankManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadRanks();
    }

    public void loadRanks() {
        ranks.clear();
        if (plugin.getConfig().contains("rank.ranks")) {
            for (String key : plugin.getConfig().getConfigurationSection("rank.ranks").getKeys(false)) {
                String path = "rank.ranks." + key;
                RankData rank = new RankData();
                rank.id = key;
                rank.display = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString(path + ".display", key));
                rank.chatPrefix = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString(path + ".chat-prefix", ""));
                ranks.put(key, rank);
            }
        }
        plugin.getLogger().info("Loaded " + ranks.size() + " ranks");
    }

    public String detectRankFromName(String itemName) {
        String strippedName = ChatColor.stripColor(itemName).toLowerCase();
        if (plugin.getConfig().contains("rank.items")) {
            for (String matchItem : plugin.getConfig().getConfigurationSection("rank.items").getKeys(false)) {
                String rankId = plugin.getConfig().getString("rank.items." + matchItem);
                String strippedMatch = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', matchItem)).toLowerCase();
                if (strippedName.contains(strippedMatch)) {
                    return rankId;
                }
            }
        }
        return null;
    }

    public String detectRankFromLore(List<String> lores) {
        for (String lore : lores) {
            String rankId = detectRankFromName(lore);
            if (rankId != null) return rankId;
        }
        return null;
    }

    public void setRank(Player player, String rankId) {
        RankData rank = ranks.get(rankId);
        if (rank == null) {
            player.sendMessage(ChatColor.RED + "Unknown rank: " + rankId);
            return;
        }
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.rank = rankId;
        player.sendMessage(ChatColor.GOLD + "[Rank] " + ChatColor.RESET + "Rank: " + rank.display);
    }

    public String getRankDisplay(String rankId) {
        RankData rank = ranks.get(rankId);
        return rank != null ? rank.display : rankId;
    }

    public static class RankData {
        public String id;
        public String display;
        public String chatPrefix;
    }
}
