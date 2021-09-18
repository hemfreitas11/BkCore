package me.bkrmt.bkcore.command;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.entity.Player;

public interface GUIConfigPlaceholder {
    void run(BkPlugin plugin, Player player, Configuration configuration);
}
