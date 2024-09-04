package me.sajen.rapyls3;

import me.sajen.rapyls3.commands.*;
import me.sajen.rapyls3.configuration.BansConfig;
import me.sajen.rapyls3.configuration.MessagesConfig;
import me.sajen.rapyls3.listeners.PlayerDeath;
import me.sajen.rapyls3.listeners.PlayerInteract;
import me.sajen.rapyls3.listeners.PlayerLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        registerAll();
        MessagesConfig.setup();
        BansConfig.setup();
    }

    private void registerAll() {
        getCommand("rapyls").setExecutor(new RapylsCmd());
        getCommand("rapyls").setTabCompleter(new RapylsCmd());
        getCommand("wyplac").setExecutor(new WithdrawCmd());
        getCommand("wyplac").setTabCompleter(new WithdrawCmd());

        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
    }

    public static ItemStack heart(int i) {
        ConfigurationSection config = get().getConfig().getConfigurationSection("serce-" + i);
        ItemStack serce = new ItemStack(Material.matchMaterial(config.getString("type")));
        ItemMeta meta = serce.getItemMeta();
        meta.setDisplayName(Color(config.getString("name")));
        List<String> lore = config.getStringList("lore");
        meta.setLore(Color(lore));
        if (config.getBoolean("glint")) {
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setCustomModelData(config.getInt("custom-model-data"));
        serce.setItemMeta(meta);
        return serce;
    }

    public static void ban(Player target, String killer) {
        Bukkit.broadcastMessage(Main.Color(MessagesConfig.getString("broadcast-gracz-zbanowany").replace("TARGET", target.getName())));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, Main.get().getConfig().getInt("czas-bana"));

        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));

        List<String> reasonAsList = MessagesConfig.getStringList("powod-bana");
        String reason = Color(String.join("\n", reasonAsList));
        reason = reason.replace("KILLER", killer);
        reason = reason.replace("DATE", sdf.format(date));

        target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20d);
        target.setHealth(20d);
        target.setFoodLevel(20);

        BansConfig.saveBan(target.getUniqueId(), target.getName(), date, reason);
        target.getPlayer().kickPlayer(reason);
    }

    public static Main get() {
        return plugin;
    }
    public static String Color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static List<String> Color(List<String> list) {
        list.replaceAll(Main::Color);
        return list;
    }
}
