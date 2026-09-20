package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TaskManager {

    private final IndieRPG plugin;

    public TaskManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void onEntityKill(Player player, LivingEntity entity) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.taskActive) return;

        int required = plugin.getConfig().getInt("task.required-kills", 15);
        data.taskKills++;

        String progressMsg = plugin.getConfig().getString("task.progress-message", "&a[Task] &7Progress: {kills}/{count}");
        progressMsg = progressMsg.replace("{kills}", String.valueOf(data.taskKills))
                                 .replace("{count}", String.valueOf(required));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', progressMsg));

        if (data.taskKills >= required) {
            int gold = plugin.getConfig().getInt("task.rewards.gold", 100);
            int points = plugin.getConfig().getInt("task.rewards.talent-points", 5);
            int xp = plugin.getConfig().getInt("task.rewards.battlepass-xp", 50);

            data.gold += gold;
            data.taskKills = 0;
            data.taskActive = false;
            plugin.getTalentManager().addTalentPoint(player, points);
            plugin.getBattlePassManager().addXP(player, xp);

            String completeMsg = plugin.getConfig().getString("task.complete-message",
                    "&6[Task] &eComplete! +{gold} Gold, +{points} Talent Points");
            completeMsg = completeMsg.replace("{gold}", String.valueOf(gold))
                                     .replace("{points}", String.valueOf(points));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', completeMsg));
        }
    }

    public void startTask(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.taskActive) {
            player.sendMessage(ChatColor.RED + "You already have an active task!");
            return;
        }
        data.taskActive = true;
        data.taskKills = 0;

        int required = plugin.getConfig().getInt("task.required-kills", 15);
        String startMsg = plugin.getConfig().getString("task.start-message", "&a[Task] &7Slayer task: kill {count} monsters.");
        startMsg = startMsg.replace("{count}", String.valueOf(required));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', startMsg));
    }
}
