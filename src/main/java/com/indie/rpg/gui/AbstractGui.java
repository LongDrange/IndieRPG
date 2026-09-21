package com.indie.rpg.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractGui implements InventoryHolder {
    protected final Inventory inventory;
    protected final Map<Integer, Consumer<Player>> actions = new HashMap<Integer, Consumer<Player>>();

    public AbstractGui(String title, int rows) {
        int size = Math.max(9, Math.min(54, rows * 9));
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public abstract void setup(Player player);

    protected void set(int slot, ItemStack item, Consumer<Player> action) {
        inventory.setItem(slot, item);
        if (action != null) actions.put(slot, action);
    }

    public void open(Player player) {
        setup(player);
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        Consumer<Player> c = actions.get(slot);
        if (c != null) c.accept(player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}