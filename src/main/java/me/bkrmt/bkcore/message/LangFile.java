package me.bkrmt.bkcore.message;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LangFile {
    private String language;
    private final BkPlugin plugin;
    private final Configuration langConfig;
    private ConcurrentHashMap<String, String> messages;
    private ConcurrentHashMap<String, List<String>> lists;
    private TimeLocale timeLocale;

    public LangFile(BkPlugin plugin, ArrayList<String> langList) {
        this.plugin = plugin;

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdir();

        language = plugin.getConfigManager().getConfig().getString("language");
        messages = new ConcurrentHashMap<>();
        lists = new ConcurrentHashMap<>();

        String langFile = language + ".yml";
        if (!plugin.containsResource(langFile) && !plugin.getFile("lang", langFile).exists()) {
            plugin.getServer().getLogger().log(Level.WARNING, InternalMessages.NO_LANG.getMessage().replace("{0}", langFile));
            language = "en_US";
        }

        langConfig = createConfig(language);

        langList.remove(language);
        for (String lang : langList) {
            createConfig(lang);
        }

        loadMessages();

        this.timeLocale = new TimeLocale(plugin);
    }

    public TimeLocale getTimeLocale() {
        return timeLocale;
    }

    private Configuration createConfig(String language) {
        Configuration config = new Configuration(plugin, plugin.getFile("lang", language + ".yml"), ConfigType.LANG);
        if (!config.getFile().exists()) config.saveToFile();
        else if (config.getFile().exists() && config.getFile().length() == 0) config.saveToFile();
        return config;
    }

    public final String getLanguage() {
        return language;
    }

    public Configuration getConfig() {
        return langConfig;
    }

    public List<String> getStringList(OfflinePlayer player, String key) {
        List<String> list = getStringList(key);
        boolean hasPapiHook = plugin.hasPlaceholderHook();
        list.forEach(line -> {
            if (line.contains("%") && hasPapiHook) list.set(list.indexOf(line), PlaceholderAPI.setPlaceholders(player, line));
        });
        return list;
    }

    public List<String> getStringList(String key) {
        return getStringList(key, true);
    }

    public List<String> getStringList(String key, boolean translate) {
        try {
            List<String> list = new ArrayList<>(lists.get(key) == null ? Collections.singletonList(messages.get(key)) : lists.get(key));
            if (translate) list.forEach(line -> list.set(list.indexOf(line), Utils.translateColor(line)));
            return list;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            plugin.sendConsoleMessage(Utils.translateColor(InternalMessages.INVALID_MESSAGE.getMessage().replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c")).replace("{1}", ChatColor.stripColor(formatKey(key)))));
        }
        return Collections.singletonList(ChatColor.RED + "Error, check console!");
    }

    public String get(OfflinePlayer player, String key) {
        return get(player, key, true);
    }

    public String get(OfflinePlayer player, String key, boolean translate) {
        try {
            String text = get(key, translate);
            return text.contains("%") && plugin.hasPlaceholderHook() ? PlaceholderAPI.setPlaceholders(player, text) : text;
        } catch (Exception e) {
            e.printStackTrace();
            return "§cError when trying to get the message " + formatKey(key);
        }
    }

    public String get(String key) {
        return get(key, true);
    }

    public String get(String key, boolean translate) {
        try {
            String message = messages.get(key);
            if (message.contains("%") && plugin.hasPlaceholderHook()) message = PlaceholderAPI.setPlaceholders(null, message);
            return translate ? Utils.translateColor(message) : message;
        } catch (Exception ignored) {
            plugin.sendConsoleMessage(Utils.translateColor(InternalMessages.INVALID_MESSAGE.getMessage().replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c")).replace("{1}", ChatColor.stripColor(formatKey(key)))));
        }
        return ChatColor.RED + "Error, check console!";
    }

    public void reloadMessages() {
        langConfig.loadFromFile();
        messages = new ConcurrentHashMap<>();
        lists = new ConcurrentHashMap<>();
        loadMessages();
    }

    private String formatKey(String key) {
        return key.replaceAll("\\.", " -> ");
    }

    private void loadMessages() {
        for (String key : langConfig.getKeys(true)) {
            if (key.startsWith(".")) key = key.replaceFirst(".", "");
            List<String> list = langConfig.getStringList(key);
            if (list.size() <= 1) {
                if (list.size() == 0)
                    messages.put(key, langConfig.getString(key));
                else
                    messages.put(key, list.get(0));
            } else {
                lists.put(key, list);
            }
        }
    }
}
