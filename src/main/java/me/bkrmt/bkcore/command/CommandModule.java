package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandModule {
    private final PluginCommand command;

    public CommandModule(Executor executor, TabCompleter tabCompleter) {
        BkPlugin plugin = executor.getPlugin();
        command = plugin.getCommandMapper().createPluginCommand(executor.getName());
        if (executor.getPlugin().getNmsVer().number > 8) command.setName(executor.getName());
        command.setLabel(executor.getName());
        command.setDescription(executor.getDescription());
        command.setUsage(executor.getUsage());
        command.setExecutor(executor);
        if (executor.getName().equalsIgnoreCase(plugin.getName())) {
            command.setTabCompleter(
                (commandSender, command1, s, args) -> {
                    if (commandSender.hasPermission(plugin.getName().toLowerCase()+".admin")) {
                        List<String> completions = new ArrayList<>();
                        if (args.length == 1) {
                            String partialCommand = args[0];

                            List<String> subCommands = new ArrayList<>(plugin.getLangFile().getConfig().getConfigurationSection("commands.bkcommand.subcommands").getKeys(false));
                            StringUtil.copyPartialMatches(partialCommand, subCommands, completions);
                        }
                        return completions;
                    } else {
                        return new ArrayList<>();
                    }
                }
            );
        } else {
            if (tabCompleter != null) command.setTabCompleter(tabCompleter);
        }
    }

    public PluginCommand getCommand() {
        return command;
    }
}
