package me.bkrmt.bkcore;

import me.bkrmt.bkcore.xlibs.XMaterial;
import me.bkrmt.nms.v1_16_R1.ColorUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    private static NMSVersion nmsVersion = new NMSVersion();

    private static String addHashCode(String string, int hashCode) {
        return !string.contains("@") ? string + ("@" + Integer.toHexString(hashCode)) : string;
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static int intFromPermission(Player player, int defaultValue, String permission, String[] bypassPermissions) {
        if (bypassPermissions != null) {
            for (String perm : bypassPermissions) {
                if (player.hasPermission(perm)) return -1;
            }
        }
        return (int) doubleFromPermission(player, defaultValue, permission);
    }

    public static String joinStringArray(String[] args) {
        StringBuilder argBuilder = new StringBuilder();
        for (String arg : args) {
            if (argBuilder.toString().isEmpty()) {
                argBuilder.append(arg);
            } else {
                argBuilder.append(" ").append(arg);
            }
        }
        return argBuilder.toString();
    }

    public static String getCleanPath(File file) {
        return file.getPath().replace(File.separatorChar + file.getName(), "");
    }

    public static String capitalize(String line) {
        char[] chars = line.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static double doubleFromPermission(Player player, int defaultValue, String permission) {
        String perm;
        for (PermissionAttachmentInfo pio : player.getEffectivePermissions()) {
            perm = pio.getPermission();
            if (perm.startsWith(permission)) {
                String ending = perm.substring(perm.lastIndexOf(".")).replace(".", "");
                if (StringUtils.isNumeric(ending)) {
                    return Double.parseDouble(ending);
                }
            }
        }
        return defaultValue;
    }

    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[]{content, armor};
    }

    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String cleanName(String name) {
        name = ChatColor.stripColor(name);
        name = name.replace("0", "")
                .replace("1", "")
                .replace("2", "")
                .replace("3", "")
                .replace("4", "")
                .replace("5", "")
                .replace("6", "")
                .replace("7", "")
                .replace("8", "")
                .replace("9", "")
                .replace("{", "")
                .replace("}", "")
                .replace("/", "")
                .replace("\\", "")
                .replace("'", "")
                .replace("-", "")
                .replace("+", "")
                .replace("currentpage", "")
                .replace("totalpages", "")
                .trim();
        return name;
    }

    public static boolean isValidColor(String string) {
        if (string.length() != 1) {
            return false;
        } else {
            char color = string.charAt(0);
            return color == 'a' || color == 'b' || color == 'c' || color == 'd' || color == 'e' || color == 'f' || Character.isDigit(color);
        }
    }

    public static boolean containsEqual(List<String> list, String text) {
        for (String line : list) {
            if (line.equalsIgnoreCase(text)) return true;
        }
        return false;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static String capsFirst(String message) {
        String result = message.toLowerCase();
        char firstLetter = message.charAt(0);
        result = result.replaceFirst(String.valueOf(Character.toLowerCase(firstLetter)), String.valueOf(Character.toUpperCase(firstLetter)));
        return result;
    }

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static boolean isBlank(List<String> list) {
        if (list == null) return true;
        if (list.size() == 0) return false;
        String lastMember = list.get(list.size()-1);
        return lastMember.isEmpty() ||
          StringUtils.isBlank(lastMember) ||
           StringUtils.isWhitespace(lastMember);
    }

    public static boolean canEncode(String string) {
        return StandardCharsets.ISO_8859_1.newEncoder().canEncode(string);
    }


    public static List<String> wrapLore(List<String> lore, int wrapLength) {
        List<String> returnList = new ArrayList<>();

        for (String line : lore) {
            if (line.length() > wrapLength) {
                Collections.addAll(returnList, WordUtils.wrap(line, wrapLength).replace("\r", "").split("\n"));
            } else {
                returnList.add(line);
            }
        }

        return returnList;
    }

    public static List<String> wrappedToList(String wrappedString, int wrapLength) {
        List<String> returnList = new ArrayList<>();
        Collections.addAll(returnList, WordUtils.wrap(wrappedString, wrapLength).replace("\r", "").split("\n"));
        return returnList;
    }

    public static String randomColor() {
        return randomColor(false);
    }

    public static String randomColor(boolean blackAndWhite) {
        int colorInt = 0;
        if (blackAndWhite) colorInt = new Random().nextInt(16);
        else {
            colorInt = new Random().nextInt(15);
            while (colorInt == 0) colorInt = new Random().nextInt(14);
        }
        String color;
        if (colorInt == 10) color = "a";
        else if (colorInt == 11) color = "b";
        else if (colorInt == 12) color = "c";
        else if (colorInt == 13) color = "d";
        else if (colorInt == 14) color = "e";
        else if (colorInt == 15) color = "f";
        else color = Integer.toString(colorInt);

        return color;
    }

    public static Player getPlayer(CommandSender commandSender) {
        return getPlayer(commandSender.getName());
    }

    public static Player getPlayer(String playerName) {
        return Bukkit.getServer().getPlayer(playerName);
    }

    public static String[] objectToString(Object[] objects) {
        String[] argsString = new String[objects.length];
        for (int i = 0; i <= objects.length - 1; i++) {
            argsString[i] = objects[i].toString();
        }
        return argsString;
    }

    public static String stripAccents(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return text;
    }

    public static String fixLineColor(String text) {
        String[] textParts = text.split(" ");
        StringBuilder fixedText = null;
        if (textParts.length > 0) {
            fixedText = new StringBuilder(textParts[0]).append(" ");
            for (int i = 1; i < textParts.length; i++) {
                fixedText.append(ChatColor.getLastColors(fixedText.toString())).append(textParts[i]).append(" ");
            }
        }
        return fixedText == null ? text : fixedText.toString();
    }


    public static ItemStack createItem(BkPlugin plugin, String name, Material material, Object... lore) {
        ArrayList<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, objectToString(lore));
        return createItem(plugin, name, material, loreList);
    }

    public static ItemStack getOnCoordinate(Inventory menu, int line, int colum) {
        for (int c = 0; c < menu.getSize() / 9; c++) {
            int start = 9 * c;
            int end = start + 8;
            int i = start;
            while (i <= end) {
                for (int col = 0; col < 9; col++) {
                    if (c == line - 1 && col == colum - 1) {
                        return menu.getItem(i);
                    }
                    i++;
                }
            }
        }
        return null;
    }

    public static void setOnCoordinate(Inventory menu, ItemStack item, int line, int colum) {
        for (int c = 0; c < menu.getSize() / 9; c++) {
            int start = 9 * c;
            int end = start + 8;
            int i = start;
            while (i <= end) {
                for (int col = 0; col < 9; col++) {
                    if (c == line - 1 && col == colum - 1) {
                        menu.setItem(i, item);
                        return;
                    }
                    i++;
                }
            }
        }
    }

    public static ItemStack createItem(Material material, boolean hideAttributes, String displayName, List<String> newLore) {
        return createItem(new ItemStack(material), hideAttributes, displayName, newLore);
    }

    public static ItemStack createItem(ItemStack item, boolean hideAttributes, String displayName, List<String> newLore) {
        ItemMeta tempMeta = item.getItemMeta();
        if (hideAttributes) tempMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> tempLore = new ArrayList<>();
        for (String line : newLore) {
            tempLore.add(Utils.translateColor(line));
        }
        tempMeta.setLore(tempLore);
        tempMeta.setDisplayName(displayName);
        item.setItemMeta(tempMeta);

        return item;
    }

    public static ItemStack getColoredLeather(Material leatherPiece, Color color, boolean hideAttributes, String displayName, List<String> lore) {
        ItemStack item = createItem(leatherPiece, hideAttributes, displayName, lore);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setColor(color);
        item.setItemMeta(meta);

        return item;
    }

    public static String cleanString(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String translateColor(String message) {
        if (nmsVersion.number >= 16 && message.contains("#")) message = ColorUtil.translateHex(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ItemStack createItem(BkPlugin plugin, String name, XMaterial material, ArrayList<String> lore) {
        ItemStack item = null;
        if (ChatColor.stripColor(name).contains(ChatColor.stripColor(plugin.getLangFile().get("info.next-name"))) ||
                ChatColor.stripColor(name).contains(ChatColor.stripColor(plugin.getLangFile().get("info.return-name")))) {
            if (ChatColor.stripColor(name).contains(ChatColor.stripColor(plugin.getLangFile().get("info.next-name"))))
                item = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
            else item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        } else if (material.equals(XMaterial.PLAYER_HEAD)) {
            item = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta headMeta = (SkullMeta) item.getItemMeta();
            headMeta = plugin.getHandler().getMethodManager().setHeadOwner(headMeta, plugin.getServer().getOfflinePlayer(ChatColor.stripColor(name)));
            headMeta.setDisplayName(name);
            if (!lore.isEmpty()) headMeta.setLore(lore);
            headMeta.setLore(lore);
            if (plugin.getNmsVer().number > 7) headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(headMeta);
            return item;
        }
        if (item == null) item = material.parseItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name.trim());
        if (!lore.isEmpty()) itemMeta.setLore(lore);
        if (plugin.getNmsVer().number > 7) itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }

}
