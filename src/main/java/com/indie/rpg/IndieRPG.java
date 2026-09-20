package com.indie.rpg;

import com.indie.rpg.commands.CommandHandler;
import com.indie.rpg.listeners.InventoryListener;
import com.indie.rpg.listeners.PlayerListener;
import com.indie.rpg.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class IndieRPG extends JavaPlugin {

    private static IndieRPG instance;
    private SpaceRingManager spaceRingManager;
    private TalentManager talentManager;
    private TaskManager taskManager;
    private CrateManager crateManager;
    private RankManager rankManager;
    private BattlePassManager battlePassManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        playerDataManager = new PlayerDataManager(this);
        spaceRingManager = new SpaceRingManager(this);
        talentManager = new TalentManager(this);
        taskManager = new TaskManager(this);
        crateManager = new CrateManager(this);
        rankManager = new RankManager(this);
        battlePassManager = new BattlePassManager(this);

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("indierpg").setExecutor(new CommandHandler(this));
        getCommand("spacering").setExecutor(new CommandHandler(this));
        getCommand("talent").setExecutor(new CommandHandler(this));
        getCommand("task").setExecutor(new CommandHandler(this));
        getCommand("crate").setExecutor(new CommandHandler(this));

        getLogger().info("IndieRPG v1.0.0 enabled!");
        getLogger().info("Compatible with MythicMobs: " + (getServer().getPluginManager().getPlugin("MythicMobs") != null));
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) playerDataManager.saveAll();
        getLogger().info("IndieRPG disabled!");
    }

    public static IndieRPG getInstance() { return instance; }
    public SpaceRingManager getSpaceRingManager() { return spaceRingManager; }
    public TalentManager getTalentManager() { return talentManager; }
    public TaskManager getTaskManager() { return taskManager; }
    public CrateManager getCrateManager() { return crateManager; }
    public RankManager getRankManager() { return rankManager; }
    public BattlePassManager getBattlePassManager() { return battlePassManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
}
