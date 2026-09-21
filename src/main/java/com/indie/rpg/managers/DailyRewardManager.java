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

/**
 * 每日獎勵系統：支援每日簽到、冷卻、GUI 展示。
 */
public class DailyRewardManager {
    private final IndieRPG plugin;
    private final Map<UUID, Long> lastClaim = new HashMap<>();

    public DailyRewardManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadRewards();
    }

    public void loadRewards() {
        ConfigurationSection section = plugin.getConfigManager().get("dailyreward").getConfigurationSection("dailyreward.rewards");
        if (section == null) {
            plugin.getLogger().warning("未找到 dailyreward.rewards 配置，使用預設每日獎勵");
            return;
        }
        // 讀取配置，後續可擴充到連續簽到.
    }

    public boolean canClaim(Player player) {
        long now = System.currentTimeMillis();
        long last = lastClaim.getOrDefault(player.getUniqueId(), 0L);
        long cooldownMillis = 1000L * 60L * 60L * plugin.getConfig().getInt("dailyreward.cooldown-hours", 24);
        return (now - last) >= cooldownMillis;
    }

    public boolean claimDaily(Player player) {
        if (!canClaim(player)) {
            player.sendMessage(ChatColor.RED + "你還沒有到達每日簽到時間。");
            return false;
        }

        int gold = plugin.getConfig().getInt("dailyreward.gold", 100);
        int xp = plugin.getConfig().getInt("dailyreward.xp", 25);
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.gold += gold;
        lastClaim.put(player.getUniqueId(), System.currentTimeMillis());
        data.lastDailyClaim = System.currentTimeMillis();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dailyreward.message", "&e[每日獎勵] &7已領取。"))
            .replace("{gold}", String.valueOf(gold)).replace("{xp}", String.valueOf(xp)));
        return true;
    }

    public Inventory openRewardGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "LDAPI 每日簽到");
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "領取每日獎勵");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "金幣：" + plugin.getConfig().getInt("dailyreward.gold", 100));
            lore.add(ChatColor.GRAY + "經驗：" + plugin.getConfig().getInt("dailyreward.xp", 25));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        inv.setItem(13, item);
        player.openInventory(inv);
        return inv;
    }
}
