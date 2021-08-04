package me.bkrmt.bkcore.heads;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeadManager {
    BkPlugin plugin;
    Configuration cacheConfig;
    ConcurrentHashMap<String, Head> cachedHeads;
    ConcurrentHashMap<String, Head> newHeads;

    public HeadManager(BkPlugin plugin) {
        this.plugin = plugin;
        cachedHeads = new ConcurrentHashMap<>();
        newHeads = new ConcurrentHashMap<>();
        loadSavedHeads();
    }

    public ItemStack getPlayerHead(OfflinePlayer player, HeadRunnable runnable) {
        Head foundHead = findPlayerHead(player.getName().toLowerCase());
        if (foundHead != null) {
            return getCustomTextureHead(foundHead.getTexture());
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                String newHeadTexture = getHeadTexture(player.getName().toLowerCase());
                if (newHeadTexture != null) {
                    Head newHead = new Head(
                            player.getUniqueId().toString(),
                            player.getName().toLowerCase(),
                            newHeadTexture,
                            System.currentTimeMillis()
                    );
                    newHeads.put(player.getName().toLowerCase(), newHead);
                    runnable.run(newHead);
                }
            }, 3);
        }
        return getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3YWY5ZTQ0MTEyMTdjN2RlOWM2MGFjYmQzYzNmZDY1MTk3ODMzMzJhMWIzYmM1NmZiZmNlOTA3MjFlZjM1In19fQ==");
    }

    private Head findPlayerHead(String playerName) {
        Head cachedHead = cachedHeads.get(playerName);
        if (cachedHead != null) return cachedHead;

        return newHeads.get(playerName);
    }

    private void loadSavedHeads() {
        File cacheFolder = plugin.getFile("cached-data", "");
        if (!cacheFolder.exists()) cacheFolder.mkdir();
        File cacheFile = plugin.getFile("cached-data", "head-cache.yml");
        boolean fileCreated = false;
        if (!cacheFile.exists()) {
            try {
                fileCreated = cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else fileCreated = true;
        if (fileCreated) {
            cacheConfig = new Configuration(plugin, cacheFile);
            for (String playerName : cacheConfig.getKeys(false)) {
                Head cachedHead = new Head(
                        cacheConfig.getString(playerName + ".uuid"),
                        playerName,
                        cacheConfig.getString(playerName + ".texture"),
                        cacheConfig.getLong(playerName + ".time-stamp")
                );
                this.cachedHeads.put(playerName, cachedHead);
            }
        }
    }

    public void saveNewHeads() {
        for (String playerName : newHeads.keySet()) {
            Head newHead = newHeads.get(playerName);
            cacheConfig.set(playerName + ".uuid", newHead.getUuid().toString());
            cacheConfig.set(playerName + ".texture", newHead.getTexture());
            cacheConfig.set(playerName + ".time-stamp", newHead.getTimeStamp());
        }
        cacheConfig.saveToFile();
    }

    public ItemStack getCustomTextureHead(String value) {
        ItemStack head = plugin.getHandler().getItemManager().getHead();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applyGameProfile(value, meta);
        head.setItemMeta(meta);
        return head;
    }

    public static SkullMeta applyGameProfile(String value, SkullMeta meta) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        return meta;
    }

    public String getHeadTexture(String name) {
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            if (!result.isEmpty()) {
                Gson g = new Gson();
                JsonObject obj = g.fromJson(result, JsonObject.class);
                String uid = obj.get("id").toString().replace("\"", "");
                String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
                obj = g.fromJson(signature, JsonObject.class);
                String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                String decoded = new String(Base64.getDecoder().decode(value));
                obj = g.fromJson(decoded, JsonObject.class);
                String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
                return new String(Base64.getEncoder().encode(skinByte));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String getURLContent(String urlStr) {
        URL url;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
        }
        return sb.toString();
    }


}
