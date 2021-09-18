//package me.bkrmt.bkcore;
//
//import me.bkrmt.bkcore.xlibs.XMaterial;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.inventory.meta.SkullMeta;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ItemManager {
//
//    public static ItemStack setNameAndLore(ItemStack item, String name, String... lore) {
//        ItemMeta im = item.getItemMeta();
//        im.setDisplayName(name);
//        List<String> il = new ArrayList<String>();
//        for (String l : lore) {
//            il.add(l);
//        }
//        im.setLore(il);
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack setNameAndLoreAndEnchants(ItemStack item, String name, List<String> il, List<String> enchants) {
//        ItemMeta im = item.getItemMeta();
//        im.setDisplayName(name);
//        im.setLore(il);
//        for (String enchant : enchants) {
//            String[] part = enchant.split(":");
//            im.addEnchant(Enchantment.getByName(part[0]), Integer.getInteger(part[1]), true);
//        }
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack setName(ItemStack item, String name) {
//        ItemMeta im = item.getItemMeta();
//        im.setDisplayName(name);
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack setLore(ItemStack item, String... lore) {
//        ItemMeta im = item.getItemMeta();
//        List<String> il = new ArrayList<String>();
//        for (String l : lore) {
//            il.add(l);
//        }
//        im.setLore(il);
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack setLore(ItemStack item, List<String> lore) {
//        ItemMeta im = item.getItemMeta();
//        List<String> il = new ArrayList<String>();
//        for (String l : lore) {
//            il.add(l);
//        }
//        im.setLore(il);
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack setNameAndLore(ItemStack item, String name, List<String> il) {
//        ItemMeta im = item.getItemMeta();
//        im.setDisplayName(name);
//        im.setLore(il);
//        item.setItemMeta(im);
//        return item;
//    }
//
//    public static ItemStack stripLore(BkPlugin plugin, ItemStack item) {
//
//        if (item.getType().equals(XMaterial.PLAYER_HEAD.getType())) {
//            SkullMeta meta = (SkullMeta) item.getItemMeta();
//            List<String> lore = new ArrayList<String>();
//            meta.setLore(lore);
//            item.setItemMeta(meta);
//            return item;
//        } else {
//            ItemMeta meta = item.getItemMeta();
//            List<String> lore = new ArrayList<String>();
//            meta.setLore(lore);
//            item.setItemMeta(meta);
//            return item;
//        }
//    }
//
//    public static boolean isEqual(BkPlugin plugin, ItemStack item1, ItemStack item2) {
//        return stripLore(plugin, item1).equals(stripLore(plugin, item2));
//    }
//
//    public static ItemStack setUnbreakable(ItemStack itemStack) {
//        ItemMeta meta = itemStack.getItemMeta();
//        meta.setUnbreakable(true);
//        itemStack.setItemMeta(meta);
//        return itemStack;
//    }
//}