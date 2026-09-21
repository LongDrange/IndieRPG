package com.indie.rpg.integrations;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventExecutor;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * MythicMobs 4.x bridge。使用官方 Bukkit API 的反射入口，讓 LDAPI 在未安裝
 * MythicMobs 時仍可載入，並避免把第三方 jar 打包進 LDAPI。
 */
public class MythicMobsHook implements Listener {
    private final IndieRPG plugin;
    private Object mythicApi;
    private boolean enabled;

    public MythicMobsHook(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        Plugin mythic = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythic == null || !mythic.isEnabled()) {
            plugin.getLogger().info("MythicMobs 未安裝，跳過 MythicMobs API 整合。");
            return;
        }
        try {
            Class<?> mythicBukkit = Class.forName("io.lumine.xikage.mythicmobs.bukkit.MythicBukkit");
            Object instance = mythicBukkit.getMethod("inst").invoke(null);
            mythicApi = instance.getClass().getMethod("getAPIHelper").invoke(instance);
            enabled = true;
            registerMythicEvent("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent", "death");
            registerMythicEvent("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent", "spawn");
            plugin.getLogger().info("MythicMobs API 整合已啟用。");
        } catch (Exception ex) {
            plugin.getLogger().warning("MythicMobs API 版本不相容，已停用整合：" + ex.getClass().getSimpleName());
        }
    }

    public boolean isEnabled() { return enabled; }

    /** 以 MythicMobs APIHelper 施放 skill，skill 名稱來自 mythicmobs.yml。 */
    public boolean castSkill(LivingEntity caster, String skill) {
        if (!enabled || mythicApi == null || skill == null || skill.trim().isEmpty()) return false;
        try {
            Method cast = findMethod(mythicApi.getClass(), "castSkill", 2);
            if (cast == null) return false;
            Object result = cast.invoke(mythicApi, caster, skill);
            return !(result instanceof Boolean) || (Boolean) result;
        } catch (Exception ex) {
            plugin.getLogger().warning("施放 MythicMobs skill 失敗：" + skill);
            return false;
        }
    }

    private void registerMythicEvent(String className, final String type) {
        try {
            final Class<?> eventClass = Class.forName(className);
            EventExecutor executor = new EventExecutor() {
                @Override public void execute(Listener listener, Event event) {
                    handleEvent(event, type);
                }
            };
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) eventClass,
                    this, org.bukkit.event.EventPriority.NORMAL, executor, plugin);
        } catch (ClassNotFoundException ignored) {
            plugin.getLogger().warning("找不到 MythicMobs 事件類別：" + className);
        }
    }

    private void handleEvent(Event event, String type) {
        String mobId = invokeString(event, "getMobType", "getMobName", "getMobId");
        if (mobId == null) mobId = "unknown";
        Map<?, ?> rules = plugin.getConfigManager().get("mythicmobs").getConfigurationSection("mythicmobs.events") == null
                ? null : plugin.getConfigManager().get("mythicmobs").getConfigurationSection("mythicmobs.events").getValues(false);
        if (rules == null) return;
        Object raw = rules.get(mobId.toLowerCase());
        if (!(raw instanceof org.bukkit.configuration.ConfigurationSection)) return;
        org.bukkit.configuration.ConfigurationSection rule = (org.bukkit.configuration.ConfigurationSection) raw;
        String skill = rule.getString(type + ".skill");
        Player player = findPlayer(event);
        if (skill != null && player != null) castSkill(player, skill);
        if ("death".equals(type) && player != null) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            data.gold += rule.getInt("death.gold", 0);
            int xp = rule.getInt("death.battlepass-xp", 0);
            if (xp > 0) plugin.getBattlePassManager().addXP(player, xp);
            String item = rule.getString("death.item");
            if (item != null) {
                org.bukkit.inventory.ItemStack stack = plugin.getItemManager().getItem(item);
                if (stack != null) player.getInventory().addItem(stack);
            }
        }
    }

    private Player findPlayer(Event event) {
        if (event instanceof EntityDeathEvent) {
            Player p = ((EntityDeathEvent) event).getEntity().getKiller();
            if (p != null) return p;
        }
        Object entity = invoke(event, "getEntity", "getMob");
        if (entity instanceof Entity && ((Entity) entity).getLastDamageCause() instanceof EntityDeathEvent) {
            return ((EntityDeathEvent) ((Entity) entity).getLastDamageCause()).getEntity().getKiller();
        }
        return null;
    }

    private String invokeString(Object target, String... names) {
        Object value = invoke(target, names);
        return value == null ? null : String.valueOf(value);
    }

    private Object invoke(Object target, String... names) {
        for (String name : names) try { return target.getClass().getMethod(name).invoke(target); } catch (Exception ignored) { }
        return null;
    }

    private Method findMethod(Class<?> type, String name, int count) {
        for (Method method : type.getMethods()) if (method.getName().equals(name) && method.getParameterTypes().length == count) return method;
        return null;
    }
}
