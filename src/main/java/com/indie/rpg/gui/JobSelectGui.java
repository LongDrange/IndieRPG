package com.indie.rpg.gui;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.JobManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class JobSelectGui extends AbstractGui {
    private final IndieRPG plugin;

    public JobSelectGui(IndieRPG plugin) {
        super(ChatColor.DARK_PURPLE + "選擇職業", 3);
        this.plugin = plugin;
    }

    @Override
    public void setup(final Player player) {
        int slot = 10;
        for (final JobManager.JobDefinition def : plugin.getJobManager().getAll().values()) {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName(def.displayName);
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "最高等級：" + def.maxLevel);
            lore.add(ChatColor.GRAY + "每級加成：");
            for (Map.Entry<String, Double> e : def.attributesPerLevel.entrySet()) {
                lore.add(ChatColor.DARK_GRAY + "  " + e.getKey() + " +" + e.getValue());
            }
            lore.add("");
            lore.add(ChatColor.YELLOW + "點擊選擇");
            meta.setLore(lore);
            item.setItemMeta(meta);

            set(slot++, item, new Consumer<Player>() {
                @Override public void accept(Player p) {
                    plugin.getJobManager().setJob(p, def.id);
                    p.closeInventory();
                }
            });
            if (slot >= 17) break;
        }
    }
}