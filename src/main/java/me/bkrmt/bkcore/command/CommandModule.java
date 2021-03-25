package me.bkrmt.bkcore.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CommandModule {
    private final PluginCommand command;

    public CommandModule(Executor executor, TabCompleter tabCompleter) {
        if (executor.getPlugin().isRunning()) {
            command = executor.getPlugin().getCommandMapper().createPluginCommand(executor.getName());
            if (executor.getPlugin().getNmsVer().number > 8) command.setName(executor.getName());
            command.setLabel(executor.getName());
            command.setDescription(executor.getDescription());
            command.setUsage(executor.getUsage());
            command.setExecutor(executor);
            if (tabCompleter != null) command.setTabCompleter(tabCompleter);
        } else {
            command = null;
        }
    }

    public PluginCommand getCommand() {
        return command;
    }
}
