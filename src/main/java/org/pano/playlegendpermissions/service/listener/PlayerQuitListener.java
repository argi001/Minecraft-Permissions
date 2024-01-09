package org.pano.playlegendpermissions.service.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pano.playlegendpermissions.service.PlayerService;

public class PlayerQuitListener implements Listener {
    private final PlayerService playerService;

    public PlayerQuitListener(PlayerService playerService) {
        this.playerService = playerService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final var player = playerService.toPlayer(event.getPlayer());
        playerService.deleteFromCache(player.getUuid());
    }

}
