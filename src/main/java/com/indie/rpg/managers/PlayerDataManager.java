package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 每位玩家一份 YAML；保存飾品、成長、在線時間與現實時間。 */
public class PlayerDataManager {
    private final IndieRPG plugin; private final Map<UUID, PlayerData> cache = new HashMap<>(); private final File folder;
    public PlayerDataManager(IndieRPG plugin) { this.plugin = plugin; folder = new File(plugin.getDataFolder(), "playerdata"); if (!folder.exists()) folder.mkdirs(); }
    public PlayerData getPlayerData(UUID uuid) { if (!cache.containsKey(uuid)) cache.put(uuid, load(uuid)); return cache.get(uuid); }
    public PlayerData getPlayerData(Player player) { return getPlayerData(player.getUniqueId()); }
    private PlayerData load(UUID uuid) {
        PlayerData d = new PlayerData(uuid); File f = new File(folder, uuid + ".yml"); if (!f.exists()) return d;
        FileConfiguration c = YamlConfiguration.loadConfiguration(f); d.talentPoints=c.getInt("talent-points"); d.battlePassLevel=c.getInt("battlepass-level"); d.battlePassXP=c.getInt("battlepass-xp"); d.rank=c.getString("rank","default"); d.taskKills=c.getInt("task-kills"); d.taskActive=c.getBoolean("task-active"); d.gold=c.getInt("gold", plugin.getConfig().getInt("economy.starting-gold")); d.totalOnlineMinutes=c.getLong("online.total-minutes");
        if (c.isConfigurationSection("talents")) for (String k:c.getConfigurationSection("talents").getKeys(false)) d.talentLevels.put(k,c.getInt("talents."+k));
        if (c.isConfigurationSection("jewelry")) for (String k:c.getConfigurationSection("jewelry").getKeys(false)) { d.jewelryLevels.put(k,c.getLong("jewelry."+k+".level")); d.jewelryOnlineMinutes.put(k,c.getLong("jewelry."+k+".online-minutes")); d.jewelryRealSeconds.put(k,c.getLong("jewelry."+k+".real-seconds")); d.jewelryGrowthPoints.put(k,c.getDouble("jewelry."+k+".growth-points")); }
        return d;
    }
    public void saveData(UUID uuid) { PlayerData d=cache.get(uuid); if(d==null)return; FileConfiguration c=new YamlConfiguration(); c.set("talent-points",d.talentPoints); c.set("battlepass-level",d.battlePassLevel); c.set("battlepass-xp",d.battlePassXP); c.set("rank",d.rank); c.set("task-kills",d.taskKills); c.set("task-active",d.taskActive); c.set("gold",d.gold); c.set("online.total-minutes",d.totalOnlineMinutes); for(Map.Entry<String,Integer> e:d.talentLevels.entrySet())c.set("talents."+e.getKey(),e.getValue()); for(String id:d.jewelryLevels.keySet()){c.set("jewelry."+id+".level",d.jewelryLevels.get(id));c.set("jewelry."+id+".online-minutes",d.jewelryOnlineMinutes.get(id));c.set("jewelry."+id+".real-seconds",d.jewelryRealSeconds.get(id));c.set("jewelry."+id+".growth-points",d.jewelryGrowthPoints.get(id));} try{c.save(new File(folder,uuid+".yml"));}catch(IOException e){plugin.getLogger().warning("無法保存玩家資料："+uuid);}}
    public void saveAll(){for(UUID uuid:cache.keySet())saveData(uuid);}
    public static class PlayerData { public UUID uuid; public int talentPoints,battlePassLevel,battlePassXP,taskKills,gold; public String rank; public boolean taskActive; public long totalOnlineMinutes; public Map<String,Integer> talentLevels=new HashMap<>(); public Map<String,Long> jewelryLevels=new HashMap<>(),jewelryOnlineMinutes=new HashMap<>(),jewelryRealSeconds=new HashMap<>(); public Map<String,Double> jewelryGrowthPoints=new HashMap<>(); public PlayerData(UUID uuid){this.uuid=uuid;this.rank="default";} }
}
