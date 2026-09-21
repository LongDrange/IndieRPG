package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GuildManager {
    private final IndieRPG plugin;
    private final Map<String, Guild> guilds = new HashMap<String, Guild>();
    private final Map<UUID, String> playerGuild = new HashMap<UUID, String>();
    private final Map<UUID, UUID> pendingInvite = new HashMap<UUID, UUID>();
    private long nextId = 1L;

    public GuildManager(IndieRPG plugin) { this.plugin = plugin; }

    public Guild getGuild(Player player) {
        String id = playerGuild.get(player.getUniqueId());
        return id == null ? null : guilds.get(id);
    }

    public Guild createGuild(Player founder, String name) {
        if (getGuild(founder) != null) {
            founder.sendMessage("§c你已經有公會。");
            return null;
        }
        for (Guild g : guilds.values()) {
            if (g.getName().equalsIgnoreCase(name)) {
                founder.sendMessage("§c公會名稱已被使用。");
                return null;
            }
        }
        int cost = plugin.getConfigManager().get("guilds").getInt("creation-cost", 5000);
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(founder);
        if (data.gold < cost) {
            founder.sendMessage("§c需要 " + cost + " 金幣。");
            return null;
        }
        data.gold -= cost;

        Guild guild = new Guild(String.valueOf(nextId++), name, founder.getUniqueId());
        guild.getMembers().put(founder.getUniqueId(), GuildRank.LEADER);
        guilds.put(guild.getId(), guild);
        playerGuild.put(founder.getUniqueId(), guild.getId());
        founder.sendMessage("§a公會建立成功：" + name);
        return guild;
    }

    public void invite(Player inviter, Player target) {
        Guild guild = getGuild(inviter);
        if (guild == null) {
            inviter.sendMessage("§c你沒有公會。");
            return;
        }
        if (getGuild(target) != null) {
            inviter.sendMessage("§c對方已有公會。");
            return;
        }
        pendingInvite.put(target.getUniqueId(), inviter.getUniqueId());
        target.sendMessage("§e" + inviter.getName() + " 邀請你加入公會 " + guild.getName() + "，輸入 /guild accept");
    }

    public void accept(Player player) {
        UUID inviterUuid = pendingInvite.remove(player.getUniqueId());
        if (inviterUuid == null) {
            player.sendMessage("§c沒有邀請。");
            return;
        }
        Player inviter = Bukkit.getPlayer(inviterUuid);
        if (inviter == null) {
            player.sendMessage("§c邀請者離線。");
            return;
        }
        Guild guild = getGuild(inviter);
        if (guild == null) {
            player.sendMessage("§c公會不存在。");
            return;
        }
        guild.getMembers().put(player.getUniqueId(), GuildRank.MEMBER);
        playerGuild.put(player.getUniqueId(), guild.getId());
        guild.broadcast("§a" + player.getName() + " 加入了公會");
    }

    public void leave(Player player) {
        Guild guild = getGuild(player);
        if (guild == null) return;
        guild.getMembers().remove(player.getUniqueId());
        playerGuild.remove(player.getUniqueId());
        guild.broadcast("§7" + player.getName() + " 離開了公會");
        if (guild.getMembers().isEmpty()) {
            guilds.remove(guild.getId());
        }
    }

    public void addExp(Player player, long amount) {
        Guild guild = getGuild(player);
        if (guild == null) return;
        guild.setExp(guild.getExp() + amount);
        int maxLevel = plugin.getConfigManager().get("guilds").getInt("max-level", 20);
        while (guild.getLevel() < maxLevel && guild.getExp() >= expNeeded(guild.getLevel())) {
            guild.setExp(guild.getExp() - expNeeded(guild.getLevel()));
            guild.setLevel(guild.getLevel() + 1);
            guild.broadcast("§6[公會] 等級提升至 " + guild.getLevel());
        }
    }

    private long expNeeded(int level) {
        ConfigurationSection sec = plugin.getConfigManager().get("guilds")
                .getConfigurationSection("level-up-exp");
        if (sec != null && sec.contains(String.valueOf(level))) {
            return sec.getLong(String.valueOf(level));
        }
        return level * 10000L;
    }

    public Map<String, Double> calcBuffs(Player player) {
        Map<String, Double> result = new HashMap<String, Double>();
        Guild guild = getGuild(player);
        if (guild == null) return result;
        ConfigurationSection buffs = plugin.getConfigManager().get("guilds")
                .getConfigurationSection("buffs");
        if (buffs == null) return result;
        for (String lvlKey : buffs.getKeys(false)) {
            int lvl;
            try { lvl = Integer.parseInt(lvlKey); } catch (NumberFormatException e) { continue; }
            if (guild.getLevel() < lvl) continue;
            ConfigurationSection sec = buffs.getConfigurationSection(lvlKey);
            if (sec == null) continue;
            for (String k : sec.getKeys(false)) {
                result.put(k.toLowerCase(), sec.getDouble(k));
            }
        }
        return result;
    }

    public enum GuildRank { LEADER, OFFICER, MEMBER }

    public static class Guild {
        private final String id;
        private final String name;
        private UUID leader;
        private int level = 1;
        private long exp = 0;
        private long bank = 0;
        private final Map<UUID, GuildRank> members = new LinkedHashMap<UUID, GuildRank>();

        public Guild(String id, String name, UUID leader) {
            this.id = id;
            this.name = name;
            this.leader = leader;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public UUID getLeader() { return leader; }
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        public long getExp() { return exp; }
        public void setExp(long exp) { this.exp = exp; }
        public long getBank() { return bank; }
        public void setBank(long bank) { this.bank = bank; }
        public Map<UUID, GuildRank> getMembers() { return members; }
        public void broadcast(String msg) {
            for (UUID id : members.keySet()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) p.sendMessage(msg);
            }
        }
    }
}