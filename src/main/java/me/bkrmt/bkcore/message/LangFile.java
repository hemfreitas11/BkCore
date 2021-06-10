package me.bkrmt.bkcore.message;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;

public class LangFile {
    private String language;
    private final BkPlugin plugin;
    private Configuration messageFile;
    private final Hashtable<String, String> messages;

    public LangFile(BkPlugin plugin, ArrayList<String> langList) {
        this.plugin = plugin;

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdir();

        Utils.verifyConfig(plugin);
        language = plugin.getConfig().getString("language");
        messages = new Hashtable<>();

        String langFile = language + ".yml";

        InputStream stream = plugin.getResource(langFile);

        if (stream == null && !plugin.getFile("lang", langFile).exists()) {
            plugin.getServer().getLogger().log(Level.WARNING, InternalMessages.NO_LANG.getMessage().replace("{0}", langFile));
            language = "en_US";
            langFile = language + ".yml";
        } else {
            stream = null;
        }

        loadMessageFile();
        langList.remove(language);
        for (String lang : langList) {
            createLang(lang);
        }

        loadMessages();
    }

    public final void loadMessageFile() {
        messageFile = createLang(language);
    }

    private Configuration createLang(String language) {
        return new Configuration(plugin, "lang", language + ".yml", ConfigType.Lang);
    }

    public final String getLanguage() {
        return language;
    }

    public Configuration getConfig() {
        return messageFile;
    }

    public String get(OfflinePlayer player, String key) {
        return null;
    }

    public String get(String key) {
        return get(key, true);
    }

    public String get(String key, boolean translate) {
        try {
            String message = messages.get(key);
            return translate ? ChatColor.translateAlternateColorCodes('&', message) : message;
        } catch (Exception ignored) {
            plugin.sendConsoleMessage(Utils.translateColor(InternalMessages.INVALID_MESSAGE.getMessage().replace("{0}", "&7[&4" + plugin.getName() + "&7]&c").replace("{1}", ChatColor.stripColor(key))));
        }
        return ChatColor.RED + "Error, check console!";
    }

    private void loadMessages() {
        for (String key : messageFile.getKeys(true)) {
            messages.put(key, messageFile.getString(key));
        }
    }
}
