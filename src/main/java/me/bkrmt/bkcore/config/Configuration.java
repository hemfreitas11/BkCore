package me.bkrmt.bkcore.config;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.ItemManager;
import me.bkrmt.bkcore.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Configuration extends YamlConfiguration {

    private final BkPlugin plugin;
    private File file;
    private final File filePath;
    private final ConfigType type;

    public Configuration(BkPlugin plugin, String path, String name, ConfigType type) {
        this.plugin = plugin;
        this.type = type;
        if (!path.equals(plugin.getDataFolder().getPath()))
            path = plugin.getDataFolder().getPath() + File.separatorChar + path;
        filePath = new File(path);
        file = new File(filePath, name);

        loadFile(file);
        createData();
        loadConfig();

        if (plugin.getResource(file.getName()) != null ||
                (type.equals(ConfigType.Lang) && !file.getName().equalsIgnoreCase("pt_BR.yml") && !file.getName().equalsIgnoreCase("en_US.yml"))) {
            validateOptions().save(true);
        }

    }

    public Configuration validateOptions() {
        Set<String> configKeys = getKeys(true);
        FileConfiguration resourceConfig = null;
        if (type.equals(ConfigType.Config)) {
            resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(file.getName())));
        } else if (type.equals(ConfigType.Lang)) {
            String fileName = "en_US.yml";
            if (file.getName().equalsIgnoreCase("pt_BR.yml") || file.getName().equalsIgnoreCase("en_US.yml")) {
                fileName = file.getName();
            }
            resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(fileName)));
        }
        Set<String> resourceKeys = resourceConfig.getKeys(true);
        for (String resKey : resourceKeys) {
            if (!configKeys.contains(resKey)) {
                set(resKey, resourceConfig.get(resKey));
            }
        }
        return this;
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    private void loadConfig() {
        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public void save(boolean includeComments) {
        try {
            super.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (includeComments) {
            update(Collections.singletonList("none"));
            loadConfig();
        }
    }

    public void save(List<String> ignoredSections) {
        try {
            super.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update(ignoredSections);
        loadConfig();
    }

    public void createData() {
        if (!file.exists()) {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            if (this.plugin.getResource(this.file.getName()) == null) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Utils.copyFromResource(plugin, file.getName(), filePath.getPath() + File.separatorChar + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete() {
        if (this.file.exists()) {
            this.file.delete();
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

    public void update(List<String> ignoredSections) {
        String resourceName;

        if (getType().equals(ConfigType.Config) ||
                (getType().equals(ConfigType.Lang) && (file.getName().equalsIgnoreCase("pt_BR.yml") || file.getName().equalsIgnoreCase("en_US.yml")))) {
            resourceName = file.getName();
        } else {
            resourceName = "en_US.yml";
        }

        BufferedReader newReader = new BufferedReader(new InputStreamReader(plugin.getResource(resourceName), StandardCharsets.UTF_8));
        List<String> newLines = newReader.lines().collect(Collectors.toList());
        try {
            newReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(resourceName)));
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String> ignoredSectionsArrayList = new ArrayList<>(ignoredSections);
        ignoredSectionsArrayList.removeIf(ignoredSection -> !newConfig.isConfigurationSection(ignoredSection));

        Yaml yaml = new Yaml();
        Map<String, String> comments = parseComments(newLines, ignoredSectionsArrayList, oldConfig, yaml);
        try {
            write(newConfig, oldConfig, comments, ignoredSectionsArrayList, writer, yaml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(FileConfiguration newConfig, FileConfiguration oldConfig, Map<String, String> comments, List<String> ignoredSections, BufferedWriter writer, Yaml yaml) throws IOException {
        outer:
        for (String key : newConfig.getKeys(true)) {
            String[] keys = key.split("\\.");
            String actualKey = keys[keys.length - 1];
            String comment = comments.remove(key);

            StringBuilder prefixBuilder = new StringBuilder();
            int indents = keys.length - 1;
            appendPrefixSpaces(prefixBuilder, indents);
            String prefixSpaces = prefixBuilder.toString();

            if (comment != null) {
                writer.write(comment);
            }

            for (String ignoredSection : ignoredSections) {
                if (key.startsWith(ignoredSection)) {
                    continue outer;
                }
            }

            Object newObj = newConfig.get(key);
            Object oldObj = oldConfig.get(key);

            if (newObj instanceof ConfigurationSection && oldObj instanceof ConfigurationSection) {
                writeSection(writer, actualKey, prefixSpaces, (ConfigurationSection) oldObj);
            } else if (newObj instanceof ConfigurationSection) {
                writeSection(writer, actualKey, prefixSpaces, (ConfigurationSection) newObj);
            } else if (oldObj != null) {
                write(oldObj, actualKey, prefixSpaces, yaml, writer);
            } else {
                write(newObj, actualKey, prefixSpaces, yaml, writer);
            }
        }

        String danglingComments = comments.get(null);

        if (danglingComments != null) {
            writer.write(danglingComments);
        }

        writer.close();
    }

    private void write(Object obj, String actualKey, String prefixSpaces, Yaml yaml, BufferedWriter writer) throws IOException {
        if (obj instanceof ConfigurationSerializable) {
            writer.write(prefixSpaces + actualKey + ": " + yaml.dump(((ConfigurationSerializable) obj).serialize()));
        } else if (obj instanceof String || obj instanceof Character) {
            if (obj instanceof String) {
                String s = (String) obj;
                obj = s.replace("\n", "\\n");
            }

            writer.write(prefixSpaces + actualKey + ": " + yaml.dump(obj));
        } else if (obj instanceof List) {
            writeList((List) obj, actualKey, prefixSpaces, yaml, writer);
        } else {
            writer.write(prefixSpaces + actualKey + ": " + yaml.dump(obj));
        }
    }

    private void writeSection(BufferedWriter writer, String actualKey, String prefixSpaces, ConfigurationSection section) throws IOException {
        if (section.getKeys(false).isEmpty()) {
            writer.write(prefixSpaces + actualKey + ": {}");
        } else {
            writer.write(prefixSpaces + actualKey + ":");
        }

        writer.write("\n");
    }

    private void writeList(List list, String actualKey, String prefixSpaces, Yaml yaml, BufferedWriter writer) throws IOException {
        writer.write(getListAsString(list, actualKey, prefixSpaces, yaml));
    }

    private String getListAsString(List list, String actualKey, String prefixSpaces, Yaml yaml) {
        StringBuilder builder = new StringBuilder(prefixSpaces).append(actualKey).append(":");

        if (list.isEmpty()) {
            builder.append(" []\n");
            return builder.toString();
        }

        builder.append("\n");

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);

            if (o instanceof String || o instanceof Character) {
                builder.append(prefixSpaces).append("- '").append(o).append("'");
            } else if (o instanceof List) {
                builder.append(prefixSpaces).append("- ").append(yaml.dump(o));
            } else {
                builder.append(prefixSpaces).append("- ").append(o);
            }

            if (i != list.size()) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private Map<String, String> parseComments(List<String> lines, List<String> ignoredSections, FileConfiguration oldConfig, Yaml yaml) {
        Map<String, String> comments = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();
        int lastLineIndentCount = 0;

        outer:
        for (String line : lines) {
            if (line != null && line.trim().startsWith("-"))
                continue;

            if (line == null || line.trim().equals("") || line.trim().startsWith("#")) {
                builder.append(line).append("\n");
            } else {
                lastLineIndentCount = setFullKey(keyBuilder, line, lastLineIndentCount);

                for (String ignoredSection : ignoredSections) {
                    if (keyBuilder.toString().equals(ignoredSection)) {
                        Object value = oldConfig.get(keyBuilder.toString());

                        if (value instanceof ConfigurationSection)
                            appendSection(builder, (ConfigurationSection) value, new StringBuilder(getPrefixSpaces(lastLineIndentCount)), yaml);

                        continue outer;
                    }
                }

                if (keyBuilder.length() > 0) {
                    comments.put(keyBuilder.toString(), builder.toString());
                    builder.setLength(0);
                }
            }
        }

        if (builder.length() > 0) {
            comments.put(null, builder.toString());
        }

        return comments;
    }

    private void appendSection(StringBuilder builder, ConfigurationSection section, StringBuilder prefixSpaces, Yaml yaml) {
        builder.append(prefixSpaces).append(getKeyFromFullKey(section.getCurrentPath())).append(":");
        Set<String> keys = section.getKeys(false);

        if (keys.isEmpty()) {
            builder.append(" {}\n");
            return;
        }

        builder.append("\n");
        prefixSpaces.append("  ");

        for (String key : keys) {
            Object value = section.get(key);
            String actualKey = getKeyFromFullKey(key);

            if (value instanceof ConfigurationSection) {
                appendSection(builder, (ConfigurationSection) value, prefixSpaces, yaml);
                prefixSpaces.setLength(prefixSpaces.length() - 2);
            } else if (value instanceof List) {
                builder.append(getListAsString((List) value, actualKey, prefixSpaces.toString(), yaml));
            } else {
                builder.append(prefixSpaces.toString()).append(actualKey).append(": ").append(yaml.dump(value));
            }
        }
    }

    private int countIndents(String s) {
        int spaces = 0;

        for (char c : s.toCharArray()) {
            if (c == ' ') {
                spaces += 1;
            } else {
                break;
            }
        }

        return spaces / 2;
    }

    private void removeLastKey(StringBuilder keyBuilder) {
        String temp = keyBuilder.toString();
        String[] keys = temp.split("\\.");

        if (keys.length == 1) {
            keyBuilder.setLength(0);
            return;
        }

        temp = temp.substring(0, temp.length() - keys[keys.length - 1].length() - 1);
        keyBuilder.setLength(temp.length());
    }

    private String getKeyFromFullKey(String fullKey) {
        String[] keys = fullKey.split("\\.");
        return keys[keys.length - 1];
    }

    private int setFullKey(StringBuilder keyBuilder, String configLine, int lastLineIndentCount) {
        int currentIndents = countIndents(configLine);
        String key = configLine.trim().split(":")[0];

        if (keyBuilder.length() == 0) {
            keyBuilder.append(key);
        } else if (currentIndents == lastLineIndentCount) {
            removeLastKey(keyBuilder);

            if (keyBuilder.length() > 0) {
                keyBuilder.append(".");
            }

            keyBuilder.append(key);
        } else if (currentIndents > lastLineIndentCount) {
            keyBuilder.append(".").append(key);
        } else {
            int difference = lastLineIndentCount - currentIndents;

            for (int i = 0; i < difference + 1; i++) {
                removeLastKey(keyBuilder);
            }

            if (keyBuilder.length() > 0) {
                keyBuilder.append(".");
            }

            keyBuilder.append(key);
        }

        return currentIndents;
    }

    private String getPrefixSpaces(int indents) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indents; i++) {
            builder.append("  ");
        }

        return builder.toString();
    }

    private void appendPrefixSpaces(StringBuilder builder, int indents) {
        builder.append(getPrefixSpaces(indents));
    }

}