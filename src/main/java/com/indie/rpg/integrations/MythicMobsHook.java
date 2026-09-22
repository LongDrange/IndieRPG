package com.indie.rpg.integrations;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Random;

/**
 * Optional MythicMobs 4.x bridge. No MythicMobs classes are referenced directly,
 * so LDAPI can still load when MythicMobs is absent.
 */
public class MythicMobsHook implements Listener {
    private final IndieRPG plugin;
    private final Random random = new Random();
    private Object apiHelper;
    private boolean enabled;

    public MythicMobsHook(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        if (!plugin.getConfigManager().get("mythicmobs").getBoolean("mythicmobs.enabled", true)) {
            plugin.getLogger().info("MythicMobs 整合已在配置中停用。");
            return;
        }
        Plugin mythic = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythic == null || !mythic.isEnabled()) {
            plugin.getLogger().info("MythicMobs 未安裝，跳過 API 整合。");
            return;
        }
        try {
            Class<?> bridge = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
            Object instance = bridge.getMethod("inst").invoke(null);
            apiHelper = instance.getClass().getMethod("getAPIHelper").invoke(instance);
            enabled = apiHelper != null;
            registerEvent("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent", "spawn");
            registerEvent("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent", "death");
            plugin.getLogger().info("MythicMobs API 整合已啟用。");
        } catch (Exception ex) {
            enabled = false;
            plugin.getLogger().warning("MythicMobs API 初始化失敗：" + ex.getClass().getSimpleName());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** 呼叫 MythicMobs APIHelper.castSkill，支援不同 4.x 方法參數順序。 */
    public boolean castSkill(LivingEntity caster, String skill) {
        if (!enabled || caster == null || skill == null || skill.trim().isEmpty()) return false;
        try {
            for (Method method : apiHelper.getClass().getMethods()) {
                if (!"castSkill".equals(method.getName()) || method.getParameterTypes().length != 2) continue;
                Class<?>[] types = method.getParameterTypes();
                Object[] args;
                if (types[0].isAssignableFrom(String.class) && types[1].isAssignableFrom(caster.getClass())) {
                    args = new Object[]{skill, caster};
                } else if (types[0].isAssignableFrom(caster.getClass()) && types[1].isAssignableFrom(String.class)) {
                    args = new Object[]{caster, skill};
                } else {
                    continue;
                }
                Object result = method.invoke(apiHelper, args);
                return !(result instanceof Boolean) || (Boolean) result;
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("施放 MythicMobs 技能失敗：" + skill);
        }
        return false;
    }

    private void registerEvent(String className, final String type) {
        try {
            final Class<?> eventClass = Class.forName(className);
            EventExecutor executor = new EventExecutor() {
                @Override public void execute(Listener ignored, Event event) {
                    handleMythicEvent(event, type);
                }
            };
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) eventClass, this,
                    EventPriority.NORMAL, executor, plugin);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().warning("找不到 MythicMobs 事件類別：" + className);
        }
    }

    private void handleMythicEvent(Event event, String type) {
        String mobId = readMobId(event);
        if (mobId == null) return;
        ConfigurationSection root = plugin.getConfigManager().get("mythicmobs")
                .getConfigurationSection("mythicmobs.events");
        if (root == null) return;
        ConfigurationSection rule = root.getConfigurationSection(mobId.toLowerCase(Locale.ENGLISH));
        if (rule == null) return;

        LivingEntity mob = readLivingEntity(event);
        Player killer = readKiller(event, mob);
        String skill = rule.getString(type + ".skill");
        if (skill != null && mob != null) castSkill(mob, skill);

        if ("death".equals(type) && killer != null) {
            giveDeathRewards(killer, rule.getConfigurationSection("death"));
        }
    }

    private void giveDeathRewards(Player player, ConfigurationSection reward) {
        if (reward == null) return;
        double chance = reward.getDouble("chance", 1.0D);
        if (chance < 1.0D && random.nextDouble() > chance) return;

        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.gold += Math.max(0, reward.getInt("gold", 0));
        int xp = reward.getInt("battlepass-xp", 0);
        if (xp > 0) plugin.getBattlePassManager().addXP(player, xp);

        String itemId = reward.getString("item");
        if (itemId != null && !itemId.trim().isEmpty()) {
            ItemStack item = plugin.getItemManager().getItem(itemId);
            if (item != null) player.getInventory().addItem(item);
        }
        String command = reward.getString("command");
        if (command != null && !command.trim().isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }

    private String readMobId(Object event) {
        Object mob = invoke(event, "getMob", "getMythicMob");
        Object type = mob == null ? null : invoke(mob, "getType");
        Object value = type == null ? null : invoke(type, "getInternalName", "getName");
        if (value == null) value = invoke(event, "getMobType", "getMobName", "getMobId");
        return value == null ? null : String.valueOf(value).toLowerCase(Locale.ENGLISH);
    }

    private LivingEntity readLivingEntity(Object event) {
        Object value = invoke(event, "getEntity", "getBukkitEntity");
        return value instanceof LivingEntity ? (LivingEntity) value : null;
    }

    private Player readKiller(Object event, LivingEntity mob) {
        Object killer = invoke(event, "getKiller", "getPlayer");
        if (killer instanceof Player) return (Player) killer;
        return mob == null ? null : mob.getKiller();
    }

    private Object invoke(Object target, String... names) {
        if (target == null) return null;
        for (String name : names) {
            try { return target.getClass().getMethod(name).invoke(target); }
            catch (Exception ignored) { }
        }
        return null;
    }
}
