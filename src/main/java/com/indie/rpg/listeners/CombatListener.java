package com.indie.rpg.listeners;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.AttributeEngine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * 戰鬥計算：讓 AttributeEngine 的屬性真正影響傷害。
 * 攻擊方：傷害 = 原傷 + attack，爆擊乘上 (1 + crit-damage)
 * 防守方：減傷 = defense / (defense + 100)，上限 80%
 */
public class CombatListener implements Listener {
    private final IndieRPG plugin;

    public CombatListener(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player attacker = (Player) event.getDamager();
        AttributeEngine engine = plugin.getAttributeEngine();

        double base = event.getDamage();
        double attack = engine.get(attacker, "attack");
        double critChance = engine.get(attacker, "crit-chance");
        double critDamage = engine.get(attacker, "crit-damage");
        if (critDamage <= 0) critDamage = 0.5D;

        boolean crit = Math.random() < critChance;
        double damage = base + attack;
        if (crit) {
            damage *= (1.0D + critDamage);
            attacker.sendMessage(ChatColor.YELLOW + "爆擊！");
        }
        event.setDamage(damage);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDefend(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        double defense = plugin.getAttributeEngine().get(victim, "defense");
        if (defense <= 0) return;
        double reduction = defense / (defense + 100.0D);
        if (reduction > 0.8D) reduction = 0.8D;
        event.setDamage(event.getDamage() * (1.0D - reduction));
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        // 職業經驗
        if (plugin.getJobManager() != null) {
            plugin.getJobManager().addExp(killer, 5L);
        }
    }
}