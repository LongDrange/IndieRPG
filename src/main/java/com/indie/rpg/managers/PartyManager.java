package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
    private final IndieRPG plugin;
    private final Map<String, Party> parties = new HashMap<String, Party>();
    private final Map<UUID, String> playerParty = new HashMap<UUID, String>();
    private final Map<UUID, UUID> pendingInvite = new HashMap<UUID, UUID>();

    public PartyManager(IndieRPG plugin) { this.plugin = plugin; }

    public Party getParty(Player player) {
        String id = playerParty.get(player.getUniqueId());
        return id == null ? null : parties.get(id);
    }

    public Party create(Player leader) {
        leave(leader);
        Party party = new Party(UUID.randomUUID().toString(), leader.getUniqueId());
        party.getMembers().add(leader.getUniqueId());
        parties.put(party.getId(), party);
        playerParty.put(leader.getUniqueId(), party.getId());
        leader.sendMessage("§a隊伍已建立。");
        return party;
    }

    public void invite(Player inviter, Player target) {
        Party party = getParty(inviter);
        if (party == null) party = create(inviter);
        if (getParty(target) != null) {
            inviter.sendMessage("§c對方已有隊伍。");
            return;
        }
        int max = plugin.getConfigManager().get("parties").getInt("max-members", 6);
        if (party.getMembers().size() >= max) {
            inviter.sendMessage("§c隊伍已滿。");
            return;
        }
        pendingInvite.put(target.getUniqueId(), inviter.getUniqueId());
        inviter.sendMessage("§a已邀請 " + target.getName());
        target.sendMessage("§e" + inviter.getName() + " 邀請你加入隊伍，輸入 /party accept 接受");
    }

    public void accept(Player player) {
        UUID inviterUuid = pendingInvite.remove(player.getUniqueId());
        if (inviterUuid == null) {
            player.sendMessage("§c沒有待接受的邀請。");
            return;
        }
        Player inviter = Bukkit.getPlayer(inviterUuid);
        if (inviter == null) {
            player.sendMessage("§c邀請者已離線。");
            return;
        }
        Party party = getParty(inviter);
        if (party == null) party = create(inviter);
        party.getMembers().add(player.getUniqueId());
        playerParty.put(player.getUniqueId(), party.getId());
        party.broadcast("§a" + player.getName() + " 加入了隊伍");
    }

    public void leave(Player player) {
        Party party = getParty(player);
        if (party == null) return;
        party.getMembers().remove(player.getUniqueId());
        playerParty.remove(player.getUniqueId());
        party.broadcast("§7" + player.getName() + " 離開了隊伍");
        if (party.getMembers().isEmpty()) {
            parties.remove(party.getId());
        } else if (party.getLeader().equals(player.getUniqueId())) {
            party.setLeader(party.getMembers().get(0));
            Player newLeader = Bukkit.getPlayer(party.getLeader());
            if (newLeader != null) party.broadcast("§e新隊長：" + newLeader.getName());
        }
    }

    public List<Player> getNearbyMembers(Player player, double radius) {
        Party party = getParty(player);
        if (party == null) return Collections.singletonList(player);
        List<Player> result = new ArrayList<Player>();
        for (UUID id : party.getMembers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.getWorld().equals(player.getWorld())
                    && p.getLocation().distance(player.getLocation()) <= radius) {
                result.add(p);
            }
        }
        return result;
    }

    public static class Party {
        private final String id;
        private UUID leader;
        private final List<UUID> members = new ArrayList<UUID>();

        public Party(String id, UUID leader) {
            this.id = id;
            this.leader = leader;
        }
        public String getId() { return id; }
        public UUID getLeader() { return leader; }
        public void setLeader(UUID leader) { this.leader = leader; }
        public List<UUID> getMembers() { return members; }
        public void broadcast(String msg) {
            for (UUID id : members) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) p.sendMessage(msg);
            }
        }
    }
}