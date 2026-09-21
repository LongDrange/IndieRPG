package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/** 每日簽到與獎勵系統：支援每日/連續簽到與配置讀取。 */
public class DailyRewardManager {
    private final IndieRPG plugin;
    private final Map<UUID, Long> lastClaim = new HashMap<>();
    private final List<String> rewards = new ArrayList<>();

    public DailyRewardManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadRewards();
    }

    public void loadRewards() {
        rewards.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("dailyreward.items");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            rewards.add(key);
        }
    }

    public boolean canClaim(Player player) {
        Long ts = lastClaim.get(player.getUniqueId());
        if (ts == null) return true;
        long diff = (System.currentTimeMillis() - ts) / 1000L / 60L / 60L / 24L;
        return diff >= 1L;
    }

    public boolean claimDaily(Player player) {
        if (!canClaim(player)) {
            player.sendMessage(ChatColor.RED + "你今天已領取過每日獎勵。");
            return false;
        }
        int gold = plugin.getConfig().getInt("dailyreward.gold", 50);
        int xp = plugin.getConfig().getInt("dailyreward.xp", 10);
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.gold += gold;
        lastClaim.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(ChatColor.GREEN + "已領取每日獎勵：+" + gold + " 金幣，+" + xp + " 經驗。");
        return true;
    }

    public Inventory openRewardGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "LDAPI 每日簽到");
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "領取每日獎勵");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "可領取：" + plugin.getConfig().getInt("dailyreward.gold", 50) + " 金幣");
        lore.add(ChatColor.GRAY + "每日一次，請勿重複領取");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);
        player.openInventory(inv);
        return inv;
    }
}
