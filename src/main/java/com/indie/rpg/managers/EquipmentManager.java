package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.util.ItemLoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 裝備屬性：讀取物品 Lore 中 attribute-<key>: <value>，
 * 例如 attribute-attack: 15.0 會加到玩家攻擊力。
 */
public class EquipmentManager {
    private static final String PREFIX = "attribute-";
    private final IndieRPG plugin;

    public EquipmentManager(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public Map<String, Double> calcAttributes(Player player) {
        Map<String, Double> result = new HashMap<String, Double>();
        for (ItemStack item : player.getInventory().getArmorContents()) merge(result, readItem(item));
        merge(result, readItem(player.getInventory().getItemInMainHand()));
        merge(result, readItem(player.getInventory().getItemInOffHand()));
        for (ItemStack item : player.getInventory().getContents()) merge(result, readItem(item));
        return result;
    }

    private Map<String, Double> readItem(ItemStack item) {
        Map<String, Double> map = new HashMap<String, Double>();
        if (item == null) return map;
        for (String line : ItemLoreUtil.lore(item)) {
            String plain = ItemLoreUtil.plain(line);
            if (!plain.startsWith(PREFIX)) continue;
            int colon = plain.indexOf(':', PREFIX.length());
            if (colon < 0) continue;
            String key = plain.substring(PREFIX.length(), colon).trim();
            String val = plain.substring(colon + 1).trim();
            try {
                map.put(key, Double.parseDouble(val));
            } catch (NumberFormatException ignored) { }
        }
        return map;
    }

    private void merge(Map<String, Double> target, Map<String, Double> add) {
        for (Map.Entry<String, Double> e : add.entrySet()) {
            Double old = target.get(e.getKey());
            target.put(e.getKey(), old == null ? e.getValue() : old + e.getValue());
        }
    }
}