package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DungeonListener implements Listener {
    private final IndieRPG plugin;

    public DungeonListener(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        plugin.getDungeonManager().onMobKill(killer, event.getEntity().getType().name());
    }
}