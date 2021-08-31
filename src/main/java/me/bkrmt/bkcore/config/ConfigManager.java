package me.bkrmt.bkcore.config;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private final ConcurrentHashMap<String, Configuration> configs;
    private final BkPlugin plugin;
    private final String DATA_FOLDER;

    public ConfigManager(BkPlugin plugin) {
        this.plugin = plugin;
        this.configs = new ConcurrentHashMap<>();
        DATA_FOLDER = plugin.getDataFolder().getPath();
        File configFile = plugin.getFile("", "config.yml");
        Configuration config = new Configuration(plugin, configFile, ConfigType.CONFIG);

        if (!config.getFile().exists()) config.saveToFile();
        else if (config.getFile().exists() && config.getFile().length() == 0) config.saveToFile();

        addConfig(config);
        startModifiedChecker();
    }

    private void startModifiedChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (configs.size() > 0) {
                    for (String key : configs.keySet()) {
                        Configuration config = configs.get(key);
                        if (config.getFile().exists()) {
                            long currentTimeStamp = config.getFile().lastModified();
                            if (currentTimeStamp != config.getTimeStamp()) {
                                config.setTimeStamp(currentTimeStamp);
                                config.loadFromFile();
                            }
                        } else {
                            configs.remove(key);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    public Configuration getConfig() {
        return getConfig("config.yml");
    }

    public Configuration getConfig(String name) {
        return getConfig(DATA_FOLDER, name);
    }

    public Configuration getConfig(String path, String name) {
        String temp = path;
        if (!path.equals(DATA_FOLDER))
            path = DATA_FOLDER + File.separatorChar + path;
        if (configs.size() > 0) {
            Configuration returnConfig = configs.get(name + "@" + path);
            if (returnConfig != null) return returnConfig;
        }
        File existingFile = plugin.getFile(temp, name);
        if (existingFile.exists()) {
            Configuration cachedConfig = new Configuration(plugin, existingFile);
            addConfig(cachedConfig);
            return cachedConfig;
        }

        /*plugin.sendConsoleMessage(Utils.translateColor(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                .replace("{1}", name
                )));
        Bukkit.getPluginManager().disablePlugin(plugin);*/
        return null;
    }

    public void addConfig(Configuration config) {
        configs.put(config.getFile().getName() + "@" + Utils.getCleanPath(config.getFile()), config);
    }

    public void removeConfig(String path, String name) {
        if (!path.equals(DATA_FOLDER))
            path = DATA_FOLDER + File.separatorChar + path;
        configs.remove(name + "@" + path);
    }

    public ConcurrentHashMap<String, Configuration> getConfigs() {
        return configs;
    }

    public boolean containsConfig(String name) {
        return containsConfig(DATA_FOLDER, name);
    }

    public boolean containsConfig(String path, String name) {
        if (!path.equals(DATA_FOLDER))
            path = DATA_FOLDER + File.separatorChar + path;
        return configs.containsKey(name + "@" + path);
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    public void reloadConfigs() {
        for (Configuration config : configs.values()) {
            if (config.getType() != ConfigType.PLAYER_DATA)
                config.loadFromFile();
        }
    }

    public void saveConfigs() {
        try {
            for (Configuration config : configs.values()) {
                if (config != null && config.getFile().exists() && config.getType() != ConfigType.PLAYER_DATA) {
                    config.saveToFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllFromFolder(File folder, ConfigType assignedType) {
        if (folder != null && folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".yml")) {
                        addConfig(new Configuration(plugin, file, assignedType));
                    }
                }
            }
        }
    }

    public void loadAllConfigs() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            File[] pluginFiles = plugin.getDataFolder().listFiles();
            if (pluginFiles != null && pluginFiles.length > 0) {
                for (File file : pluginFiles) {
                    if (file.getName().endsWith(".yml") && !file.getName().equalsIgnoreCase("config.yml") && !file.getPath().contains("lang")) {
                        addConfig(new Configuration(plugin, file, ConfigType.CONFIG));
                    }
                }
            }
        }, 0);
    }
}