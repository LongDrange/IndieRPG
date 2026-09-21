package com.indie.rpg.commands;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.indie.rpg.managers.JobManager;
import java.util.Map;

import java.util.List;

/** LDAPI 主命令處理器 */
public class CommandHandler implements CommandExecutor {
    private final IndieRPG plugin;

    public CommandHandler(IndieRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此指令。");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "ldapi":
                return handleLDAPI(player, args);
            case "spacering":
                plugin.getSpaceRingManager().openSpaceRing(player, "進階空間戒指");
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
            case "job":
                return handleJob(player, args);
            case "party":
                return handleParty(player, args);
            case "guild":
                return handleGuild(player, args);
            default:
                return false;
        }
    }
    private boolean handleJob(Player player, String[] args) {
        if (args.length < 1) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            if (data.job == null || data.job.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "你還沒有職業，可選：");
                for (String id : plugin.getJobManager().getAll().keySet()) {
                    player.sendMessage(ChatColor.GRAY + "  /job " + id);
                }
            } else {
                JobManager.JobDefinition def = plugin.getJobManager().get(data.job);
                long need = plugin.getJobManager().expNeeded(def, data.jobLevel);
                player.sendMessage(ChatColor.GOLD + "職業：" + def.displayName
                        + ChatColor.GRAY + "  等級 " + data.jobLevel
                        + "  經驗 " + data.jobExp + "/" + need);
            }
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("list")) {
            for (Map.Entry<String, JobManager.JobDefinition> e
                    : plugin.getJobManager().getAll().entrySet()) {
                player.sendMessage(ChatColor.YELLOW + e.getKey()
                        + ChatColor.GRAY + " - " + e.getValue().displayName);
            }
            return true;
        }
        plugin.getJobManager().setJob(player, sub);
        return true;
    }
    private boolean handleParty(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§e/party create|invite <player>|accept|leave");
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("create")) {
            plugin.getPartyManager().create(player);
        } else if (sub.equals("invite") && args.length >= 2) {
            Player t = plugin.getServer().getPlayer(args[1]);
            if (t != null) plugin.getPartyManager().invite(player, t);
        } else if (sub.equals("accept")) {
            plugin.getPartyManager().accept(player);
        } else if (sub.equals("leave")) {
            plugin.getPartyManager().leave(player);
        }
        return true;
    }

    private boolean handleGuild(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§e/guild create <name>|invite <player>|accept|leave|info");
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("create") && args.length >= 2) {
            plugin.getGuildManager().createGuild(player, args[1]);
        } else if (sub.equals("invite") && args.length >= 2) {
            Player t = plugin.getServer().getPlayer(args[1]);
            if (t != null) plugin.getGuildManager().invite(player, t);
        } else if (sub.equals("accept")) {
            plugin.getGuildManager().accept(player);
        } else if (sub.equals("leave")) {
            plugin.getGuildManager().leave(player);
        } else if (sub.equals("info")) {
            com.indie.rpg.managers.GuildManager.Guild g = plugin.getGuildManager().getGuild(player);
            if (g == null) {
                player.sendMessage("§c你沒有公會。");
            } else {
                player.sendMessage("§6公會：" + g.getName() + " §7等級 " + g.getLevel() + " §7成員 " + g.getMembers().size());
            }
        }
        return true;
    }

    private boolean handleLDAPI(Player player, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("general.prefix", "&d[LDAPI] &r"));

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "=== LDAPI 1.0.0 ===");
            player.sendMessage(ChatColor.YELLOW + "/ld info" + ChatColor.GRAY + " - 查看插件資訊");
            player.sendMessage(ChatColor.YELLOW + "/ld reload" + ChatColor.GRAY + " - 重新載入配置");
            player.sendMessage(ChatColor.YELLOW + "/ld give <item-id>" + ChatColor.GRAY + " - 給予物品");
            player.sendMessage(ChatColor.YELLOW + "/ld items" + ChatColor.GRAY + " - 列出所有物品");
            player.sendMessage(ChatColor.YELLOW + "/ld gold" + ChatColor.GRAY + " - 檢查金幣");
            player.sendMessage(ChatColor.YELLOW + "/ld rank" + ChatColor.GRAY + " - 查看稱號");
            player.sendMessage(ChatColor.YELLOW + "/ld bp" + ChatColor.GRAY + " - 戰令進度");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            player.sendMessage(prefix + "版本：1.0.0");
            player.sendMessage(ChatColor.GRAY + "核心：Minecraft 1.12.2 Paper");
            player.sendMessage(ChatColor.GRAY + "MythicMobs: " + (plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null ? "已偵測" : "未偵測到"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("ldapi.admin")) {
                player.sendMessage(ChatColor.RED + "你沒有 LDAPI 管理員權限。");
                return true;
            }
            plugin.reloadConfig();
            player.sendMessage(prefix + ChatColor.GREEN + "配置已重新載入。");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "用法：/ld give <item-id>");
                return true;
            }
            ItemStack item = plugin.getItemManager().getItem(args[1]);
            if (item == null) {
                player.sendMessage(ChatColor.RED + "未知物品：" + args[1]);
                return true;
            }
            player.getInventory().addItem(item);
            player.sendMessage(prefix + ChatColor.GRAY + "已給予：" + args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("items")) {
            List<String> ids = plugin.getItemManager().getItemIds();
            player.sendMessage(ChatColor.GOLD + "自定義物品（" + ids.size() + "）：");
            for (String id : ids) {
                player.sendMessage(ChatColor.YELLOW + "  /ld give " + id);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("gold")) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            player.sendMessage(prefix + ChatColor.GRAY + "金幣：" + ChatColor.GOLD + data.gold);
            return true;
        }

        if (args[0].equalsIgnoreCase("rank")) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            player.sendMessage(prefix + ChatColor.GRAY + "稱號：" + plugin.getRankManager().getRankDisplay(data.rank));
            return true;
        }

        if (args[0].equalsIgnoreCase("bp")) {
            plugin.getBattlePassManager().showProgress(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            return adminCommands(player, args);
        }

        player.sendMessage(ChatColor.RED + "未知指令，輸入 /ld 查看幫助。");
        return true;
    }

    private boolean adminCommands(Player player, String[] args) {
        if (!player.hasPermission("ldapi.admin")) {
            player.sendMessage(ChatColor.RED + "你沒有 LDAPI 管理員權限。");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "/ld admin reload|setlevel|addgrowth|resetdata");
            return true;
        }

        if (args[1].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            player.sendMessage(ChatColor.GREEN + "LDAPI 已重新載入。");
            return true;
        }

        if (args[1].equalsIgnoreCase("setlevel") && args.length >= 5) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "找不到在線玩家：" + args[2]);
                return true;
            }
            try {
                long value = Long.parseLong(args[4]);
                plugin.getPlayerDataManager().getPlayerData(target).jewelryLevels.put(args[3], value);
                player.sendMessage(ChatColor.GREEN + "已設定飾品等級。");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "等級必須是數字。");
            }
            return true;
        }

        if (args[1].equalsIgnoreCase("addgrowth") && args.length >= 5) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "找不到在線玩家：" + args[2]);
                return true;
            }
            try {
                double value = Double.parseDouble(args[4]);
                PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
                data.jewelryGrowthPoints.put(args[3], data.jewelryGrowthPoints.containsKey(args[3]) ? data.jewelryGrowthPoints.get(args[3]) + value : value);
                player.sendMessage(ChatColor.GREEN + "已增加成長點數。");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "數值格式錯誤。");
            }
            return true;
        }

        if (args[1].equalsIgnoreCase("resetdata") && args.length >= 3) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target != null) {
                PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
                data.jewelryLevels.clear();
                data.jewelryGrowthPoints.clear();
                player.sendMessage(ChatColor.GREEN + "已重置玩家飾品資料。");
            }
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "用法：/ld admin reload|setlevel|addgrowth|resetdata");
        return true;
    }
}
