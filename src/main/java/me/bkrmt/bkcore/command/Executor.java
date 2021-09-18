package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.message.InternalMessages;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Iterator;

public abstract class Executor implements CommandExecutor {
    private final String name;
    private final String description;
    private String usage;
    private final String permission;
    private final BkPlugin plugin;
    private final String langKey;
    private final String permissionMessage;

    public Executor(BkPlugin plugin, String langKey, String permission) {
        this.plugin = plugin;
        this.langKey = langKey;
        if (langKey.contains("bkcommand")) {
            this.name = plugin.getName().toLowerCase();
            this.description = "&7Shows the help of the plugin, and opens the config editor.";
            this.usage = "/" + name + " [";
            ConfigurationSection subCommands = plugin.getLangFile().getConfig().getConfigurationSection("commands.bkcommand.subcommands");
            Iterator<String> iterator = subCommands.getKeys(false).iterator();
            StringBuilder usageBuilder = new StringBuilder(usage);
            while (iterator.hasNext()) {
                String key = iterator.next();
                usageBuilder.append(subCommands.get(key + ".command"));
                if (iterator.hasNext()) usageBuilder.append(" | ");
                else usageBuilder.append("]");
            }
            this.usage = usageBuilder.toString();
        } else {
            this.name = plugin.getLangFile().get("commands." + langKey + ".command");
            this.description = plugin.getLangFile().get("commands." + langKey + ".description");
            this.usage = plugin.getLangFile().get("commands." + langKey + ".usage");
        }
        this.permissionMessage = plugin.getLangFile().get("error.no-permission");
        this.permission = permission;
    }

    public String getLangKey() {
        return langKey;
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    public boolean blockConsole(CommandSender sender, String prefix) {
        if (!(sender instanceof Player)) {
            String message = InternalMessages.NO_CONSOLE_SENDER.getMessage(plugin).replace("{0}", Utils.translateColor(prefix));
            plugin.sendConsoleMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public final void sendUsage(CommandSender sender) {
        ConfigurationSection section = plugin.getLangFile().getConfig().getConfigurationSection("commands." + langKey + ".subcommands");
        if (section != null) {
            StringBuilder subCommands = new StringBuilder();
            Iterator<String> it = section.getKeys(false).iterator();
            while (it.hasNext()) {
                subCommands.append(section.getString(it.next() + ".command"));
                if (it.hasNext()) subCommands.append(" | ");
            }
            sender.sendMessage(plugin.getLangFile().get("commands.usage-format")
                    .replace("{usage}", getUsage())
                    .replace("{subcommands}", subCommands.toString()));
        } else {
            sender.sendMessage(plugin.getLangFile().get("commands.usage-format").replace("{usage}", getUsage()));
        }
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public boolean hasPermission(CommandSender sender) {
        return !(sender instanceof Player) || sender.hasPermission(getPermission());
    }

    public boolean hasPermission(CommandSender sender, String sub) {
        String perm = permission + "." + sub;
        return sender.hasPermission(perm) || !(sender instanceof Player);
    }

    public String getPermission() {
        return permission;
    }
}
