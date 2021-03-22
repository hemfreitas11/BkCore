package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Executor implements CommandExecutor {
    private final String name;
    private final String description;
    private final String usage;
    private final String permission;
    private final BkPlugin plugin;
    private final String langKey;
    private final String permissionMessage;

    public Executor(BkPlugin plugin, String langKey, String permission) {
        this.plugin = plugin;
        this.langKey = langKey;
        this.name = plugin.getLangFile().get("commands." + langKey + ".command");
        this.description = plugin.getLangFile().get("commands." + langKey + ".description");
        this.usage = plugin.getLangFile().get("commands." + langKey + ".usage");
        this.permissionMessage = plugin.getLangFile().get("error.no-permission");
        this.permission = permission;
    }

    public String getLangKey() {
        return langKey;
    }

    public BkPlugin getPlugin() {
        return plugin;
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
        sender.sendMessage(plugin.getLangFile().get("commands.usage-format").replace("{usage}", getUsage()));
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
