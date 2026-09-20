package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {

    private final IndieRPG plugin;

    public PlayerListener(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = ChatColor.stripColor(meta.getDisplayName());

        if (name.contains("Space Ring") || name.contains("空间戒指")) {
            event.setCancelled(true);
            int size = 27;
            if (name.contains("Basic")) size = 9;
            else if (name.contains("Advanced")) size = 27;
            else if (name.contains("Supreme")) size = 54;
            plugin.getSpaceRingManager().openSpaceRing(player, size);
        }

        if (name.contains("Soul Storage") || name.contains("灵魂空间")) {
            event.setCancelled(true);
            plugin.getSpaceRingManager().openSoulStorage(player);
        }

        if (name.contains("Crate") || name.contains("宝箱")) {
            event.setCancelled(true);
            String tier = "common";
            if (name.contains("Rare")) tier = "rare";
            else if (name.contains("Legendary")) tier = "legendary";
            plugin.getCrateManager().openCrate(player, tier);
            item.setAmount(item.getAmount() - 1);
        }

        if (name.contains("Talent Point") || name.contains("天赋点")) {
            event.setCancelled(true);
            plugin.getTalentManager().addTalentPoint(player, 1);
            item.setAmount(item.getAmount() - 1);
        }

        if (name.contains("BattlePass") || name.contains("战令")) {
            event.setCancelled(true);
            plugin.getBattlePassManager().addXP(player, 10);
            item.setAmount(item.getAmount() - 1);
        }

        if (name.contains("Title") || name.contains("称号")) {
            event.setCancelled(true);
            String rank = "Warrior";
            if (name.contains("Knight")) rank = "Knight";
            else if (name.contains("Legend")) rank = "Legend";
            plugin.getRankManager().setRank(player, rank);
            item.setAmount(item.getAmount() - 1);
        }
    }
}
