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

import java.util.ArrayList;
import java.util.List;

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
        if (meta == null) return;

        String displayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        // Space Ring (check name + lore)
        if (plugin.getSpaceRingManager().matchesLore(lores) || matchesAny(displayName,
                plugin.getConfig().getStringList("spacering.trigger-items"))) {
            event.setCancelled(true);
            plugin.getSpaceRingManager().openSpaceRing(player, displayName);
            return;
        }

        // Soul Storage
        if (plugin.getSoulStorageManager().matchesLore(lores) || matchesAny(displayName,
                plugin.getConfig().getStringList("soulstorage.trigger-items"))) {
            event.setCancelled(true);
            plugin.getSoulStorageManager().openSoulStorage(player, 0);
            return;
        }

        // Crates
        String crateTier = plugin.getCrateManager().detectTier(displayName);
        if (crateTier == null) {
            crateTier = plugin.getCrateManager().detectTierFromLore(lores);
        }
        if (crateTier != null) {
            event.setCancelled(true);
            plugin.getCrateManager().openCrate(player, crateTier);
            item.setAmount(item.getAmount() - 1);
            return;
        }

        // Talent Point
        if (plugin.getTalentManager().matchesLore(lores) || displayName.toLowerCase().contains("talent point")) {
            event.setCancelled(true);
            plugin.getTalentManager().addTalentPoint(player, 1);
            item.setAmount(item.getAmount() - 1);
            return;
        }

        // BattlePass Token
        if (displayName.toLowerCase().contains("battlepass") ||
                containsLore(lores, "battlepass") || containsLore(lores, "战令")) {
            event.setCancelled(true);
            int xp = 10;
            plugin.getBattlePassManager().addXP(player, xp);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "+" + xp + " BattlePass XP!");
            item.setAmount(item.getAmount() - 1);
            return;
        }

        // Rank / Title
        String rankId = plugin.getRankManager().detectRankFromName(displayName);
        if (rankId == null) {
            rankId = plugin.getRankManager().detectRankFromLore(lores);
        }
        if (rankId != null) {
            event.setCancelled(true);
            plugin.getRankManager().setRank(player, rankId);
            item.setAmount(item.getAmount() - 1);
            return;
        }
    }

    private boolean matchesAny(String displayName, List<String> triggers) {
        String strippedName = ChatColor.stripColor(displayName).toLowerCase();
        for (String trigger : triggers) {
            String strippedTrigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', trigger)).toLowerCase();
            if (strippedName.contains(strippedTrigger)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLore(List<String> lores, String keyword) {
        for (String lore : lores) {
            if (ChatColor.stripColor(lore).toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
