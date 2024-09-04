package me.sajen.rapyls3.configuration;

import com.google.common.base.Charsets;
import me.sajen.rapyls3.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BansConfig {

    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(Main.get().getDataFolder(), "bans.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveBan(UUID playerUUID, String playerName, Date banTime, String banReason) {
        String formattedBanTime = formatDate(banTime);
        config.set(playerUUID + ".playerName", playerName);
        config.set(playerUUID + ".banTime", formattedBanTime);
        config.set(playerUUID + ".banReason", banReason);
        save();
    }

    public static void removeBan(UUID playerUUID) {
        config.set(playerUUID.toString(), null);
        save();
    }

    public static boolean isBanned(UUID playerUUID) {
        return config.contains(playerUUID.toString());
    }

    public static Date getBanTime(UUID playerUUID) throws ParseException {
        String banPath = playerUUID + ".banTime";
        String formattedBanTime = config.getString(banPath);
        if (formattedBanTime != null) {
            return parseDate(formattedBanTime);
        }
        return null;
    }

    public static String getBanReason(UUID playerUUID) {
        String banPath = playerUUID + ".banReason";
        return config.getString(banPath);
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBannedPlayers() {
        List<String> bannedPlayers = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            if (isBanned(playerUUID)) {
                String playerName = config.getString(key + ".playerName");
                bannedPlayers.add(playerName);
            }
        }
        return bannedPlayers;
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date);
    }

    private static Date parseDate(String formattedDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.parse(formattedDate);
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defConfigStream = Main.get().getResource("bans.yml");
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }

}
