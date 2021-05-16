package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class HelpCmd extends Executor {

    public HelpCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sendCommands(getPlugin(), sender);
        return true;
    }

    public static void sendCommands(BkPlugin plugin, CommandSender sender) {
        Configuration config = plugin.getLangFile().getConfig();
        ConfigurationSection commandsSection = config.getConfigurationSection("commands");
        sender.sendMessage(Utils.translateColor(config.getString("commands.help-format.header")));
        Set<String> keys = commandsSection.getKeys(false);
        for (String key : keys) {
            if (key.equalsIgnoreCase("usage-format") || key.equalsIgnoreCase("help-format")) continue;
            sender.sendMessage(Utils.translateColor(config.getString("commands.help-format.help-section").replace("{command}", config.getString("commands." + key + ".usage"))
                    .replace("{description}", config.getString("commands." + key + ".description"))));
        }
        sender.sendMessage(Utils.translateColor(config.getString("commands.help-format.footer")));
    }
}
