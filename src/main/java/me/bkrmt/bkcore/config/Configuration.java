package me.bkrmt.bkcore.config;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.ItemManager;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.message.InternalMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Configuration extends YamlConfiguration {
    private final BkPlugin plugin;
    private final File file;
    private final ConfigType type;
    private long timeStamp;

    public Configuration(BkPlugin plugin, File newFile, ConfigType type) {
        this.plugin = plugin;
        this.type = type;
        file = newFile;
        timeStamp = file.lastModified();

        createData();
        loadFromFile();

        if (plugin.containsResource(file.getName()) ||
                (type.equals(ConfigType.Lang) && !file.getName().equalsIgnoreCase("pt_BR.yml")
                        && !file.getName().equalsIgnoreCase("en_US.yml"))) {
            validateOptions();
        }
    }

    public Configuration(BkPlugin plugin, File newFile) {
        this.plugin = plugin;
        this.type = ConfigType.Config;
        file = newFile;

        createData();
        loadFromFile();

        if (plugin.containsResource(file.getName())) {
            validateOptions();
        }
    }

    private void validateOptions() {
        Set<String> configKeys = getKeys(true);
        FileConfiguration resourceConfig = null;
        if (type.equals(ConfigType.Config)) {
            resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(file.getName())));
        } else if (type.equals(ConfigType.Lang)) {
            String fileName = "en_US.yml";
            if (file.getName().equalsIgnoreCase("pt_BR.yml") || file.getName().equalsIgnoreCase("en_US.yml")) {
                fileName = file.getName();
            }
            resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(file.getName())));
        }
        if (resourceConfig != null) {
            Set<String> resourceKeys = resourceConfig.getKeys(true);
            for (String resKey : resourceKeys) {
                if (!configKeys.contains(resKey)) {
                    set(resKey, resourceConfig.get(resKey));
                }
            }
        }
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    public void loadFromFile() {
        if (file.exists()) {
            try {
                this.load(file);
            } catch
            (Exception e) {
                e.printStackTrace();
                plugin.sendConsoleMessage(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                        .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                        .replace("{1}", file.getName()
                        ));
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public File getFile() {
        return this.file;
    }

    public void saveToFile() {
        try {
            if (!file.exists()) file.createNewFile();
            this.save(file);
            if (type != ConfigType.Player_Data)
                updateFromResource();
        } catch
        (Exception e) {
            e.printStackTrace();
            plugin.sendConsoleMessage(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                    .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                    .replace("{1}", file.getName()
                    ));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private void createData() {
        if (!file.exists()) {
            File filePath = new File(Utils.getCleanPath(file));
            boolean pathExists = true;
            if (!filePath.exists()) {
                pathExists = filePath.mkdirs();
            }

            if (pathExists) {
                if (plugin.containsResource(file.getName())) {
                    try {
                        this.file.createNewFile();
                    } catch
                    (Exception e) {
                        e.printStackTrace();
                        plugin.sendConsoleMessage(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                                .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                                .replace("{1}", file.getName()
                                ));
                        Bukkit.getPluginManager().disablePlugin(plugin);
                    }
                } else {
                    updateFromResource();
                }
            } else {
                plugin.sendConsoleMessage(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                        .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                        .replace("{1}", file.getName()
                        ));
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public ConfigType getType() {
        return type;
    }

    public void setLocation(String path, Location location) {
        set(path + ".world", location.getWorld().getName());
        set(path + ".x", location.getX());
        set(path + ".y", location.getY());
        set(path + ".z", location.getZ());
        set(path + ".pitch", location.getPitch());
        set(path + ".yaw", location.getYaw());
    }

    public Location getLocation(String path) {
        if (getString(path + ".world") == null) {
            return null;
        }
        return new Location(Bukkit.getWorld(getString(path + ".world")), getDouble(path + ".x"), getDouble(path + ".y"), getDouble(path + ".z"), (float) getDouble(path + ".yaw"), (float) getDouble(path + ".pitch"));
    }

    public void setItemStack(String path, ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }
        if (plugin.getNmsVer().number < 13) {
            set(path + ".byte", item.getData().getData());
            set(path + ".damage", item.getDurability());
        }
        set(path + ".material", item.getType().toString());
        set(path + ".ammount", item.getAmount());
        if (item.getItemMeta().getDisplayName() == null) {
            set(path + ".name", item.getType().toString());
        } else {
            set(path + ".name", item.getItemMeta().getDisplayName().replace("§", "&"));
        }
        List<String> lore = new ArrayList<>();
        if (item.getItemMeta().getLore() != null) {
            for (String l : item.getItemMeta().getLore()) {
                lore.add(l.replace("§", "&"));
            }
        }
        set(path + ".lore", lore);
        for (Enchantment e : item.getItemMeta().getEnchants().keySet()) {
            set(path + ".enchants." + e.getName() + ".level", item.getEnchantmentLevel(e));
            try {
                save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            for (Enchantment e : meta.getStoredEnchants().keySet()) {
                set(path + ".enchants." + e.getName() + ".level", meta.getStoredEnchantLevel(e));
                try {
                    save(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        /*if (item.getItemMeta() instanceof SpawnEggMeta) {
            SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
            set(path + ".spawneggmeta.type", meta.getSpawnedType().toString());
            try {
                save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            set(path + ".potionmeta.type", meta.getBasePotionData().getType().toString());
            set(path + ".potionmeta.isextended", meta.getBasePotionData().isExtended());
            set(path + ".potionmeta.isupgraded", meta.getBasePotionData().isUpgraded());
            try {
                save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLore(String path) {
        List<String> translatedLore = new ArrayList<>();
        List<String> oldLore = getStringList(path);
        if (oldLore != null) {
            for (String line : oldLore) {
                translatedLore.add(Utils.translateColor(line));
            }
        }
        return translatedLore;
    }

    public ItemStack getItemStack(String path) {
        ItemStack item = null;
        if (plugin.getNmsVer().number < 13) {
            item = new ItemStack(Material.valueOf(getString(path + ".material", "STONE")), getInt(path + ".ammount", 1), (short) getInt(path + ".damage", 0), (byte) getInt(path + ".byte", 0));
        } else {
            item = new ItemStack(Material.valueOf(getString(path + ".material", "STONE")), getInt(path + ".ammount", 1));
        }
        if (getString(path + ".material") == null) {
            return null;
        }
        List<String> lore = new ArrayList<>();
        for (String l : getStringList(path + ".lore")) {
            lore.add(Utils.translateColor(l));
        }
        if (item.getType().equals(Material.POTION) /*|| item.getType().equals(Utils.getMaterial("Material.LINGERING_POTION) || item.getType().equals(Utils.getMaterial("Material.SPLASH_POTION) || item.getType().equals(Utils.getMaterial("Material.TIPPED_ARROW)*/) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.valueOf(getString(path + ".potionmeta.type")), getBoolean(path + ".potionmeta.isextended"), getBoolean(path + ".potionmeta.isupgraded")));
            item.setItemMeta(meta);
        }/*
        if (item.getItemMeta() instanceof SpawnEggMeta) {
            SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
            meta.setSpawnedType(EntityType.valueOf(getString(path + ".spawneggmeta.type")));
            item.setItemMeta(meta);
        }*/
        if (getConfigurationSection(path + ".enchants") != null) {
            for (String l : getConfigurationSection(path + ".enchants").getKeys(false)) {
                if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(Enchantment.getByName(l), getInt(path + ".enchants." + l + ".level"), false);
                    item.setItemMeta(meta);
                } else if (item.getItemMeta() != null) {
                    ItemMeta meta = item.getItemMeta();
                    meta.addEnchant(Enchantment.getByName(l), getInt(path + ".enchants." + l + ".level"), true);
                    item.setItemMeta(meta);
                }
            }
        }
        if (getString(path + ".name") != null) {
            if (ChatColor.stripColor(getString(path + ".name").replace("&", "§")).equals(item.getType().toString())) {
                ItemManager.setLore(item, lore);
            } else {
                ItemManager.setNameAndLore(item, Utils.translateColor(getString(path + ".name")), lore);
            }
        }
        return item;
    }

    public void updateFromResource() {
        String fileName = file.getName();
        try {
            if (plugin.containsResource(file.getName())) {
                ConfigUpdater.update(plugin, fileName, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.sendConsoleMessage(InternalMessages.INVALID_CONFIG.getMessage(plugin)
                    .replace("{0}", Utils.translateColor("&7[&4" + plugin.getName() + "&7]&c"))
                    .replace("{1}", fileName
                    ));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}