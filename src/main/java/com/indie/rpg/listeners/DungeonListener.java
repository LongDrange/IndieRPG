package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getDungeonManager().onPlayerDeath(event.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getDungeonManager().onPlayerQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ldapi.admin")) return;
        if (!plugin.getConfig().getBoolean("dungeon.commands.enabled", true)) return;
        if (!plugin.getDungeonManager().isInDungeon(player)) return;

        String message = event.getMessage();
        if (message == null || message.length() < 2) return;
        String cmd = message.substring(1);

        if (!plugin.getDungeonManager().isCommandAllowed(cmd)) {
            event.setCancelled(true);
            String msg = plugin.getConfig().getString("dungeon.commands.blocked-message",
                    "&c副本內無法使用該指令，請先 /dungeon leave 離開副本。");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}