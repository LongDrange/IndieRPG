package com.indie.rpg.commands;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            case "indierpg": return handleIndieRPG(player, args);
            case "spacering":
                plugin.getSpaceRingManager().openSpaceRing(player, 27);
                return true;
            case "talent":
                plugin.getTalentManager().openTalentMenu(player);
                return true;
            case "task":
                plugin.getTaskManager().startTask(player, "slayer");
                return true;
            case "crate":
                plugin.getCrateManager().openCrate(player, "common");
                return true;
        }
        return false;
    }

    private boolean handleIndieRPG(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "=== IndieRPG v1.0.0 ===");
            player.sendMessage(ChatColor.YELLOW + "/rpg info " + ChatColor.GRAY + "- Plugin info");
            player.sendMessage(ChatColor.YELLOW + "/rpg reload " + ChatColor.GRAY + "- Reload config");
            player.sendMessage(ChatColor.YELLOW + "/sr " + ChatColor.GRAY + "- Open Space Ring");
            player.sendMessage(ChatColor.YELLOW + "/talent " + ChatColor.GRAY + "- Open Talent Menu");
            player.sendMessage(ChatColor.YELLOW + "/task " + ChatColor.GRAY + "- Start Task");
            player.sendMessage(ChatColor.YELLOW + "/crate " + ChatColor.GRAY + "- Open Crate");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            player.sendMessage(ChatColor.GOLD + "IndieRPG v1.0.0");
            player.sendMessage(ChatColor.GRAY + "Independent RPG plugin");
            player.sendMessage(ChatColor.GRAY + "MythicMobs compatible: " +
                    (plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            player.sendMessage(ChatColor.GREEN + "IndieRPG config reloaded!");
            return true;
        }

        return false;
    }
}
