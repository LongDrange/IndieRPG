package com.indie.rpg.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Minecraft 1.12.2 可用的名稱與 Lore 識別工具。 */
public final class ItemLoreUtil {
    private ItemLoreUtil() { }

    public static String plain(String value) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', value == null ? "" : value))
                .trim().toLowerCase(Locale.ENGLISH);
    }

    public static List<String> lore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Collections.emptyList();
        ItemMeta meta = item.getItemMeta();
        return meta == null || !meta.hasLore() || meta.getLore() == null
                ? Collections.<String>emptyList() : new ArrayList<String>(meta.getLore());
    }

    public static String displayName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
    }

    public static String value(ItemStack item, String key) {
        String prefix = plain(key) + ":";
        for (String line : lore(item)) {
            String normalized = plain(line);
            if (normalized.startsWith(prefix)) return normalized.substring(prefix.length()).trim();
        }
        return null;
    }

    public static boolean hasValue(ItemStack item, String key, String expected) {
        String actual = value(item, key);
        return actual != null && actual.equals(plain(expected));
    }
}
