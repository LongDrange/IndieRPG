package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SpaceRingManager {

    private final IndieRPG plugin;

    public SpaceRingManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openSpaceRing(Player player, int size) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int ringSize = Math.min(size, 54);
        Inventory inv = Bukkit.createInventory(null, ringSize, "§d§lSpace Ring §7[" + ringSize + " slots]");
        player.openInventory(inv);
        player.sendMessage("§d[SpaceRing] §7Opening space ring...");
    }

    public void openSoulStorage(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5§lSoul Storage §7[Infinite]");
        player.openInventory(inv);
        player.sendMessage("§5[SoulRingX] §7Opening soul storage...");
    }

    public void openMaterialStorage(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§e§lMaterial Storage");
        player.openInventory(inv);
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lSpace Shop");
        player.openInventory(inv);
    }
}
