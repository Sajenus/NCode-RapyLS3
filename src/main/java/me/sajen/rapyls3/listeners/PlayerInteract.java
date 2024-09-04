package me.sajen.rapyls3.listeners;

import me.sajen.rapyls3.Main;
import me.sajen.rapyls3.configuration.MessagesConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteract implements Listener {
    private Player player;
    private Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN = 10;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

        player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {
            long lastTime = cooldowns.get(uuid);
            if (currentTime - lastTime < COOLDOWN) {
                return;
            }
        }

        int usedHeart = getUsedHeart(inv);

        switch (usedHeart) {
            case 1 -> useHeart(1);
            case 2 -> useHeart(2);
            case 3 -> useHeart(3);
            case 4 -> useHeart(4);
            case 5 -> useHeart(5);
            case 6 -> useHeart(6);
        }

    }

    private void useHeart(int usedHeart) {
        double hearts = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;
        PlayerInventory inv = player.getInventory();
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        ItemStack usedItem;
        if (usedHeart < 4) {
            usedItem = inv.getItemInMainHand();
        } else {
            usedItem = inv.getItemInOffHand();
            usedHeart -= 3;
        }

        int minHearts = Main.get().getConfig().getInt("serce-" + usedHeart + ".serca-min");
        int maxHearts = Main.get().getConfig().getInt("serce-" + usedHeart + ".serca-max");
        if (hearts < minHearts-1 || hearts >= maxHearts) {
            player.sendMessage(Main.Color(MessagesConfig.getString("nie-mozesz-uzyc-serca-" + usedHeart)));
            cooldowns.put(uuid, currentTime);
            return;
        }
        usedItem.setAmount(usedItem.getAmount() - 1);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + 2);
        player.sendMessage(Main.Color(MessagesConfig.getString("uzyto-serce-" + usedHeart)));
        cooldowns.put(uuid, currentTime);
    }

    private int getUsedHeart(PlayerInventory inv) {
        ItemStack main = inv.getItemInMainHand();
        ItemStack off = inv.getItemInOffHand();
        if (main.isSimilar(Main.heart(1))) return 1;
        else if (main.isSimilar(Main.heart(2))) return 2;
        else if (main.isSimilar(Main.heart(3))) return 3;
        else if (off.isSimilar(Main.heart(1))) return 4;
        else if (off.isSimilar(Main.heart(2))) return 5;
        else if (off.isSimilar(Main.heart(3))) return 6;
        else return 0;
    }

}
