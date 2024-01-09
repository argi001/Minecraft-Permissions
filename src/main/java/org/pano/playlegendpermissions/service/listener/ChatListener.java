package org.pano.playlegendpermissions.service.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;

import java.util.logging.Logger;

public class ChatListener implements Listener {
    private final PlayerService playerService;
    private final Logger logger;

    public ChatListener(PlayerService playerService, JavaPlugin javaPlugin) {
        this.playerService = playerService;
        this.logger = javaPlugin.getLogger();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        try {
            Player player = playerService.toPlayer(event.getPlayer());
            final var playerDao = playerService.getPlayersActiveGroup(player);
            if (!playerDao.getCurrentGroupPrefix().isEmpty()) {
                String format = event.getFormat();
                event.setFormat("[" + playerDao.getCurrentGroupPrefix() + "] " + format);
            }
        } catch (Exception e) {
            logger.warning("Could not modify Player Message, showing no Prefix for User:" + event.getPlayer().getName());
        }
    }

}
