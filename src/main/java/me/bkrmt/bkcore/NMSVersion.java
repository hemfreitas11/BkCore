package me.bkrmt.bkcore;

import org.bukkit.Bukkit;

public class NMSVersion {
    public String full;
    public String trimmed;
    public int number;

    public NMSVersion() {
        full = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        trimmed = full.split("_")[0] + "_" + full.split("_")[1];
        number = Integer.parseInt(trimmed.split("_")[1]);
    }
}
