package me.sajen.rapyls3.listeners;

import me.sajen.rapyls3.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity().getPlayer();
        double serca = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(serca - 2);
        Player killer = player.getKiller();

        // Gracz nie został zabity przez gracza
        if (killer == null || killer == player) {
            // Wypadanie True
            if (Main.get().getConfig().getBoolean("wypadanie")) {
                // Gracz ma 1 - 10 serc
                if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 20) {
                    event.getDrops().add(Main.heart(1));
                }
                // Gracz ma 11 - 20 serc
                else if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 40) {
                    event.getDrops().add(Main.heart(2));
                }
                // Gracz ma 21 - 30 serc
                else if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 60) {
                    event.getDrops().add(Main.heart(3));
                }
            }
        }
        // ANTY NABIJANIE
        else if (killer.getAddress() == player.getAddress() && Main.get().getConfig().getBoolean("anty-nabijanie")) {
            System.out.println(killer.getName() + " (ip: " + player.getAddress() + ") probowal zabic gracza " + player.getName() + " (ip: " + player.getAddress() + ")");
        }
        // Gracz został zabity przez gracza
        else {
            // Gracz ma 1 - 10 serc
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 18) {
                event.getDrops().add(Main.heart(1));
            }
            // Gracz ma 11 - 20 serc
            else if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 38) {
                event.getDrops().add(Main.heart(2));
            }
            // Gracz ma 21 - 30 serc
            else if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 48) {
                event.getDrops().add(Main.heart(3));
            }
        }

        // Ban za brak serc
        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() == 1) {
            if (killer == null || killer == player) {
                Main.ban(player, "Samobójstwo");
            } else {
                Main.ban(player, killer.getName());
            }
        }

    }
}
