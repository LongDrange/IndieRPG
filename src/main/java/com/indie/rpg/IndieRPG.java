package com.indie.rpg;

import com.indie.rpg.commands.CommandHandler;
import com.indie.rpg.integrations.MythicMobsHook;
import com.indie.rpg.listeners.InventoryListener;
import com.indie.rpg.listeners.PlayerListener;
import com.indie.rpg.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class IndieRPG extends JavaPlugin {
    private static IndieRPG instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private ItemManager itemManager;
    private AttributeManager attributeManager;
    private ShopManager shopManager;
    private DailyRewardManager dailyRewardManager;
    private JewelryManager jewelryManager;
    private JewelryGrowthManager jewelryGrowthManager;
    private GrowthManager growthManager;
    private SpaceRingManager spaceRingManager;
    private SoulStorageManager soulStorageManager;
    private TalentManager talentManager;
    private TaskManager taskManager;
    private CrateManager crateManager;
    private RankManager rankManager;
    private BattlePassManager battlePassManager;
    private MonsterCardManager monsterCardManager;
    private GuideManager guideManager;
    private ExchangeManager exchangeManager;
    private SoulBeadManager soulBeadManager;
    private JobManager jobManager;
    private AttributeEngine attributeEngine;
    private MythicMobsHook mythicMobsHook;

    @Override public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        itemManager = new ItemManager(this);
        attributeManager = new AttributeManager(this);
        shopManager = new ShopManager(this);
        dailyRewardManager = new DailyRewardManager(this);
        jewelryGrowthManager = new JewelryGrowthManager(this);
        growthManager = new GrowthManager(this);
        jewelryManager = new JewelryManager(this);
        spaceRingManager = new SpaceRingManager(this);
        soulStorageManager = new SoulStorageManager(this);
        talentManager = new TalentManager(this);
        taskManager = new TaskManager(this);
        crateManager = new CrateManager(this);
        rankManager = new RankManager(this);
        battlePassManager = new BattlePassManager(this);
        monsterCardManager = new MonsterCardManager(this);
        guideManager = new GuideManager(this);
        exchangeManager = new ExchangeManager(this);
        soulBeadManager = new SoulBeadManager(this);
        jobManager = new JobManager(this);
        attributeEngine = new AttributeEngine(this);
        mythicMobsHook = new MythicMobsHook(this);
        mythicMobsHook.enable();

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        CommandHandler handler = new CommandHandler(this);
        for (String command : new String[]{"ldapi", "spacering", "talent", "task", "crate", "job"})
            if (getCommand(command) != null) getCommand(command).setExecutor(handler);
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                    playerDataManager.addOnlineMinute(player.getUniqueId());
                    growthManager.addRealTime(player, "account", 60L);
                }
            }
        }, 1200L, 1200L);
        getLogger().info("LDAPI 1.0.0 已啟用（Minecraft 1.12.2 Paper）");
    }

    @Override public void onDisable() { if (playerDataManager != null) playerDataManager.saveAll(); }
    @Override public void reloadConfig() {
        super.reloadConfig();
        if (configManager != null) configManager.reloadAll();
        if (itemManager != null) itemManager.loadItems();
        if (attributeManager != null) attributeManager.loadAttributes();
        if (shopManager != null) shopManager.loadShop();
        if (dailyRewardManager != null) dailyRewardManager.loadRewards();
        if (jewelryGrowthManager != null) jewelryGrowthManager.reload();
        if (jewelryManager != null) jewelryManager.loadSets();
        if (talentManager != null) talentManager.loadTrees();
        if (crateManager != null) crateManager.loadRewards();
        if (rankManager != null) rankManager.loadRanks();
        if (monsterCardManager != null) monsterCardManager.loadCards();
        if (guideManager != null) guideManager.loadEntries();
        if (soulBeadManager != null) soulBeadManager.loadBeads();
        if (jobManager != null) jobManager.loadJobs();
    }
    public static IndieRPG getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public ItemManager getItemManager() { return itemManager; }
    public AttributeManager getAttributeManager() { return attributeManager; }
    public ShopManager getShopManager() { return shopManager; }
    public DailyRewardManager getDailyRewardManager() { return dailyRewardManager; }
    public JewelryManager getJewelryManager() { return jewelryManager; }
    public JewelryGrowthManager getJewelryGrowthManager() { return jewelryGrowthManager; }
    public GrowthManager getGrowthManager() { return growthManager; }
    public SpaceRingManager getSpaceRingManager() { return spaceRingManager; }
    public SoulStorageManager getSoulStorageManager() { return soulStorageManager; }
    public TalentManager getTalentManager() { return talentManager; }
    public TaskManager getTaskManager() { return taskManager; }
    public CrateManager getCrateManager() { return crateManager; }
    public RankManager getRankManager() { return rankManager; }
    public BattlePassManager getBattlePassManager() { return battlePassManager; }
    public MonsterCardManager getMonsterCardManager() { return monsterCardManager; }
    public GuideManager getGuideManager() { return guideManager; }
    public ExchangeManager getExchangeManager() { return exchangeManager; }
    public SoulBeadManager getSoulBeadManager() { return soulBeadManager; }
    public JobManager getJobManager() { return jobManager; }
    public AttributeEngine getAttributeEngine() { return attributeEngine; }
    public MythicMobsHook getMythicMobsHook() { return mythicMobsHook; }
}
