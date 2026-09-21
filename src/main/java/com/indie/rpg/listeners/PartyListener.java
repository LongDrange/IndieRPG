package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {
    private final IndieRPG plugin;

    public PartyListener(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPartyManager().leave(event.getPlayer());
    }
}