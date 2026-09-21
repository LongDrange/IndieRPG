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

/** LDAPI 主指令；/ldapi、/ld、/led 都可以使用。 */
public class CommandHandler implements CommandExecutor {
    private final IndieRPG plugin;
    public CommandHandler(IndieRPG plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("只有玩家可以使用此指令。"); return true; }
        Player player = (Player) sender;
        String name = command.getName().toLowerCase();
        if (name.equals("spacering")) { plugin.getSpaceRingManager().openSpaceRing(player, "進階空間戒指"); return true; }
        if (name.equals("talent")) { plugin.getTalentManager().openTalentMenu(player); return true; }
        if (name.equals("task")) { plugin.getTaskManager().startTask(player); return true; }
        if (name.equals("crate")) { plugin.getCrateManager().openCrate(player, "common"); return true; }
        if (!name.equals("ldapi")) return false;

        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("general.prefix", "&d[LDAPI] &r"));
        if (args.length == 0) { help(player); return true; }
        if (args[0].equalsIgnoreCase("info")) {
            player.sendMessage(prefix + "&f版本：&e1.0.0");
            player.sendMessage(prefix + "&f指令前綴：&e/ld &7（別名：/ldapi、/led）");
            player.sendMessage(prefix + "&fMythicMobs：" + (plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null ? ChatColor.GREEN + "已偵測" : ChatColor.RED + "未偵測到"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("ldapi.admin")) { player.sendMessage(ChatColor.RED + "你沒有 LDAPI 管理員權限。"); return true; }
            plugin.reloadConfig(); player.sendMessage(prefix + ChatColor.GREEN + "全部配置已重新載入。"); return true;
        }
        if (args[0].equalsIgnoreCase("give") && args.length > 1) {
            ItemStack item = plugin.getItemManager().getItem(args[1]);
            if (item == null) player.sendMessage(ChatColor.RED + "找不到物品：" + args[1]);
            else { player.getInventory().addItem(item); player.sendMessage(prefix + "&a已給予：&f" + args[1]); }
            return true;
        }
        if (args[0].equalsIgnoreCase("items")) {
            List<String> ids = plugin.getItemManager().getItemIds(); player.sendMessage(ChatColor.GOLD + "LDAPI 自定義物品：");
            for (String id : ids) player.sendMessage(ChatColor.YELLOW + "/ld give " + id); return true;
        }
        if (args[0].equalsIgnoreCase("gold")) { player.sendMessage(prefix + "&7金幣：&6" + plugin.getPlayerDataManager().getPlayerData(player).gold); return true; }
        if (args[0].equalsIgnoreCase("rank")) { PlayerDataManager.PlayerData d = plugin.getPlayerDataManager().getPlayerData(player); player.sendMessage(prefix + "&7稱號：" + plugin.getRankManager().getRankDisplay(d.rank)); return true; }
        if (args[0].equalsIgnoreCase("bp")) { plugin.getBattlePassManager().showProgress(player); return true; }
        if (args[0].equalsIgnoreCase("admin")) return admin(player, args);
        player.sendMessage(ChatColor.RED + "未知指令，輸入 /ld 查看幫助。"); return true;
    }

    private boolean admin(Player player, String[] args) {
        if (!player.hasPermission("ldapi.admin")) { player.sendMessage(ChatColor.RED + "你沒有 LDAPI 管理員權限。"); return true; }
        if (args.length < 2) { player.sendMessage(ChatColor.YELLOW + "/ld admin reload|setlevel|addgrowth|resetdata"); return true; }
        if (args[1].equalsIgnoreCase("reload")) { plugin.reloadConfig(); player.sendMessage(ChatColor.GREEN + "LDAPI 已重新載入。"); return true; }
        if (args[1].equalsIgnoreCase("setlevel") && args.length >= 5) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) { player.sendMessage(ChatColor.RED + "找不到在線玩家。"); return true; }
            try { plugin.getPlayerDataManager().getPlayerData(target).jewelryLevels.put(args[3], Long.parseLong(args[4])); player.sendMessage(ChatColor.GREEN + "已設定飾品等級。"); }
            catch (NumberFormatException e) { player.sendMessage(ChatColor.RED + "等級必須是數字。"); } return true;
        }
        if (args[1].equalsIgnoreCase("addgrowth") && args.length >= 5) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) { player.sendMessage(ChatColor.RED + "找不到在線玩家。"); return true; }
            try { PlayerDataManager.PlayerData d = plugin.getPlayerDataManager().getPlayerData(target); d.jewelryGrowthPoints.put(args[3], d.jewelryGrowthPoints.containsKey(args[3]) ? d.jewelryGrowthPoints.get(args[3]) + Double.parseDouble(args[4]) : Double.parseDouble(args[4])); player.sendMessage(ChatColor.GREEN + "已增加成長點數。"); }
            catch (NumberFormatException e) { player.sendMessage(ChatColor.RED + "數值格式錯誤。"); } return true;
        }
        if (args[1].equalsIgnoreCase("resetdata") && args.length >= 3) { Player target = plugin.getServer().getPlayer(args[2]); if (target != null) { PlayerDataManager.PlayerData d = plugin.getPlayerDataManager().getPlayerData(target); d.jewelryLevels.clear(); d.jewelryGrowthPoints.clear(); player.sendMessage(ChatColor.GREEN + "已重置飾品資料。"); } return true; }
        player.sendMessage(ChatColor.YELLOW + "用法：/ld admin reload|setlevel|addgrowth|resetdata"); return true;
    }

    private void help(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== LDAPI 1.0.0 指令 ===");
        player.sendMessage(ChatColor.YELLOW + "/ld info &7- 查看 LDAPI 資訊");
        player.sendMessage(ChatColor.YELLOW + "/ld reload &7- 重新載入配置");
        player.sendMessage(ChatColor.YELLOW + "/ld give <物品ID> &7- 給予物品");
        player.sendMessage(ChatColor.YELLOW + "/ld items &7- 列出物品");
        player.sendMessage(ChatColor.YELLOW + "/ld admin ... &7- 管理員功能");
        player.sendMessage(ChatColor.YELLOW + "/ldsr /ldtalent /ldtask /ldcrate");
    }
}
