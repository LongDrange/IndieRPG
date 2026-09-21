package com.indie.rpg.commands;

import com.indie.rpg.IndieRPG;
import com.indie.rpg.managers.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/** 中文玩家與管理員指令。 */
public class CommandHandler implements CommandExecutor {
    private final IndieRPG plugin; public CommandHandler(IndieRPG plugin){this.plugin=plugin;}
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){sender.sendMessage("只有玩家可以使用此指令。");return true;} Player p=(Player)sender;
        if(!command.getName().equalsIgnoreCase("indierpg")){ if(command.getName().equalsIgnoreCase("spacering")){plugin.getSpaceRingManager().openSpaceRing(p,"進階空間戒指");return true;} if(command.getName().equalsIgnoreCase("talent")){plugin.getTalentManager().openTalentMenu(p);return true;} if(command.getName().equalsIgnoreCase("task")){plugin.getTaskManager().startTask(p);return true;} if(command.getName().equalsIgnoreCase("crate")){plugin.getCrateManager().openCrate(p,"common");return true;} return false; }
        String prefix=ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("general.prefix","&d[IndieRPG] &r")); if(args.length==0){help(p);return true;}
        if(args[0].equalsIgnoreCase("reload")){if(!p.hasPermission("indierpg.admin")){p.sendMessage(ChatColor.RED+"你沒有權限。");return true;}plugin.reloadConfig();p.sendMessage(prefix+ChatColor.GREEN+"全部配置已重新載入。");return true;}
        if(args[0].equalsIgnoreCase("give")&&args.length>1){ItemStack item=plugin.getItemManager().getItem(args[1]);if(item==null){p.sendMessage(ChatColor.RED+"找不到物品："+args[1]);}else{p.getInventory().addItem(item);p.sendMessage(prefix+"&a已給予：&f"+args[1]);}return true;}
        if(args[0].equalsIgnoreCase("items")){List<String> ids=plugin.getItemManager().getItemIds();p.sendMessage(ChatColor.GOLD+"自定義物品：");for(String id:ids)p.sendMessage(ChatColor.YELLOW+"/rpg give "+id);return true;}
        if(args[0].equalsIgnoreCase("gold")){p.sendMessage(prefix+"&7金幣：&6"+plugin.getPlayerDataManager().getPlayerData(p).gold);return true;}
        if(args[0].equalsIgnoreCase("rank")){PlayerDataManager.PlayerData d=plugin.getPlayerDataManager().getPlayerData(p);p.sendMessage(prefix+"&7稱號："+plugin.getRankManager().getRankDisplay(d.rank));return true;}
        if(args[0].equalsIgnoreCase("bp")){plugin.getBattlePassManager().showProgress(p);return true;}
        if(args[0].equalsIgnoreCase("admin")){return admin(p,args);}
        p.sendMessage(ChatColor.RED+"未知指令，輸入 /rpg 查看幫助。");return true;
    }
    private boolean admin(Player p,String[] a){if(!p.hasPermission("indierpg.admin")){p.sendMessage(ChatColor.RED+"你沒有管理員權限。");return true;}if(a.length<2){p.sendMessage("/rpg admin reload|setlevel|addgrowth|resetdata");return true;}if(a[1].equalsIgnoreCase("reload")){plugin.reloadConfig();p.sendMessage(ChatColor.GREEN+"已重新載入。");return true;}if(a[1].equalsIgnoreCase("setlevel")&&a.length>=4){Player target=plugin.getServer().getPlayer(a[2]);if(target!=null)try{plugin.getPlayerDataManager().getPlayerData(target).jewelryLevels.put(a[3],a.length>4?Long.parseLong(a[4]):0L);p.sendMessage(ChatColor.GREEN+"已設定飾品等級。");}catch(NumberFormatException e){p.sendMessage(ChatColor.RED+"等級必須是數字。");}return true;}if(a[1].equalsIgnoreCase("addgrowth")&&a.length>=4){Player target=plugin.getServer().getPlayer(a[2]);if(target!=null)try{PlayerDataManager.PlayerData d=plugin.getPlayerDataManager().getPlayerData(target);d.jewelryGrowthPoints.put(a[3],(d.jewelryGrowthPoints.containsKey(a[3])?d.jewelryGrowthPoints.get(a[3]):0D)+(a.length>4?Double.parseDouble(a[4]):1D));p.sendMessage(ChatColor.GREEN+"已增加成長點數。");}catch(NumberFormatException e){p.sendMessage(ChatColor.RED+"數值格式錯誤。");}return true;}if(a[1].equalsIgnoreCase("resetdata")&&a.length>=3){Player target=plugin.getServer().getPlayer(a[2]);if(target!=null){plugin.getPlayerDataManager().getPlayerData(target).jewelryLevels.clear();plugin.getPlayerDataManager().getPlayerData(target).jewelryGrowthPoints.clear();p.sendMessage(ChatColor.GREEN+"已重置飾品資料。");}return true;}return true;}
    private void help(Player p){p.sendMessage(ChatColor.GOLD+"=== IndieRPG 中文指令 ===");p.sendMessage("/rpg info、reload、give <ID>、items、gold、rank、bp");p.sendMessage("/rpg admin reload|setlevel|addgrowth|resetdata");p.sendMessage("/sr /talent /task /crate");}
}
