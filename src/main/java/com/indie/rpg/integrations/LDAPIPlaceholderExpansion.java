package com.indie.rpg.integrations;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.GuildManager;
import com.indie.rpg.managers.JobManager;
import com.indie.rpg.managers.PartyManager;
import com.indie.rpg.managers.PlayerDataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI 擴展，繼承 PlaceholderExpansion。
 * 註冊識別碼：ldapi（使用 %ldapi_xxx%）
 */
public class LDAPIPlaceholderExpansion extends PlaceholderExpansion {
    private final IndieRPG plugin;

    public LDAPIPlaceholderExpansion(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "ldapi";
    }

    @Override
    public String getAuthor() {
        return "LongDrange";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) return "";
        return resolve(player, params);
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
}