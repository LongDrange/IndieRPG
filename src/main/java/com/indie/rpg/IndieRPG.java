package com.indie.rpg;

import com.indie.rpg.commands.CommandHandler;
import com.indie.rpg.listeners.InventoryListener;
import com.indie.rpg.listeners.PlayerListener;
import com.indie.rpg.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class IndieRPG extends JavaPlugin {

    private static IndieRPG instance;
    private PlayerDataManager playerDataManager;
    private SpaceRingManager spaceRingManager;
    private SoulStorageManager soulStorageManager;
    private TalentManager talentManager;
    private TaskManager taskManager;
    private CrateManager crateManager;
    private RankManager rankManager;
    private BattlePassManager battlePassManager;
    private ItemManager itemManager;
    private DailyRewardManager dailyRewardManager;
    private ShopManager shopManager;
    private MonsterCardManager monsterCardManager;
    private GuideManager guideManager;
    private JewelryManager jewelryManager;
    private ExchangeManager exchangeManager;
    private SoulBeadManager soulBeadManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        playerDataManager = new PlayerDataManager(this);
        spaceRingManager = new SpaceRingManager(this);
        soulStorageManager = new SoulStorageManager(this);
        talentManager = new TalentManager(this);
        taskManager = new TaskManager(this);
        crateManager = new CrateManager(this);
        rankManager = new RankManager(this);
        battlePassManager = new BattlePassManager(this);
        itemManager = new ItemManager(this);
        dailyRewardManager = new DailyRewardManager(this);
        shopManager = new ShopManager(this);
        monsterCardManager = new MonsterCardManager(this);
        guideManager = new GuideManager(this);
        jewelryManager = new JewelryManager(this);
        exchangeManager = new ExchangeManager(this);
        soulBeadManager = new SoulBeadManager(this);

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("indierpg").setExecutor(new CommandHandler(this));
        getCommand("spacering").setExecutor(new CommandHandler(this));
        getCommand("talent").setExecutor(new CommandHandler(this));
        getCommand("task").setExecutor(new CommandHandler(this));
        getCommand("crate").setExecutor(new CommandHandler(this));

        getLogger().info("IndieRPG v4.0.0 enabled! (Full RPG Suite)");
        getLogger().info("MythicMobs detected: " + (getServer().getPluginManager().getPlugin("MythicMobs") != null));
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) playerDataManager.saveAll();
        getLogger().info("IndieRPG disabled!");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (talentManager != null) talentManager.loadTrees();
        if (crateManager != null) crateManager.loadRewards();
        if (rankManager != null) rankManager.loadRanks();
        if (itemManager != null) itemManager.loadItems();
        if (shopManager != null) shopManager.loadShop();
        if (monsterCardManager != null) monsterCardManager.loadCards();
        if (guideManager != null) guideManager.loadEntries();
        if (jewelryManager != null) jewelryManager.loadSets();
        if (soulBeadManager != null) soulBeadManager.loadBeads();
        getLogger().info("IndieRPG config reloaded!");
    }

    public static IndieRPG getInstance() { return instance; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public SpaceRingManager getSpaceRingManager() { return spaceRingManager; }
    public SoulStorageManager getSoulStorageManager() { return soulStorageManager; }
    public TalentManager getTalentManager() { return talentManager; }
    public TaskManager getTaskManager() { return taskManager; }
    public CrateManager getCrateManager() { return crateManager; }
    public RankManager getRankManager() { return rankManager; }
    public BattlePassManager getBattlePassManager() { return battlePassManager; }
    public ItemManager getItemManager() { return itemManager; }
    public DailyRewardManager getDailyRewardManager() { return dailyRewardManager; }
    public ShopManager getShopManager() { return shopManager; }
    public MonsterCardManager getMonsterCardManager() { return monsterCardManager; }
    public GuideManager getGuideManager() { return guideManager; }
    public JewelryManager getJewelryManager() { return jewelryManager; }
    public ExchangeManager getExchangeManager() { return exchangeManager; }
    public SoulBeadManager getSoulBeadManager() { return soulBeadManager; }
}
