package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager {
    private final IndieRPG plugin;
    private final Map<UUID, ActivePet> active = new HashMap<UUID, ActivePet>();
    private final Map<String, PetDefinition> defs = new HashMap<String, PetDefinition>();

    public PetManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadDefinitions();
        startFollowTask();
    }

    public void loadDefinitions() {
        defs.clear();
        ConfigurationSection root = plugin.getConfigManager().get("pets")
                .getConfigurationSection("pets");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            PetDefinition def = new PetDefinition();
            def.id = id.toLowerCase();
            def.name = sec.getString("name", id);
            def.mythicId = sec.getString("mythic-id", "");
            def.maxLevel = sec.getInt("max-level", 50);
            ConfigurationSection attrs = sec.getConfigurationSection("bonus-per-level");
            if (attrs != null) {
                for (String k : attrs.getKeys(false)) {
                    def.attributesPerLevel.put(k.toLowerCase(), attrs.getDouble(k));
                }
            }
            defs.put(def.id, def);
        }
        plugin.getLogger().info("已載入 " + defs.size() + " 個寵物定義");
    }

    public Map<String, PetDefinition> getAll() {
        return Collections.unmodifiableMap(defs);
    }

    public Map<String, Double> calcAttributes(Player player) {
        Map<String, Double> result = new HashMap<String, Double>();
        ActivePet pet = active.get(player.getUniqueId());
        if (pet == null) return result;
        for (Map.Entry<String, Double> e : pet.def.attributesPerLevel.entrySet()) {
            result.put(e.getKey(), e.getValue() * pet.level);
        }
        return result;
    }

    public boolean summon(Player player, String petId) {
        PetDefinition def = defs.get(petId.toLowerCase());
        if (def == null) {
            player.sendMessage("§c未知寵物。");
            return false;
        }
        dismiss(player);
        Object entity = spawnMythicMob(def.mythicId, player.getLocation());
        if (entity == null) {
            player.sendMessage("§c召喚失敗（MythicMobs 未載入或 ID 錯誤）。");
            return false;
        }
        ActivePet pet = new ActivePet(def, entity);
        active.put(player.getUniqueId(), pet);
        if (plugin.getAttributeEngine() != null) {
            plugin.getAttributeEngine().recalculate(player);
        }
        player.sendMessage("§a已召喚 " + def.name);
        return true;
    }

    public void dismiss(Player player) {
        ActivePet pet = active.remove(player.getUniqueId());
        if (pet == null) return;
        try {
            pet.entity.getClass().getMethod("remove").invoke(pet.entity);
        } catch (Exception ignored) { }
        if (plugin.getAttributeEngine() != null) {
            plugin.getAttributeEngine().recalculate(player);
        }
    }

    private void startFollowTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override public void run() {
                for (Map.Entry<UUID, ActivePet> entry : active.entrySet()) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p == null) continue;
                    ActivePet pet = entry.getValue();
                    try {
                        Method teleport = pet.entity.getClass().getMethod("teleport", Location.class);
                        teleport.invoke(pet.entity, p.getLocation());
                    } catch (Exception ignored) { }
                }
            }
        }, 20L, 20L);
    }

    private Object spawnMythicMob(String mythicId, Location loc) {
        try {
            Class<?> bukkit = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
            Object inst = bukkit.getMethod("inst").invoke(null);
            Object mobManager = inst.getClass().getMethod("getMobManager").invoke(inst);
            Object mythicMob = mobManager.getClass()
                    .getMethod("getMythicMob", String.class).invoke(mobManager, mythicId);
            if (mythicMob == null) return null;
            Method spawn = mythicMob.getClass().getMethod("spawn", Location.class, int.class);
            Object activeMob = spawn.invoke(mythicMob, loc, 1);
            return activeMob.getClass().getMethod("getEntity").invoke(activeMob);
        } catch (Exception e) {
            return null;
        }
    }

    public static class PetDefinition {
        public String id;
        public String name;
        public String mythicId;
        public int maxLevel = 50;
        public final Map<String, Double> attributesPerLevel = new HashMap<String, Double>();
    }

    public static class ActivePet {
        public final PetDefinition def;
        public final Object entity;
        public int level = 1;
        public ActivePet(PetDefinition def, Object entity) {
            this.def = def;
            this.entity = entity;
        }
    }
}