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

public class JewelryManager {

    private final IndieRPG plugin;
    private final Map<String, JewelrySet> sets = new HashMap<>();

    public JewelryManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadSets();
    }

    public void loadSets() {
        sets.clear();
        if (plugin.getConfig().contains("jewelry.sets")) {
            for (String key : plugin.getConfig().getConfigurationSection("jewelry.sets").getKeys(false)) {
                String path = "jewelry.sets." + key;
                JewelrySet set = new JewelrySet();
                set.id = key;
                set.jewelry = plugin.getConfig().getStringList(path + ".jewelry");
                set.effect = plugin.getConfig().getString(path + ".effect", "");
                sets.put(key, set);
            }
        }
        plugin.getLogger().info("Loaded " + sets.size() + " jewelry sets");
    }

    public void openJewelryGUI(Player player) {
        int pages = plugin.getConfig().getInt("jewelry.pages", 3);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.translateAlternateColorCodes('&', "&b&lJewelry Slots &7[" + pages + " pages]"));

        int slotCount = plugin.getConfig().getConfigurationSection("jewelry.slots").getKeys(false).size();
        for (int i = 0; i < slotCount && i < 27; i++) {
            String perm = plugin.getConfig().getString("jewelry.slots." + i + ".permission", "");
            ItemStack icon;
            if (perm.isEmpty() || player.hasPermission(perm)) {
                icon = new ItemStack(Material.EMERALD);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Slot " + (i + 1) + " (Unlocked)");
                icon.setItemMeta(meta);
            } else {
                icon = new ItemStack(Material.IRON_FENCE);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Slot " + (i + 1) + " (Locked)");
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "Requires: " + perm));
                icon.setItemMeta(meta);
            }
            inv.setItem(i, icon);
        }

        player.openInventory(inv);
    }

    public String detectJewelryFromLore(List<String> lores) {
        for (String lore : lores) {
            String stripped = ChatColor.stripColor(lore).trim().toLowerCase();
            if (stripped.startsWith("jewelry-id:")) {
                return stripped.substring("jewelry-id:".length()).trim();
            }
        }
        return null;
    }

    public void applySetBonuses(Player player, Set<String> equippedJewelry) {
        for (JewelrySet set : sets.values()) {
            if (equippedJewelry.containsAll(set.jewelry)) {
                if (!set.effect.isEmpty()) {
                    String[] parts = set.effect.split(":");
                    PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
                    int amp = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    if (type != null) {
                        player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp, true, false));
                    }
                }
            }
        }
    }

    public static class JewelrySet {
        public String id;
        public List<String> jewelry;
        public String effect;
    }
}
