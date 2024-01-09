package org.pano.playlegendpermissions.service.listener;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.config.localization.MessageKey;
import org.pano.playlegendpermissions.exceptions.GroupNotFoundException;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;
import org.pano.playlegendpermissions.store.DatabaseConfig;

import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final PlayerService playerService;
    private final UserGroupService userGroupService;
    final LocalizationManager localization;
    private final Logger logger;

    public PlayerJoinListener(PlayerService playerService, UserGroupService userGroupService, LocalizationManager localization, JavaPlugin javaPlugin) {
        this.playerService = playerService;
        this.userGroupService = userGroupService;
        this.localization = localization;
        this.logger = javaPlugin.getLogger();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
        final var player = playerService.toPlayer(event.getPlayer());
        if (playerService.getPlayer(player) == null) {
            playerService.savePlayer(player);
            onPlayerJoin(event);
        } else {
            final var playerDao = playerService.getPlayersActiveGroup(player);
            if (playerDao == null) {
                addPlayerToDefaultGroup(player);
                onPlayerJoin(event);
            } else {
                event.setJoinMessage(ChatColor.GOLD + generateJoinMessage(playerDao));
            }
        }
    }

    private String generateJoinMessage(PlayerDAO playerDao) {
        var prefixString = "";
        if (!playerDao.getCurrentGroupPrefix().isEmpty()) {
            prefixString = "[" + playerDao.getCurrentGroupPrefix() + "]";
        }
        return localization.getFormattedMessage(MessageKey.JOIN_MESSAGE,
                prefixString,
                playerDao.getName(),
                playerDao.getCurrentGroupName());
    }

    private void addPlayerToDefaultGroup(Player player) throws RuntimeException {
        try {
            playerService.addPlayerToGroup(player, userGroupService.getGroupByName(DatabaseConfig.DEFAULT_GROUP));
        } catch (GroupNotFoundException e) {
            logger.warning("Could not add User to Default group Reason: Default Group not found please check config");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
