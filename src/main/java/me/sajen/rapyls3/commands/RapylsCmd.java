package me.sajen.rapyls3.commands;

import me.sajen.rapyls3.configuration.BansConfig;
import me.sajen.rapyls3.Main;
import me.sajen.rapyls3.configuration.MessagesConfig;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.*;

public class RapylsCmd implements CommandExecutor, TabCompleter {
    private static CommandSender sender;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RapylsCmd.sender = sender;
        if (args.length == 0) {
            sendStringList("lista-komend");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "unban" -> unban(args);
            case "ban" -> ban(args);
            case "reset" -> reset(args);
            case "sprawdz" -> sprawdz(args);
            case "dajserce" -> dajserce(args);
            case "reload" -> reload(args);
            default -> sendString("nie-znaleziono-komendy");
        }
        return true;
    }

    public void unban(String[] args) {
        if (args.length != 2) {
            sendString("poprawne-uzycie-unban");
            return;
        }
        if (wasNeverOnline(args[1])) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (!BansConfig.isBanned(target.getUniqueId())) {
            sendString("nie-ma-bana", args[1]);
            return;
        }

        BansConfig.removeBan(target.getUniqueId());

        sendString("gracz-odbanowany", target.getName());
        Bukkit.broadcastMessage(Main.Color(MessagesConfig.getString("broadcast-gracz-odbanowany").replace("TARGET", target.getName())));
    }
    public void ban(String[] args) {
        if (args.length != 2) {
            sendString("poprawne-uzycie-ban");
            return;
        }
        if (wasNeverOnline(args[1])) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (isOffline(target)) return;

        sendString("gracz-zbanowany", target.getName());
        Main.ban(target.getPlayer(), sender.getName());
    }
    public void reset(String[] args) {
        if (args.length != 2) {
            sendString("poprawne-uzycie-reset");
            return;
        }
        if (wasNeverOnline(args[1])) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (isOffline(target)) return;

        Player player = target.getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20d);
        player.setHealth(20d);
        player.setFoodLevel(20);

        sendString("gracz-zresetowano", target.getName());
        target.getPlayer().sendMessage(Main.Color(MessagesConfig.getString("target-zresetowano")));
    }
    public void sprawdz(String[] args) {
        if (args.length != 2) {
            sendString("poprawne-uzycie-sprawdz");
            return;
        }

        if (wasNeverOnline(args[1])) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (isOffline(target)) return;

        int hearts = (int) target.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.getString("sprawdz-serca-1"));
        } else {
            sender.sendMessage(Main.Color(MessagesConfig.getString("sprawdz-serca-2")
                    .replace("TARGET", target.getName())
                    .replace("HEARTS", String.valueOf(hearts))));
        }
    }
    public void dajserce(String[] args) {
        if (args.length != 3) {
            sendString("poprawne-uzycie-dajserce");
            return;
        }
        if (wasNeverOnline(args[1])) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (isOffline(target)) return;

        try {
            Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            sendString("zly-poziom-serca");
            return;
        }

        int level = Integer.parseInt(args[2]);
        if (level > 3 || level < 1) {
            sendString("zly-poziom-serca");
            return;
        }

        if (target.getPlayer().getInventory().firstEmpty() == -1) {
            sendString("dajserce-pelny-ekwipunek", target.getName());
            return;
        }

        if (level == 1) {
            target.getPlayer().getInventory().addItem(Main.heart(1));
            sendString("dajserce-dostal1", target.getName());
            target.getPlayer().sendMessage(Main.Color(MessagesConfig.getString("dajserce-otrzymano1")));
        } else if (level == 2) {
            target.getPlayer().getInventory().addItem(Main.heart(2));
            sendString("dajserce-dostal2", target.getName());
            target.getPlayer().sendMessage(Main.Color(MessagesConfig.getString("dajserce-otrzymano2")));
        } else {
            target.getPlayer().getInventory().addItem(Main.heart(3));
            sendString("dajserce-dostal3", target.getName());
            target.getPlayer().sendMessage(Main.Color(MessagesConfig.getString("dajserce-otrzymano3")));
        }
    }
    public void reload(String[] args) {
        if (args.length != 1) {
            sendString("poprawne-uzycie-reload");
            return;
        }
        Main.get().reloadConfig();
        MessagesConfig.reload();
        BansConfig.reload();
        sendString("reload");
    }

    public static void sendString(String key) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MessagesConfig.getString(key + "-1"));
        } else {
            p.sendMessage(Main.Color(MessagesConfig.getString(key + "-2")));
        }
    }
    public static void sendString(String key, String target) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MessagesConfig.getString(key + "-1").replace("TARGET", target));
        } else {
            p.sendMessage(Main.Color(MessagesConfig.getString(key + "-2").replace("TARGET", target)));
        }
    }
    public static void sendStringList(String key) {
        List<String> listConsole = MessagesConfig.getStringList(key + "-1");
        List<String> listPlayer = MessagesConfig.getStringList(key + "-2");
        if (!(sender instanceof Player p)) {
            listConsole.forEach(str -> sender.sendMessage(str));
        } else {
            listPlayer.forEach(str -> p.sendMessage(Main.Color(str)));
        }
    }
    private boolean wasNeverOnline(String target) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            String playerName = player.getName();
            if (playerName != null && playerName.equals(target)) {
                return false;
            }
        }
        sendString("nie-byl-na-serwerze", target);
        return true;
    }
    private boolean isOffline(OfflinePlayer target) {
        if (target.isOnline()) {
            return false;
        }
        sendString("gracz-offline", target.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("ban");
            completions.add("unban");
            completions.add("sprawdz");
            completions.add("reset");
            completions.add("dajserce");
            completions.add("reload");
            return completions;
        }
        else if (args[0].equalsIgnoreCase("ban")) {
            if (args.length == 2) return null;
        }
        else if (args[0].equalsIgnoreCase("reset")) {
            if (args.length == 2) return null;
        }
        else if (args[0].equalsIgnoreCase("sprawdz")) {
            if (args.length == 2) return null;
        }
        else if (args[0].equalsIgnoreCase("dajserce")) {
            if (args.length == 2) {
                return null;
            }
            if (args.length == 3) {
                completions.add("1");
                completions.add("2");
                completions.add("3");
                return completions;
            }
        }
        else if (args[0].equalsIgnoreCase("unban")) {
            if (args.length == 2) {
                List<String> bannedPlayers = BansConfig.getBannedPlayers();
                for (String playerName : bannedPlayers) {
                    if (playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(playerName);
                    }
                }
                return completions;
            }
        }
        return new ArrayList<>();
    }
}
