package com.indie.rpg;

import com.indie.rpg.commands.CommandHandler;
import com.indie.rpg.listeners.InventoryListener;
import com.indie.rpg.listeners.PlayerListener;
import com.indie.rpg.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

/** LDAPI 入口；所有模組先初始化，再註冊事件與命令。 */
public class IndieRPG extends JavaPlugin {
    private static IndieRPG instance;
    private ConfigManager configManager;
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
    private JewelryGrowthManager jewelryGrowthManager;
    private GrowthManager growthManager;

    @Override public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        itemManager = new ItemManager(this);
        jewelryGrowthManager = new JewelryGrowthManager(this);
        growthManager = new GrowthManager(this);
        spaceRingManager = new SpaceRingManager(this);
        soulStorageManager = new SoulStorageManager(this);
        talentManager = new TalentManager(this);
        taskManager = new TaskManager(this);
        crateManager = new CrateManager(this);
        rankManager = new RankManager(this);
        battlePassManager = new BattlePassManager(this);
        dailyRewardManager = new DailyRewardManager(this);
        shopManager = new ShopManager(this);
        monsterCardManager = new MonsterCardManager(this);
        guideManager = new GuideManager(this);
        jewelryManager = new JewelryManager(this);
        exchangeManager = new ExchangeManager(this);
        soulBeadManager = new SoulBeadManager(this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        CommandHandler handler = new CommandHandler(this);
        for (String name : new String[]{"ldapi", "spacering", "talent", "task", "crate"}) {
            if (getCommand(name) != null) getCommand(name).setExecutor(handler);
        }
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
        if (shopManager != null) shopManager.loadShop();
        if (jewelryGrowthManager != null) jewelryGrowthManager.reload();
        if (talentManager != null) talentManager.loadTrees();
        if (crateManager != null) crateManager.loadRewards();
        if (rankManager != null) rankManager.loadRanks();
        if (monsterCardManager != null) monsterCardManager.loadCards();
        if (guideManager != null) guideManager.loadEntries();
        if (jewelryManager != null) jewelryManager.loadSets();
        if (soulBeadManager != null) soulBeadManager.loadBeads();
        getLogger().info("LDAPI 配置已重新載入。");
    }

    public static IndieRPG getInstance(){return instance;}
    public ConfigManager getConfigManager(){return configManager;}
    public PlayerDataManager getPlayerDataManager(){return playerDataManager;}
    public SpaceRingManager getSpaceRingManager(){return spaceRingManager;}
    public SoulStorageManager getSoulStorageManager(){return soulStorageManager;}
    public TalentManager getTalentManager(){return talentManager;}
    public TaskManager getTaskManager(){return taskManager;}
    public CrateManager getCrateManager(){return crateManager;}
    public RankManager getRankManager(){return rankManager;}
    public BattlePassManager getBattlePassManager(){return battlePassManager;}
    public ItemManager getItemManager(){return itemManager;}
    public DailyRewardManager getDailyRewardManager(){return dailyRewardManager;}
    public ShopManager getShopManager(){return shopManager;}
    public MonsterCardManager getMonsterCardManager(){return monsterCardManager;}
    public GuideManager getGuideManager(){return guideManager;}
    public JewelryManager getJewelryManager(){return jewelryManager;}
    public ExchangeManager getExchangeManager(){return exchangeManager;}
    public SoulBeadManager getSoulBeadManager(){return soulBeadManager;}
    public JewelryGrowthManager getJewelryGrowthManager(){return jewelryGrowthManager;}
    public GrowthManager getGrowthManager(){return growthManager;}
}
