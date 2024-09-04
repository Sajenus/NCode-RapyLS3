package me.sajen.rapyls3.configuration;

import com.google.common.base.Charsets;
import me.sajen.rapyls3.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MessagesConfig {

    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(Main.get().getDataFolder(), "messages.yml");

        if (!file.exists()) {
            Main.get().saveResource("messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static String getString(String key) {
        if (!config.contains(key)) {
            Main.get().getLogger().warning("Brak wartości " + key + " w pliku messages.yml");
            return "";
        }
        return config.getString(key);
    }

    public static List<String> getStringList(String key) {
        if (!config.contains(key)) {
            Main.get().getLogger().warning("Brak wartości " + key + " w pliku messages.yml");
            return new ArrayList<>();
        }
        return config.getStringList(key);
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defConfigStream = Main.get().getResource("messages.yml");
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }


}
