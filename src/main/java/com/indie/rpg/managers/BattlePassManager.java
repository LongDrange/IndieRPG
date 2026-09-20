package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BattlePassManager {

    private final IndieRPG plugin;

    public BattlePassManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void addXP(Player player, int amount) {
        if (!plugin.getConfig().getBoolean("battlepass.enabled", true)) return;

        int xpPerLevel = plugin.getConfig().getInt("battlepass.xp-per-level", 100);
        int maxLevel = plugin.getConfig().getInt("battlepass.max-level", 50);
        int milestoneEvery = plugin.getConfig().getInt("battlepass.milestone-every", 10);
        int milestonePoints = plugin.getConfig().getInt("battlepass.milestone-rewards.talent-points", 5);
        int milestoneGold = plugin.getConfig().getInt("battlepass.milestone-rewards.gold", 500);
        int goldPerLevel = plugin.getConfig().getInt("battlepass.gold-per-level", 10);

        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.battlePassXP += amount;

        while (data.battlePassXP >= xpPerLevel && data.battlePassLevel < maxLevel) {
            data.battlePassXP -= xpPerLevel;
            data.battlePassLevel++;

            player.sendMessage(ChatColor.LIGHT_PURPLE + "[BattlePass] " + ChatColor.RESET +
                    "Level up! Level " + data.battlePassLevel);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

            data.gold += goldPerLevel * data.battlePassLevel;

            if (data.battlePassLevel % milestoneEvery == 0) {
                data.gold += milestoneGold;
                plugin.getTalentManager().addTalentPoint(player, milestonePoints);
                player.sendMessage(ChatColor.GOLD + "[BattlePass] Milestone! +" + milestoneGold + " Gold, +" + milestonePoints + " Talent Points");
            }
        }
    }

    public void showProgress(Player player) {
        int xpPerLevel = plugin.getConfig().getInt("battlepass.xp-per-level", 100);
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[BattlePass] " + ChatColor.RESET +
                "Level: " + data.battlePassLevel + " | XP: " + data.battlePassXP + "/" + xpPerLevel);
    }
}
