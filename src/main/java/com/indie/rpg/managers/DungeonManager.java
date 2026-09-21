package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DungeonManager {
    private final IndieRPG plugin;
    private final Map<String, DungeonDefinition> definitions = new HashMap<String, DungeonDefinition>();
    private final Map<UUID, ActiveInstance> activeByPlayer = new HashMap<UUID, ActiveInstance>();
    private final Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
    private final Map<UUID, BukkitTask> waveTasks = new HashMap<UUID, BukkitTask>();

    public DungeonManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadDefinitions();
    }

    public void loadDefinitions() {
        definitions.clear();
        ConfigurationSection root = plugin.getConfigManager().get("dungeons")
                .getConfigurationSection("dungeons");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            DungeonDefinition def = new DungeonDefinition();
            def.id = id;
            def.name = sec.getString("name", id);
            def.templateWorld = sec.getString("template-world", "dungeon_" + id);
            def.minLevel = sec.getInt("min-level", 1);
            def.maxPlayers = sec.getInt("max-players", 4);
            def.timeLimit = sec.getInt("time-limit", 1800);
            def.cooldown = sec.getInt("cooldown", 3600);
            def.mobWaves = new ArrayList<List<String>>();
            for (Map<?, ?> wave : sec.getMapList("waves")) {
                List<String> mobs = new ArrayList<String>();
                Object mobsObj = wave.get("mobs");
                if (mobsObj instanceof List) {
                    for (Object o : (List<?>) mobsObj) mobs.add(String.valueOf(o));
                }
                def.mobWaves.add(mobs);
            }
            def.rewardGold = sec.getInt("rewards.gold", 0);
            def.rewardBpExp = sec.getInt("rewards.battle-pass-exp", 0);
            def.rewardItem = sec.getString("rewards.item", "");
            definitions.put(id, def);
        }
        plugin.getLogger().info("已載入 " + definitions.size() + " 個副本定義");
    }

    public DungeonDefinition getDefinition(String id) { return definitions.get(id); }
    public Map<String, DungeonDefinition> getAll() { return Collections.unmodifiableMap(definitions); }

    public boolean enter(Player player, String dungeonId) {
        DungeonDefinition def = definitions.get(dungeonId);
        if (def == null) {
            player.sendMessage("§c副本不存在。");
            return false;
        }
        Long cd = cooldowns.get(player.getUniqueId());
        if (cd != null && System.currentTimeMillis() < cd) {
            long remain = (cd - System.currentTimeMillis()) / 1000L;
            player.sendMessage("§c冷卻中，剩餘 " + remain + " 秒。");
            return false;
        }
        PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.jobLevel < def.minLevel) {
            player.sendMessage("§c需要職業等級 " + def.minLevel);
            return false;
        }

        ActiveInstance instance;
        try {
            instance = createInstance(def);
        } catch (Exception e) {
            player.sendMessage("§c無法建立副本世界：" + e.getMessage());
            plugin.getLogger().warning("建立副本失敗：" + e.getMessage());
            return false;
        }
        if (instance == null) {
            player.sendMessage("§c找不到模板世界：" + def.templateWorld);
            return false;
        }
        player.teleport(instance.world.getSpawnLocation());
        activeByPlayer.put(player.getUniqueId(), instance);
        instance.members.add(player.getUniqueId());
        player.sendMessage("§a已進入副本：" + def.name);
        startWaves(instance, player);
        return true;
    }

    public void leave(Player player) {
        ActiveInstance inst = activeByPlayer.remove(player.getUniqueId());
        if (inst == null) return;
        inst.members.remove(player.getUniqueId());
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + inst.def.cooldown * 1000L);
        if (inst.members.isEmpty()) destroyInstance(inst);
    }

    private ActiveInstance createInstance(DungeonDefinition def) throws IOException {
        String worldName = def.templateWorld + "_inst_" + UUID.randomUUID().toString().substring(0, 8);
        File templatesRoot = new File(plugin.getDataFolder(), "dungeon-templates");
        File source = new File(templatesRoot, def.templateWorld);
        File target = new File(Bukkit.getWorldContainer(), worldName);

        if (!source.exists()) return null;
        copyFolder(source.toPath(), target.toPath());

        World world = new WorldCreator(worldName).createWorld();
        if (world == null) return null;
        ActiveInstance instance = new ActiveInstance(def, world);
        if (def.timeLimit > 0) {
            final ActiveInstance fInst = instance;
            instance.timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run() { timeout(fInst); }
            }, def.timeLimit * 20L);
        }
        return instance;
    }

    private void startWaves(final ActiveInstance inst, final Player owner) {
        final List<List<String>> waves = inst.def.mobWaves;
        if (waves.isEmpty()) return;
        final int[] index = new int[]{0};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override public void run() {
                if (index[0] >= waves.size()) {
                    for (UUID id : inst.members) {
                        Player p = Bukkit.getPlayer(id);
                        if (p != null) p.sendMessage("§6[副本] §e所有波次已清除！");
                    }
                    giveRewards(inst);
                    BukkitTask t = waveTasks.remove(owner.getUniqueId());
                    if (t != null) t.cancel();
                    return;
                }

                inst.spawnedMobs.removeIf(uuid -> {
                    Entity e = Bukkit.getEntity(uuid);
                    return e == null || e.isDead();
                });
                if (!inst.spawnedMobs.isEmpty()) return;

                List<String> wave = waves.get(index[0]);
                for (String mobId : wave) {
                    Entity e = spawnMythicMob(mobId, inst.world.getSpawnLocation());
                    if (e != null) inst.spawnedMobs.add(e.getUniqueId());
                }
                index[0]++;
                for (UUID id : inst.members) {
                    Player p = Bukkit.getPlayer(id);
                    if (p != null) p.sendMessage("§6[副本] §e第 " + index[0] + " 波來襲！");
                }
            }
        }, 40L, 40L);
        waveTasks.put(owner.getUniqueId(), task);
    }

    private void giveRewards(ActiveInstance inst) {
        for (UUID id : inst.members) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) continue;
            PlayerDataManager.PlayerData d = plugin.getPlayerDataManager().getPlayerData(p);
            if (inst.def.rewardGold > 0) d.gold += inst.def.rewardGold;
            if (inst.def.rewardBpExp > 0) plugin.getBattlePassManager().addXP(p, inst.def.rewardBpExp);
            if (inst.def.rewardItem != null && !inst.def.rewardItem.isEmpty()) {
                org.bukkit.inventory.ItemStack it = plugin.getItemManager().getItem(inst.def.rewardItem);
                if (it != null) p.getInventory().addItem(it);
            }
            p.sendMessage("§6[副本] §a完成！§7+ " + inst.def.rewardGold + " 金幣");
        }
    }

    private Entity spawnMythicMob(String mythicId, Location loc) {
        try {
            Class<?> bukkit = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
            Object inst = bukkit.getMethod("inst").invoke(null);
            Object mobManager = inst.getClass().getMethod("getMobManager").invoke(inst);
            Object mythicMob = mobManager.getClass()
                    .getMethod("getMythicMob", String.class).invoke(mobManager, mythicId);
            if (mythicMob == null) return null;
            Method spawn = mythicMob.getClass().getMethod("spawn", Location.class, int.class);
            Object activeMob = spawn.invoke(mythicMob, loc, 1);
            Object entity = activeMob.getClass().getMethod("getEntity").invoke(activeMob);
            return entity instanceof Entity ? (Entity) entity : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void timeout(ActiveInstance inst) {
        for (UUID id : new ArrayList<UUID>(inst.members)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.sendMessage("§c副本時間到，已傳出。");
        }
        destroyInstance(inst);
    }

    private void destroyInstance(ActiveInstance inst) {
        if (inst.timeoutTask != null) inst.timeoutTask.cancel();
        for (UUID id : new ArrayList<UUID>(inst.members)) {
            BukkitTask t = waveTasks.remove(id);
            if (t != null) t.cancel();
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            activeByPlayer.remove(id);
        }
        String worldName = inst.world.getName();
        Bukkit.unloadWorld(inst.world, false);
        try {
            deleteFolder(new File(Bukkit.getWorldContainer(), worldName).toPath());
        } catch (IOException ignored) { }
    }

    public void onMobKill(Player player, String mobId) {
        ActiveInstance inst = activeByPlayer.get(player.getUniqueId());
        if (inst != null) inst.killed++;
    }

    private static void copyFolder(Path src, Path dst) throws IOException {
        if (!Files.exists(src)) return;
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, dst.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteFolder(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static class DungeonDefinition {
        public String id;
        public String name;
        public String templateWorld;
        public int minLevel;
        public int maxPlayers;
        public int timeLimit;
        public int cooldown;
        public List<List<String>> mobWaves = new ArrayList<List<String>>();
        public int rewardGold = 0;
        public int rewardBpExp = 0;
        public String rewardItem = "";
    }

    public static class ActiveInstance {
        public final DungeonDefinition def;
        public final World world;
        public final List<UUID> members = new ArrayList<UUID>();
        public final Set<UUID> spawnedMobs = new HashSet<UUID>();
        public int killed = 0;
        public BukkitTask timeoutTask;

        public ActiveInstance(DungeonDefinition def, World world) {
            this.def = def;
            this.world = world;
        }
    }
}