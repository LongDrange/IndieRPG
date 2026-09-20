package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BattlePassManager {

    private final IndieRPG plugin;
    private static final int XP_PER_LEVEL = 100;
    private static final int MAX_LEVEL = 50;

    public BattlePassManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void addXP(Player player, int amount) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.battlePassXP += amount;

        while (data.battlePassXP >= XP_PER_LEVEL && data.battlePassLevel < MAX_LEVEL) {
            data.battlePassXP -= XP_PER_LEVEL;
            data.battlePassLevel++;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[BattlePass] " + ChatColor.RESET + "Level up! Level " + data.battlePassLevel);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            rewardLevel(player, data.battlePassLevel);
        }
    }

    private void rewardLevel(Player player, int level) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (level % 10 == 0) {
            data.talentPoints += 5;
            player.sendMessage(ChatColor.GOLD + "[BattlePass] " + ChatColor.RESET + "Milestone reward: +5 Talent Points!");
        }
        data.gold += level * 10;
    }

    public void showProgress(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[BattlePass] " + ChatColor.RESET +
                "Level: " + data.battlePassLevel + " | XP: " + data.battlePassXP + "/" + XP_PER_LEVEL);
    }
}
