package me.sajen.rapyls3.commands;

import me.sajen.rapyls3.Main;
import me.sajen.rapyls3.configuration.MessagesConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WithdrawCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.getString("wyplac-konsola"));
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 0) {
            player.sendMessage(Main.Color(MessagesConfig.getString("wyplac-poprawne-uzycie")));
            return true;
        }
        double health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;

        // Gracz nie może wypłacić
        if (health <= Main.get().getConfig().getInt("min-withdraw")) {
            player.sendMessage(Main.Color(MessagesConfig.getString("wyplac-jedno-serce")));
            return true;
        }

        int maxHearts1 = Main.get().getConfig().getInt("serce-1.serca-max");
        int maxHearts2 = Main.get().getConfig().getInt("serce-2.serca-max");

        if (health <= maxHearts1) {
            player.getInventory().addItem(Main.heart(1));
            player.sendMessage(Main.Color(MessagesConfig.getString("wyplacono-serce-1")));
        } else if (health <= maxHearts2) {
            player.getInventory().addItem(Main.heart(2));
            player.sendMessage(Main.Color(MessagesConfig.getString("wyplacono-serce-2")));
        } else {
            player.getInventory().addItem(Main.heart(3));
            player.sendMessage(Main.Color(MessagesConfig.getString("wyplacono-serce-3")));
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health * 2 - 2);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }

}
