package com.indie.rpg.managers;

import com.indie.rpg.IndieRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MonsterCardManager {

    private final IndieRPG plugin;
    private final Map<String, CardDef> cards = new HashMap<>();
    private final Map<String, CardSet> sets = new HashMap<>();

    public MonsterCardManager(IndieRPG plugin) {
        this.plugin = plugin;
        loadCards();
    }

    public void loadCards() {
        cards.clear();
        sets.clear();

        if (plugin.getConfig().contains("cards.definitions")) {
            for (String key : plugin.getConfig().getConfigurationSection("cards.definitions").getKeys(false)) {
                String path = "cards.definitions." + key;
                CardDef card = new CardDef();
                card.id = key;
                card.effect = plugin.getConfig().getString(path + ".effect", "");
                cards.put(key, card);
            }
        }

        if (plugin.getConfig().contains("cards.sets")) {
            for (String key : plugin.getConfig().getConfigurationSection("cards.sets").getKeys(false)) {
                String path = "cards.sets." + key;
                CardSet set = new CardSet();
                set.id = key;
                set.cards = plugin.getConfig().getStringList(path + ".cards");
                set.effect = plugin.getConfig().getString(path + ".effect", "");
                sets.put(key, set);
            }
        }

        plugin.getLogger().info("Loaded " + cards.size() + " monster cards, " + sets.size() + " card sets");
    }

    public String detectCardFromLore(List<String> lores) {
        for (String lore : lores) {
            String stripped = ChatColor.stripColor(lore).trim().toLowerCase();
            if (stripped.startsWith("card-id:")) {
                return stripped.substring("card-id:".length()).trim();
            }
        }
        return null;
    }

    public void onMonsterKill(Player player, LivingEntity entity) {
        String mobType = entity.getType().name();
        for (CardDef card : cards.values()) {
            if (card.id.toLowerCase().contains(mobType.toLowerCase()) ||
                mobType.toLowerCase().contains(card.id.toLowerCase())) {
                player.sendMessage(ChatColor.DARK_PURPLE + "[Cards] " + ChatColor.RESET +
                        "Obtained card: " + card.id + "!");
            }
        }
    }

    public void applyCardEffects(Player player, Set<String> ownedCards) {
        for (String cardId : ownedCards) {
            CardDef card = cards.get(cardId);
            if (card != null && !card.effect.isEmpty()) {
                applyEffect(player, card.effect, 0);
            }
        }
        for (CardSet set : sets.values()) {
            if (ownedCards.containsAll(set.cards)) {
                if (!set.effect.isEmpty()) {
                    applyEffect(player, set.effect, 0);
                }
            }
        }
    }

    private void applyEffect(Player player, String effectStr, int amplifier) {
        String[] parts = effectStr.split(":");
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
        int amp = parts.length > 1 ? Integer.parseInt(parts[1]) : amplifier;
        if (type != null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp, true, false));
        }
    }

    public static class CardDef {
        public String id;
        public String effect;
    }

    public static class CardSet {
        public String id;
        public List<String> cards;
        public String effect;
    }
}
