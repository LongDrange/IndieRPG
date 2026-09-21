package com.indie.rpg.gui;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.GuildManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildInfoGui extends AbstractGui {
    private final IndieRPG plugin;

    public GuildInfoGui(IndieRPG plugin) {
        super(ChatColor.DARK_GREEN + "公會資訊", 6);
        this.plugin = plugin;
    }

    @Override
    public void setup(Player player) {
        GuildManager.Guild guild = plugin.getGuildManager().getGuild(player);
        if (guild == null) {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "你沒有公會");
                item.setItemMeta(meta);
            }
            set(22, item, null);
            return;
        }

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + guild.getName());
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "等級：" + guild.getLevel());
            lore.add(ChatColor.GRAY + "經驗：" + guild.getExp());
            lore.add(ChatColor.GRAY + "成員：" + guild.getMembers().size());
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        set(4, info, null);

        int slot = 19;
        for (UUID id : guild.getMembers().keySet()) {
            String name = Bukkit.getOfflinePlayer(id).getName();
            GuildManager.GuildRank rank = guild.getMembers().get(id);
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            ItemMeta headMeta = head.getItemMeta();
            if (headMeta != null) {
                headMeta.setDisplayName(ChatColor.YELLOW + (name == null ? "未知" : name));
                List<String> hl = new ArrayList<String>();
                hl.add(ChatColor.GRAY + "職位：" + rank.name());
                headMeta.setLore(hl);
                head.setItemMeta(headMeta);
            }
            set(slot++, head, null);
            if (slot >= 45) break;
        }
    }
}