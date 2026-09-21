package com.indie.rpg.integrations;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.GuildManager;
import com.indie.rpg.managers.JobManager;
import com.indie.rpg.managers.PartyManager;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * PlaceholderAPI 擴充（反射版）。
 * 支援：%ldapi_gold%  %ldapi_job%  %ldapi_job_level%  ...
 */
public class LDAPIPlaceholders {
    private final IndieRPG plugin;
    private boolean registered = false;

    public LDAPIPlaceholders(IndieRPG plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().info("未偵測到 PlaceholderAPI，跳過變數註冊");
            return;
        }
        try {
            Class<?> expansionClass = Class.forName(
                    "me.clip.placeholderapi.expansion.PlaceholderExpansion");
            Object expansion = java.lang.reflect.Proxy.newProxyInstance(
                    expansionClass.getClassLoader(),
                    new Class<?>[]{expansionClass},
                    new java.lang.reflect.InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            String name = method.getName();
                            if (name.equals("getIdentifier")) return "ldapi";
                            if (name.equals("getAuthor")) return "LongDrange";
                            if (name.equals("getVersion")) return "1.0.0";
                            if (name.equals("persist")) return Boolean.TRUE;
                            if (name.equals("onRequest")) {
                                if (args != null && args.length == 2
                                        && args[0] instanceof Player
                                        && args[1] instanceof String) {
                                    return resolve((Player) args[0], (String) args[1]);
                                }
                                return "";
                            }
                            if (name.equals("toString")) return "LDAPIPlaceholders";
                            if (name.equals("equals")) return proxy == args[0];
                            if (name.equals("hashCode")) return System.identityHashCode(proxy);
                            return null;
                        }
                    });

            Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method registerMethod = null;
            for (Method m : papi.getMethods()) {
                if (m.getName().equals("registerExpansion") && m.getParameterTypes().length == 1) {
                    registerMethod = m;
                    break;
                }
            }
            if (registerMethod != null) {
                registerMethod.invoke(null, expansion);
                registered = true;
                plugin.getLogger().info("PlaceholderAPI 變數已註冊（識別碼：ldapi）");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("註冊 PlaceholderAPI 失敗：" + e.getMessage());
        }
    }

    private String resolve(Player player, String params) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        String p = params.toLowerCase();

        if (p.equals("gold")) return String.valueOf(data.gold);
        if (p.equals("job")) {
            JobManager.JobDefinition def = plugin.getJobManager().get(data.job);
            return def == null ? "無" : def.displayName;
        }
        if (p.equals("job_level")) return String.valueOf(data.jobLevel);
        if (p.equals("job_exp")) return String.valueOf(data.jobExp);
        if (p.equals("battlepass_level")) return String.valueOf(data.battlePassLevel);
        if (p.equals("battlepass_xp")) return String.valueOf(data.battlePassXP);
        if (p.equals("talent_points")) return String.valueOf(data.talentPoints);
        if (p.equals("rank")) return plugin.getRankManager().getRankDisplay(data.rank);
        if (p.equals("attack")) return fmt(plugin.getAttributeEngine().get(player, "attack"));
        if (p.equals("defense")) return fmt(plugin.getAttributeEngine().get(player, "defense"));
        if (p.equals("health")) return fmt(plugin.getAttributeEngine().get(player, "health"));

        if (p.equals("guild")) {
            GuildManager.Guild g = plugin.getGuildManager().getGuild(player);
            return g == null ? "無" : g.getName();
        }
        if (p.equals("guild_level")) {
            GuildManager.Guild g = plugin.getGuildManager().getGuild(player);
            return g == null ? "0" : String.valueOf(g.getLevel());
        }
        if (p.equals("party_size")) {
            PartyManager.Party pt = plugin.getPartyManager().getParty(player);
            return pt == null ? "0" : String.valueOf(pt.getMembers().size());
        }
        if (p.startsWith("rep_")) {
            String faction = params.substring(4);
            return String.valueOf(plugin.getReputationManager().getRep(player, faction));
        }
        return "";
    }

    private String fmt(double v) {
        if (v == Math.floor(v)) return String.valueOf((long) v);
        return String.format("%.1f", v);
    }

    public boolean isRegistered() {
        return registered;
    }
}