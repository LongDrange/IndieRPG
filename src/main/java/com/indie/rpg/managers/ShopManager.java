package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 1.12.2 兼容的簡單金幣商店。 */
public class ShopManager {
    private final IndieRPG plugin;
    private final Map<String, ShopItem> items = new LinkedHashMap<>();

    public ShopManager(IndieRPG plugin) { this.plugin = plugin; loadShop(); }

    public void loadShop() {
        items.clear();
        ConfigurationSection root = plugin.getConfig().getConfigurationSection("shop.items");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) continue;
            ShopItem item = new ShopItem();
            item.id = id;
            item.price = Math.max(0, section.getInt("price", 0));
            item.itemId = section.getString("item", id);
            item.material = Material.matchMaterial(section.getString("material", "PAPER"));
            item.name = ChatColor.translateAlternateColorCodes('&', section.getString("display-name", id));
            items.put(id.toLowerCase(), item);
        }
        plugin.getLogger().info("已載入 " + items.size() + " 個商店商品");
    }

    public Inventory openShop(Player player) {
        Inventory inventory = org.bukkit.Bukkit.createInventory(null, 54, ChatColor.GOLD + "LDAPI 金幣商店");
        int slot = 0;
        for (ShopItem shopItem : items.values()) {
            ItemStack icon = new ItemStack(shopItem.material == null ? Material.PAPER : shopItem.material);
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(shopItem.name);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "價格：" + shopItem.price + " 金幣");
                lore.add(ChatColor.YELLOW + "點擊購買");
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }
            inventory.setItem(slot++, icon);
            if (slot >= inventory.getSize()) break;
        }
        player.openInventory(inventory);
        return inventory;
    }

    public boolean buy(Player player, String id) {
        ShopItem item = items.get(id.toLowerCase());
        if (item == null) return false;
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.gold < item.price) { player.sendMessage(ChatColor.RED + "金幣不足。"); return false; }
        ItemStack result = plugin.getItemManager().getItem(item.itemId);
        if (result == null) result = new ItemStack(item.material == null ? Material.PAPER : item.material);
        data.gold -= item.price;
        player.getInventory().addItem(result);
        player.sendMessage(ChatColor.GREEN + "購買成功：" + item.name);
        return true;
    }

    public Map<String, ShopItem> getItems() { return items; }
    public static class ShopItem { public String id, itemId, name; public int price; public Material material; }
}
