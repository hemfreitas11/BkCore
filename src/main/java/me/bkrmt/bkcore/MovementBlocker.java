package me.bkrmt.bkcore;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementBlocker {
    public static void processMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (((int) event.getFrom().getX() != (int) event.getTo().getX()) || ((int) event.getFrom().getZ() != (int) event.getTo().getZ())) {
            event.setCancelled(true);
            player.teleport(event.getFrom());
        }
    }
}
