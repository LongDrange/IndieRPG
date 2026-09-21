package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcDialogueManager {
    private final IndieRPG plugin;
    private final Map<String, Dialogue> dialogues = new HashMap<String, Dialogue>();

    public NpcDialogueManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadDialogues();
    }

    public void loadDialogues() {
        dialogues.clear();
        ConfigurationSection root = plugin.getConfigManager().get("npc-dialogues")
                .getConfigurationSection("npc-dialogues");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            Dialogue d = new Dialogue();
            d.id = id;
            d.npcName = sec.getString("npc-name", id);
            for (Map<?, ?> raw : sec.getMapList("lines")) {
                Object text = raw.get("text");
                Object sound = raw.get("sound");
                d.lines.add(new DialogueLine(String.valueOf(text),
                        sound == null ? null : String.valueOf(sound)));
            }
            d.rewardGold = sec.getInt("reward-gold", 0);
            d.rewardRep = sec.getString("reward-faction", null);
            d.rewardRepAmount = sec.getInt("reward-rep", 0);
            dialogues.put(id, d);
        }
        plugin.getLogger().info("已載入 " + dialogues.size() + " 個 NPC 對話");
    }

    public void talk(Player player, String npcId) {
        Dialogue d = dialogues.get(npcId);
        if (d == null) {
            player.sendMessage("§c找不到對話：" + npcId);
            return;
        }
        for (DialogueLine line : d.lines) {
            player.sendMessage("§f" + d.npcName + "§7：§f" + line.text);
        }
        if (d.rewardGold > 0) {
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            data.gold += d.rewardGold;
            player.sendMessage("§a+" + d.rewardGold + " 金幣");
        }
        if (d.rewardRep != null && d.rewardRepAmount != 0) {
            plugin.getReputationManager().addRep(player, d.rewardRep, d.rewardRepAmount);
        }
    }

    public static class Dialogue {
        public String id;
        public String npcName;
        public final List<DialogueLine> lines = new ArrayList<DialogueLine>();
        public int rewardGold = 0;
        public String rewardRep;
        public int rewardRepAmount = 0;
    }

    public static class DialogueLine {
        public final String text;
        public final String sound;
        public DialogueLine(String text, String sound) {
            this.text = text;
            this.sound = sound;
        }
    }
}