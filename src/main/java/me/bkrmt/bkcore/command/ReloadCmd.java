package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCmd extends Executor {

    public ReloadCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (hasPermission(sender)) {
            getPlugin().getConfigManager().reloadConfigs();
            getPlugin().getLangFile().reloadMessages();
            sender.sendMessage(getPlugin().getLangFile().get("info.configs-reloaded"));
        } else {
            sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
        }
        return true;
    }
}
