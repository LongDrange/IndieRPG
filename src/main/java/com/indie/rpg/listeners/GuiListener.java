package com.indie.rpg.listeners;

import com.indie.rpg.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener {

    private final com.indie.rpg.IndieRPG plugin;

    public GuiListener(com.indie.rpg.IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractGui)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;
        AbstractGui gui = (AbstractGui) event.getInventory().getHolder();
        gui.handleClick((Player) event.getWhoClicked(), event.getRawSlot());
    }
}