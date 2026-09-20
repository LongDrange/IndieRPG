package com.indie.rpg.commands;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandHandler implements CommandExecutor {

    private final IndieRPG plugin;

    public CommandHandler(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "indierpg":
                return handleIndieRPG(player, args);
            case "spacering":
                plugin.getSpaceRingManager().openSpaceRing(player, "Advanced Space Ring");
                return true;
            case "talent":
                plugin.getTalentManager().openTalentMenu(player);
                return true;
            case "task":
                plugin.getTaskManager().startTask(player);
                return true;
            case "crate":
                plugin.getCrateManager().openCrate(player, "common");
                return true;
        }
        return false;
    }

    private boolean handleIndieRPG(Player player, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("general.prefix", "&d[IndieRPG] &r"));

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "=== IndieRPG v2.0.0 ===");
            player.sendMessage(ChatColor.YELLOW + "/rpg info " + ChatColor.GRAY + "- Plugin info");
            player.sendMessage(ChatColor.YELLOW + "/rpg reload " + ChatColor.GRAY + "- Reload config");
            player.sendMessage(ChatColor.YELLOW + "/rpg give <item> " + ChatColor.GRAY + "- Give custom item");
            player.sendMessage(ChatColor.YELLOW + "/rpg items " + ChatColor.GRAY + "- List all custom items");
            player.sendMessage(ChatColor.YELLOW + "/rpg gold " + ChatColor.GRAY + "- Check your gold");
            player.sendMessage(ChatColor.YELLOW + "/rpg rank " + ChatColor.GRAY + "- Check your rank");
            player.sendMessage(ChatColor.YELLOW + "/rpg bp " + ChatColor.GRAY + "- BattlePass progress");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            player.sendMessage(prefix + "v2.0.0 (Config-driven)");
            player.sendMessage(ChatColor.GRAY + "MythicMobs: " +
                    (plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null ? "&aDetected" : "&cNot detected"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            player.sendMessage(prefix + "&aConfig reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /rpg give <item-id>");
                return true;
            }
            ItemStack item = plugin.getItemManager().getItem(args[1]);
            if (item == null) {
                player.sendMessage(ChatColor.RED + "Unknown item: " + args[1]);
                return true;
            }
            player.getInventory().addItem(item);
            player.sendMessage(prefix + "&7Gave: &f" + args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("items")) {
            List<String> ids = plugin.getItemManager().getItemIds();
            player.sendMessage(ChatColor.GOLD + "Custom Items (" + ids.size() + "):");
            for (String id : ids) {
                player.sendMessage(ChatColor.YELLOW + "  /rpg give " + id);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("gold")) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            player.sendMessage(prefix + "&7Gold: &6" + data.gold);
            return true;
        }

        if (args[0].equalsIgnoreCase("rank")) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            player.sendMessage(prefix + "&7Rank: " + plugin.getRankManager().getRankDisplay(data.rank));
            return true;
        }

        if (args[0].equalsIgnoreCase("bp")) {
            plugin.getBattlePassManager().showProgress(player);
            return true;
        }

        return false;
    }
}
