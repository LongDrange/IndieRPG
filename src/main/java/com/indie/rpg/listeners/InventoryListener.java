package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryListener implements Listener {

    private final IndieRPG plugin;

    public InventoryListener(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getInventory().getTitle();
        if (title.contains("Talent") || title.contains("Soul Storage")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            int killXp = plugin.getConfig().getInt("battlepass.kill-xp", 5);
            plugin.getTaskManager().onEntityKill(killer, event.getEntity());
            plugin.getBattlePassManager().addXP(killer, killXp);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().saveData(event.getPlayer().getUniqueId());
    }
}
