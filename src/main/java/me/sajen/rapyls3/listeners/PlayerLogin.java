package me.sajen.rapyls3.listeners;

import me.sajen.rapyls3.configuration.BansConfig;
import me.sajen.rapyls3.Main;
import me.sajen.rapyls3.configuration.MessagesConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import java.util.Date;
import java.util.UUID;

public class PlayerLogin implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (BansConfig.isBanned(playerUUID)) {
            Date banTime;
            try {
                banTime = BansConfig.getBanTime(playerUUID);
            } catch (Exception e) {
                e.printStackTrace();
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Main.Color(MessagesConfig.getString("blad-sprawdzania-bana")));
                return;
            }

            Date currentTime = new Date();

            if (currentTime.after(banTime)) {
                BansConfig.removeBan(playerUUID);
                event.allow();
            } else {
                String banReason = BansConfig.getBanReason(playerUUID);
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Main.Color(banReason));
            }
        }
    }

}
