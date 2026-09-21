package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 屬性引擎：集中計算玩家最終屬性。
 * 目前支援基礎值 + 職業加成；後續會擴充天賦、裝備、公會、寵物。
 */
public class AttributeEngine {
    private final IndieRPG plugin;

    public AttributeEngine(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void recalculate(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Map<String, Double> total = new HashMap<String, Double>();

        // 1. 基礎值（attributes.yml）
        ConfigurationSection base = plugin.getConfigManager().get("attributes")
                .getConfigurationSection("attributes");
        if (base != null) {
            for (String key : base.getKeys(false)) {
                ConfigurationSection sec = base.getConfigurationSection(key);
                if (sec == null) continue;
                total.put(key.toLowerCase(), sec.getDouble("default", 0D));
            }
        }

        // 2. 職業加成
        if (plugin.getJobManager() != null) {
            merge(total, plugin.getJobManager().calcAttributes(data));
        }

        data.computedAttributes = total;
        applyVanilla(player, total);
    }

    private void applyVanilla(Player player, Map<String, Double> total) {
        double maxHealth = total.containsKey("health") ? total.get("health") : 20D;
        AttributeInstance hp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(maxHealth);
            if (player.getHealth() > maxHealth) player.setHealth(maxHealth);
        }

        double speed = total.containsKey("move-speed") ? total.get("move-speed") : 0.1D;
        AttributeInstance sp = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (sp != null) sp.setBaseValue(speed);
    }

    public static void merge(Map<String, Double> target, Map<String, Double> add) {
        if (add == null) return;
        for (Map.Entry<String, Double> e : add.entrySet()) {
            String key = e.getKey().toLowerCase();
            Double old = target.get(key);
            target.put(key, old == null ? e.getValue() : old + e.getValue());
        }
    }

    public double get(Player player, String key) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Map<String, Double> attrs = data.computedAttributes;
        if (attrs == null) {
            recalculate(player);
            attrs = data.computedAttributes;
        }
        Double v = attrs == null ? null : attrs.get(key.toLowerCase());
        return v == null ? 0D : v;
    }

    public Map<String, Double> getAll(Player player) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.computedAttributes == null) recalculate(player);
        return data.computedAttributes == null
                ? Collections.<String, Double>emptyMap()
                : data.computedAttributes;
    }
}