package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class MainCommand extends Executor {
    private final GUIConfigPlaceholder placeholder;
    public MainCommand(BkPlugin plugin, String permission, GUIConfigPlaceholder placeholder) {
        super(plugin, "commands.bkcommand", permission);
        this.placeholder = placeholder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            if (hasPermission(sender)) {
                if (args[0].equalsIgnoreCase(getPlugin().getLangFile().get("commands.bkcommand.subcommands.config.command"))) {
                    placeholder.run(getPlugin(), player, getPlugin().getConfigManager().getConfig());
                } else if (args[0].equalsIgnoreCase(getPlugin().getLangFile().get("commands.bkcommand.subcommands.messages.command"))) {
                    placeholder.run(getPlugin(), player, getPlugin().getLangFile().getConfig());
                } else if (args[0].equalsIgnoreCase(getPlugin().getLangFile().get("commands.bkcommand.subcommands.reload.command"))) {
                    getPlugin().getConfigManager().reloadConfigs();
                    getPlugin().getLangFile().reloadMessages();
                    sender.sendMessage(getPlugin().getLangFile().get("info.configs-reloaded"));
                }
            } else {
                sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
            }
        } else {
            sendCommands(getPlugin(), sender);
        }
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
