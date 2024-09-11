package me.sajen.rapyls3.listeners;

import me.sajen.rapyls3.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        double hearts = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;
        Player killer = player.getKiller();

        // Gracz nie został zabity przez gracza
        if (killer == null || killer == player) {
            // Wypadanie True
            if (Main.get().getConfig().getBoolean("wypadanie")) {
                event.getDrops().add(dropItem(hearts));
            }
        }
        // ANTY NABIJANIE
        else if (killer.getAddress() == player.getAddress() && Main.get().getConfig().getBoolean("anty-nabijanie")) {
            System.out.println(killer.getName() + " (ip: " + player.getAddress() + ") probowal zabic gracza " + player.getName() + " (ip: " + player.getAddress() + ")");
        }
        // Gracz został zabity przez gracza
        else {
            event.getDrops().add(dropItem(hearts));
        }

        // Usunięcie jednego serca
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hearts * 2 - 2);

        // Ban za brak serc
        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() == 1) {
            if (killer == null || killer == player) {
                Main.ban(player, "Samobójstwo");
            } else {
                Main.ban(player, killer.getName());
            }
        }
    }

    private ItemStack dropItem(double hearts) {
        int maxHearts1 = Main.get().getConfig().getInt("serce-1.serca-max");
        int maxHearts2 = Main.get().getConfig().getInt("serce-2.serca-max");

        if (hearts <= maxHearts1) {
            return Main.heart(1);
        }
        else if (hearts <= maxHearts2) {
            return Main.heart(2);
        }
        else {
            return Main.heart(3);
        }
    }
}
