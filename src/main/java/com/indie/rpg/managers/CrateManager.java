package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CrateManager {

    private final IndieRPG plugin;
    private final Random random = new Random();
    private final List<CrateTier> tiers = new ArrayList<>();

    public CrateManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadRewards();
    }

    public void loadRewards() {
        tiers.clear();
        if (!plugin.getConfig().contains("crate.rewards")) return;

        for (String tierName : plugin.getConfig().getConfigurationSection("crate.rewards").getKeys(false)) {
            String path = "crate.rewards." + tierName;
            CrateTier tier = new CrateTier();
            tier.name = tierName;

            List<Map<?, ?>> rewardMaps = plugin.getConfig().getMapList(path);
            for (Map<?, ?> map : rewardMaps) {
                CrateReward reward = new CrateReward();
                reward.chance = ((Number) map.get("chance")).floatValue();
                reward.gold = ((Number) map.get("gold")).intValue();
                reward.talentPoints = ((Number) map.get("talent-points")).intValue();
                reward.battlepassXp = ((Number) map.get("battlepass-xp")).intValue();
                tier.rewards.add(reward);
            }
            tiers.add(tier);
        }
        plugin.getLogger().info("Loaded " + tiers.size() + " crate tiers");
    }

    public String detectTier(String itemName) {
        String strippedName = ChatColor.stripColor(itemName).toLowerCase();
        if (plugin.getConfig().contains("crate.trigger-items")) {
            for (String tier : plugin.getConfig().getConfigurationSection("crate.trigger-items").getKeys(false)) {
                List<String> names = plugin.getConfig().getStringList("crate.trigger-items." + tier);
                for (String name : names) {
                    String strippedTrigger = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)).toLowerCase();
                    if (strippedName.contains(strippedTrigger)) {
                        return tier;
                    }
                }
            }
        }
        return null;
    }

    public String detectTierFromLore(List<String> lores) {
        for (String lore : lores) {
            String tier = detectTier(lore);
            if (tier != null) return tier;
        }
        return null;
    }

    public void openCrate(Player player, String tierName) {
        CrateTier tier = null;
        for (CrateTier t : tiers) {
            if (t.name.equalsIgnoreCase(tierName)) {
                tier = t;
                break;
            }
        }
        if (tier == null) {
            player.sendMessage(ChatColor.RED + "Unknown crate tier: " + tierName);
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);

        float roll = random.nextFloat();
        float cumulative = 0;
        CrateReward won = tier.rewards.get(tier.rewards.size() - 1);

        for (CrateReward reward : tier.rewards) {
            cumulative += reward.chance;
            if (roll <= cumulative) {
                won = reward;
                break;
            }
        }

        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.gold += won.gold;
        if (won.talentPoints > 0) plugin.getTalentManager().addTalentPoint(player, won.talentPoints);
        if (won.battlepassXp > 0) plugin.getBattlePassManager().addXP(player, won.battlepassXp);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("general.prefix", "&d[IndieRPG] &r") +
                "&7Opening &6" + tier.name + " &7crate..."));
        player.sendMessage(ChatColor.GOLD + "  +" + won.gold + " Gold" +
                (won.talentPoints > 0 ? "  +" + won.talentPoints + " Talent Points" : "") +
                (won.battlepassXp > 0 ? "  +" + won.battlepassXp + " BattlePass XP" : ""));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
    }

    public static class CrateTier {
        public String name;
        public List<CrateReward> rewards = new ArrayList<>();
    }

    public static class CrateReward {
        public float chance;
        public int gold;
        public int talentPoints;
        public int battlepassXp;
    }
}
