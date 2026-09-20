package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GuideManager {

    private final IndieRPG plugin;
    private final Map<String, GuideEntry> entries = new HashMap<>();

    public GuideManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadEntries();
    }

    public void loadEntries() {
        entries.clear();
        if (plugin.getConfig().contains("guide.entries")) {
            for (String key : plugin.getConfig().getConfigurationSection("guide.entries").getKeys(false)) {
                String path = "guide.entries." + key;
                GuideEntry entry = new GuideEntry();
                entry.id = key;
                entry.mobs = plugin.getConfig().getStringList(path + ".mobs");
                entry.requiredKills = plugin.getConfig().getInt(path + ".required-kills", 10);
                entry.unlockChance = plugin.getConfig().getDouble(path + ".unlock-chance", 25.0);
                entry.reward = plugin.getConfig().getString(path + ".reward", "");
                entry.effect = plugin.getConfig().getString(path + ".effect", "");
                entries.put(key, entry);
            }
        }
        plugin.getLogger().info("Loaded " + entries.size() + " guide entries");
    }

    public void onMonsterKill(Player player, LivingEntity entity, PlayerDataManager.PlayerData data) {
        String mobType = entity.getType().name().toLowerCase();
        Set<String> unlocked = getUnlockedEntries(data);

        for (GuideEntry entry : entries.values()) {
            if (unlocked.contains(entry.id)) continue;

            boolean matches = false;
            for (String mob : entry.mobs) {
                if (mobType.contains(mob.toLowerCase()) || mob.toLowerCase().contains(mobType)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) continue;

            if (Math.random() * 100 < entry.unlockChance) {
                unlocked.add(entry.id);
                player.sendMessage(ChatColor.GREEN + "[Guide] " + ChatColor.RESET +
                        "Unlocked entry: " + entry.id + "!");

                if (!entry.effect.isEmpty()) {
                    applyEffect(player, entry.effect);
                }
            }
        }
    }

    public Set<String> getUnlockedEntries(PlayerDataManager.PlayerData data) {
        return new HashSet<>();
    }

    private void applyEffect(Player player, String effectStr) {
        String[] parts = effectStr.split(":");
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
        int amp = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        if (type != null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp, true, false));
        }
    }

    public static class GuideEntry {
        public String id;
        public List<String> mobs;
        public int requiredKills;
        public double unlockChance;
        public String reward;
        public String effect;
    }
}
