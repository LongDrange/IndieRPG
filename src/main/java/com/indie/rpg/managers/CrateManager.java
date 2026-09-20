package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

public class CrateManager {

    private final IndieRPG plugin;
    private final Random random = new Random();

    public CrateManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void openCrate(Player player, String tier) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        double roll = random.nextDouble();

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);

        switch (tier.toLowerCase()) {
            case "common":
                if (roll < 0.6) {
                    player.sendMessage(ChatColor.GRAY + "[Crate] " + ChatColor.RESET + "Common reward: 10 Gold");
                    data.gold += 10;
                } else if (roll < 0.9) {
                    player.sendMessage(ChatColor.YELLOW + "[Crate] " + ChatColor.RESET + "Rare reward: 50 Gold");
                    data.gold += 50;
                } else {
                    player.sendMessage(ChatColor.GOLD + "[Crate] " + ChatColor.RESET + "Epic reward: 100 Gold + 1 Talent Point");
                    data.gold += 100;
                    data.talentPoints += 1;
                }
                break;
            case "rare":
                if (roll < 0.5) {
                    player.sendMessage(ChatColor.AQUA + "[Crate] " + ChatColor.RESET + "Rare reward: 100 Gold");
                    data.gold += 100;
                } else if (roll < 0.85) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "[Crate] " + ChatColor.RESET + "Epic reward: 250 Gold");
                    data.gold += 250;
                } else {
                    player.sendMessage(ChatColor.GOLD + "[Crate] " + ChatColor.RESET + "Legendary reward: 500 Gold + 3 Talent Points");
                    data.gold += 500;
                    data.talentPoints += 3;
                }
                break;
            case "legendary":
                if (roll < 0.4) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "[Crate] " + ChatColor.RESET + "Epic reward: 500 Gold");
                    data.gold += 500;
                } else if (roll < 0.8) {
                    player.sendMessage(ChatColor.GOLD + "[Crate] " + ChatColor.RESET + "Legendary reward: 1000 Gold + 5 Talent Points");
                    data.gold += 1000;
                    data.talentPoints += 5;
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "[Crate] " + ChatColor.RESET + "Mythic reward: 2500 Gold + 10 Talent Points");
                    data.gold += 2500;
                    data.talentPoints += 10;
                }
                break;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
    }
}
