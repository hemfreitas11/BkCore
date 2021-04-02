package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Set;

public class CommandMapper {
    private final BkPlugin plugin;
    private final Hashtable<String, PluginCommand> commands;

    public CommandMapper(BkPlugin plugin) {
        this.plugin = plugin;
        commands = new Hashtable<>();
    }

    public final CommandMapper addCommand(CommandModule command) {
        commands.put(command.getCommand().getName(), command.getCommand());
        return this;
    }

    public final void register(String commandName) {
        CommandMap commandMap = getCommandMapInstance();
        commandMap.register(plugin.getDescription().getName(), commands.get(commandName));
        if (plugin.hasHandler()) plugin.buildHandler();
    }

    public final void registerAll() {
        Set<String> keys = commands.keySet();
        CommandMap commandMap = getCommandMapInstance();
        for (String key : keys) {
            if (commandMap != null) {
                commandMap.register(plugin.getDescription().getName(), commands.get(key));
            }
        }
        if (plugin.hasHandler()) plugin.buildHandler();
    }

    public final CommandMap getCommandMapInstance() {
        if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
            try {
                Field field = FieldUtils.getDeclaredField(spm.getClass(), "commandMap", true);
                return (CommandMap) field.get(spm);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't get the Bukkit CommandMap instance.");
            }
        }
        return null;
    }

    public final PluginCommand createPluginCommand(String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
