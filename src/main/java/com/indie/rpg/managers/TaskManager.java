package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

public class TaskManager {

    private final IndieRPG plugin;

    public TaskManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void onEntityKill(Player player, LivingEntity entity) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.taskId == null || data.taskId.isEmpty()) return;

        data.taskKills++;
        player.sendMessage("§a[Task] §7Progress: " + data.taskKills + " kills");

        if (data.taskKills >= 10) {
            player.sendMessage("§6[Task] §eTask completed! +100 Gold, +5 Talent Points");
            data.gold += 100;
            data.talentPoints += 5;
            data.taskKills = 0;
            data.taskId = "";
        }
    }

    public void startTask(Player player, String taskId) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.taskId = taskId;
        data.taskKills = 0;
        player.sendMessage("§a[Task] §7Started task: " + taskId);
    }
}
