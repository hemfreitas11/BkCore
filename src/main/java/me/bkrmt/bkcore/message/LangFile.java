package me.bkrmt.bkcore.message;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;

public class LangFile {
    private String language;
    private final BkPlugin plugin;
    private Configuration messageFile;

    public LangFile(BkPlugin plugin, ArrayList<String> langList) {
        this.plugin = plugin;

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdir();

        Utils.verifyConfig(plugin);
        language = plugin.getConfig().getString("language");

        String langFile = language + ".yml";

        InputStream stream = plugin.getResource(langFile);

        if (stream == null && !plugin.getFile("lang", langFile).exists()) {
            plugin.getServer().getLogger().log(Level.WARNING, InternalMessages.NOLANG.getMessage().replace("{0}", langFile));
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

    public String get(String key) {
        return get(key, true);
    }

    public String get(String key, boolean translate) {
        if (translate) return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
        else return getConfig().getString(key);
    }

    public String prefix(String msg) {
        return ChatColor.translateAlternateColorCodes('&', get("prefix")) + msg;
    }
}
