package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.util.ItemLoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** 飾品成長來源：上線時間、現實時間與特殊物品。 */
public class GrowthManager {
    private final IndieRPG plugin;
    public GrowthManager(IndieRPG plugin) { this.plugin = plugin; }
    public void addOnlineMinute(Player player, String jewelryId) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.totalOnlineMinutes++;
        data.jewelryOnlineMinutes.put(jewelryId, data.jewelryOnlineMinutes.containsKey(jewelryId) ? data.jewelryOnlineMinutes.get(jewelryId) + 1L : 1L);
    }
    public void addRealTime(Player player, String jewelryId, long seconds) {
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.jewelryRealSeconds.put(jewelryId, data.jewelryRealSeconds.containsKey(jewelryId) ? data.jewelryRealSeconds.get(jewelryId) + seconds : seconds);
    }
    public boolean useGrowthItem(Player player, String jewelryId, ItemStack item) {
        String id = ItemLoreUtil.value(item, "item-id");
        if (id == null) id = ItemLoreUtil.value(item, "indierpg-id");
        if (id == null || !id.equalsIgnoreCase("growth_core")) return false;
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1); else player.getInventory().removeItem(item);
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.jewelryGrowthPoints.put(jewelryId, data.jewelryGrowthPoints.containsKey(jewelryId) ? data.jewelryGrowthPoints.get(jewelryId) + 1D : 1D);
        return true;
    }
}
