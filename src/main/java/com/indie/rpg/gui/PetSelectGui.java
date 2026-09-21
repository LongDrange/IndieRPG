package com.indie.rpg.gui;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PetManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PetSelectGui extends AbstractGui {
    private final IndieRPG plugin;

    public PetSelectGui(IndieRPG plugin) {
        super(ChatColor.DARK_AQUA + "寵物選擇", 3);
        this.plugin = plugin;
    }

    @Override
    public void setup(Player player) {
        int slot = 10;
        for (Map.Entry<String, PetManager.PetDefinition> e
                : plugin.getPetManager().getAll().entrySet()) {
            final PetManager.PetDefinition def = e.getValue();
            ItemStack item = new ItemStack(Material.MONSTER_EGG);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName(def.name);
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "最高等級：" + def.maxLevel);
            for (Map.Entry<String, Double> a : def.attributesPerLevel.entrySet()) {
                lore.add(ChatColor.DARK_GRAY + "  " + a.getKey() + " +" + a.getValue() + "/級");
            }
            lore.add("");
            lore.add(ChatColor.YELLOW + "點擊召喚");
            meta.setLore(lore);
            item.setItemMeta(meta);

            set(slot++, item, new Consumer<Player>() {
                @Override public void accept(Player p) {
                    plugin.getPetManager().summon(p, def.id);
                    p.closeInventory();
                }
            });
            if (slot >= 17) break;
        }
    }
}