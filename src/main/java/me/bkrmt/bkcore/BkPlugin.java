package me.bkrmt.bkcore;

import me.bkrmt.bkcore.actionbar.ActionBar;
import me.bkrmt.bkcore.command.CommandMapper;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkcore.message.InternalMessages;
import me.bkrmt.bkcore.message.LangFile;
import me.bkrmt.bkcore.title.Title;
import me.bkrmt.nms.api.NMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class BkPlugin extends JavaPlugin {
    private CommandMapper commandMapper;
    private LangFile langFile;
    private NMSVersion nmsVersion;
    private NMS nmsApi;
    private boolean hasHandler = false;
    private ArrayList<String> langList;
    private boolean running;

    public final CommandMapper start() {
        return start(false);
    }

    public final CommandMapper start(boolean hasHandler) {
        try {
            getConfig();
            this.hasHandler = hasHandler;
            if (langList == null) this.langList = new ArrayList<>();

            langList.add("en_US");
            langList.add("pt_BR");
            running = false;

            langFile = new LangFile(this, langList);
            commandMapper = new CommandMapper(this);
            nmsVersion = new NMSVersion();
            if (nmsVersion.number <= 7)
                getServer().getLogger().log(Level.SEVERE, InternalMessages.INCOMPATIBLE_VERSION.getMessage());

            return getCommandMapper();
        } catch (Exception ignored) {return null;}
    }

    public final void addLanguage(String language) {
        if (langList == null) this.langList = new ArrayList<>();
        langList.add(language);
    }

    public final CommandMapper getCommandMapper() {
        return commandMapper;
    }

    public final File getFile(String filePath, String fileName) {
        if (!filePath.equals(getDataFolder().getPath()))
            filePath = getDataFolder().getPath() + File.separatorChar + filePath;
        File configPath = new File(filePath);
        return new File(configPath, fileName);
    }

    public final NMSVersion getNmsVer() {
        return nmsVersion;
    }

    @Override
    public final Configuration getConfig() {
        return getConfig("config.yml");
    }

    public final Configuration getConfig(String name) {
        return getConfig(getDataFolder().getPath(), name);
    }

    public final Configuration getConfig(String path, String name) {
        return new Configuration(this, path, name, ConfigType.Config);
    }

    public final void callEvent(Event event) {
        getServer().getPluginManager().callEvent(event);
    }

    public LangFile getLangFile() {
        return langFile;
    }

    public final NMS getHandler() {
        return nmsApi;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void sendConsoleMessage(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }

    public final void sendStartMessage(String prefix) {
        String message = Utils.translateColor(InternalMessages.PLUGIN_START.getMessage(this).replace("{0}", prefix));
        getServer().getConsoleSender().sendMessage(message);
    }

    public final void setRunning(boolean running) {
        this.running = running;
    }

    public final void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        if (getNmsVer().number == 8) {
            getHandler().getTitleManager().sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
        } else {
            new Title(this).sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
        }
    }

    public final void sendActionBar(Player player, String message) {
        if (getNmsVer().number < 13) new ActionBar(this, player).sendActionBar(message);
        else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public final boolean hasHandler() {
        return hasHandler;
    }

    public ItemStack createHead(UUID owner, String name, List<String> lore) {
        ItemStack item = getHandler().getItemManager().getHead();
        SkullMeta headMeta = (SkullMeta) item.getItemMeta();
        headMeta = getHandler().getMethodManager().setHeadOwner(headMeta, getServer().getOfflinePlayer(owner));
        headMeta.setDisplayName(name);
        if (!lore.isEmpty()) headMeta.setLore(lore);
        headMeta.setLore(lore);
        if (getNmsVer().number > 7) headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(headMeta);
        return item;
    }

    public final void buildHandler() {
        String apiVersion;

        switch (getNmsVer().number) {
            default:
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + "-----------------------WARNING-------------------------");
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + getName() + " does not support this minecraft version.");
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + "The plugin will start with support for the version 1.14");
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + "but you will probably find problems.");
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + "Look for an update in: URL");
                getServer().getLogger().log(Level.WARNING, ChatColor.RED + "-----------------------WARNING-------------------------");
                apiVersion = "me.bkrmt.nms.v1_14_R1.NMSHandler";
                break;
            case 8:
                apiVersion = "me.bkrmt.nms." + getNmsVer().full + ".NMSHandler";
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                apiVersion = "me.bkrmt.nms.v1_9_R1.NMSHandler";
                break;
            case 13:
                apiVersion = "me.bkrmt.nms.v1_13_R1.NMSHandler";
                break;
            case 14:
            case 15:
            case 16:
                apiVersion = "me.bkrmt.nms.v1_14_R1.NMSHandler";
                break;
        }
        try {
            if (running) nmsApi = (NMS) Class.forName(apiVersion).getConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            getServer().getLogger().log(Level.SEVERE, "The plugin could not be started...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
