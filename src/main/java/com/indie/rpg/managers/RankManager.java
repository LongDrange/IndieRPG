package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RankManager {

    private final IndieRPG plugin;
    private final String[] ranks = {"Rookie", "Warrior", "Knight", "Elite", "Legend", "Mythic"};

    public RankManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void setRank(Player player, String rank) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.rank = rank;
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', getRankColor(rank) + "[" + rank + "] ") + player.getName());
        player.sendMessage(ChatColor.GOLD + "[Rank] " + ChatColor.RESET + "Rank updated to: " + getRankColor(rank) + rank);
    }

    public String getRankColor(String rank) {
        switch (rank) {
            case "Rookie": return "&7";
            case "Warrior": return "&c";
            case "Knight": return "&f";
            case "Elite": return "&b";
            case "Legend": return "&6";
            case "Mythic": return "&5";
            default: return "&7";
        }
    }

    public void showRank(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        player.sendMessage(ChatColor.GOLD + "[Rank] " + ChatColor.RESET + "Your rank: " + getRankColor(data.rank) + data.rank);
    }
}
