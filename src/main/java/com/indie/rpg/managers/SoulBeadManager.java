package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SoulBeadManager {

    private final IndieRPG plugin;
    private final Map<String, BeadDef> beads = new HashMap<>();
    private final Map<String, BeadSet> sets = new HashMap<>();

    public SoulBeadManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadBeads();
    }

    public void loadBeads() {
        beads.clear();
        sets.clear();

        if (plugin.getConfig().contains("soul-beads.definitions")) {
            for (String key : plugin.getConfig().getConfigurationSection("soul-beads.definitions").getKeys(false)) {
                String path = "soul-beads.definitions." + key;
                BeadDef bead = new BeadDef();
                bead.id = key;
                bead.max = plugin.getConfig().getInt(path + ".max", 1);
                bead.permission = plugin.getConfig().getString(path + ".permission", "");
                bead.effect = plugin.getConfig().getString(path + ".effect", "");
                beads.put(key, bead);
            }
        }

        if (plugin.getConfig().contains("soul-beads.sets")) {
            for (String key : plugin.getConfig().getConfigurationSection("soul-beads.sets").getKeys(false)) {
                String path = "soul-beads.sets." + key;
                BeadSet set = new BeadSet();
                set.id = key;
                set.beads = plugin.getConfig().getStringList(path + ".beads");
                set.effect = plugin.getConfig().getString(path + ".effect", "");
                sets.put(key, set);
            }
        }

        plugin.getLogger().info("Loaded " + beads.size() + " soul beads, " + sets.size() + " bead sets");
    }

    public String detectBeadFromLore(List<String> lores) {
        for (String lore : lores) {
            String stripped = ChatColor.stripColor(lore).trim().toLowerCase();
            if (stripped.startsWith("soul-bead-id:")) {
                return stripped.substring("soul-bead-id:".length()).trim();
            }
        }
        return null;
    }

    public void openBeadGUI(Player player) {
        int pageSize = plugin.getConfig().getInt("soul-beads.page-size", 36);
        Inventory inv = Bukkit.createInventory(null, 45,
                ChatColor.translateAlternateColorCodes('&', "&d&lSoul Beads &7[Collect & Equip]"));

        int slot = 0;
        for (BeadDef bead : beads.values()) {
            if (slot >= pageSize) break;

            boolean unlocked = bead.permission.isEmpty() || player.hasPermission(bead.permission);

            Material mat = unlocked ? Material.NETHER_STAR : Material.GHAST_TEAR;
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + bead.id);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Max: " + bead.max);
            lore.add(ChatColor.GRAY + "Effect: " + bead.effect);
            if (!unlocked) {
                lore.add("");
                lore.add(ChatColor.RED + "Locked: " + bead.permission);
            } else {
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to equip!");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;
        }

        player.openInventory(inv);
    }

    public void applyBeadEffects(Player player, Set<String> equippedBeads) {
        for (String beadId : equippedBeads) {
            BeadDef bead = beads.get(beadId);
            if (bead != null && !bead.effect.isEmpty()) {
                applyEffect(player, bead.effect, 0);
            }
        }
        for (BeadSet set : sets.values()) {
            if (equippedBeads.containsAll(set.beads)) {
                if (!set.effect.isEmpty()) {
                    applyEffect(player, set.effect, 0);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "[SoulBeads] " + ChatColor.RESET +
                            "Set bonus active: " + set.id + "!");
                }
            }
        }
    }

    private void applyEffect(Player player, String effectStr, int amplifier) {
        String[] parts = effectStr.split(":");
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
        int amp = parts.length > 1 ? Integer.parseInt(parts[1]) : amplifier;
        if (type != null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp, true, false));
        }
    }

    public static class BeadDef {
        public String id;
        public int max;
        public String permission;
        public String effect;
    }

    public static class BeadSet {
        public String id;
        public List<String> beads;
        public String effect;
    }
}
