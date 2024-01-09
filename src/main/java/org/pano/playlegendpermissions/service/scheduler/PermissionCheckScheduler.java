package org.pano.playlegendpermissions.service.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;

import java.sql.Timestamp;
import java.util.logging.Logger;

import static org.pano.playlegendpermissions.config.localization.MessageKey.PLAYER_NTFC_GROUP_EXPIRED;

/**
 * A scheduler class for checking and updating player permissions based on group expiration.
 * This class regularly checks if any players' group assignments have expired and updates their permissions accordingly.
 */
public class PermissionCheckScheduler extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final PlayerCacheManager playerCacheManager;
    private final PlayerService playerService;
    private final LocalizationManager localization;

    /**
     * Constructs a PermissionCheckScheduler with necessary dependencies.
     *
     * @param plugin             The main JavaPlugin instance.
     * @param playerCacheManager Manager for the player cache.
     * @param playerService      Service for player-related operations.
     * @param localization       localization object to get correct language
     */
    public PermissionCheckScheduler(JavaPlugin plugin, PlayerCacheManager playerCacheManager, PlayerService playerService, LocalizationManager localization) {
        this.plugin = plugin;
        this.playerCacheManager = playerCacheManager;
        this.playerService = playerService;
        this.logger = plugin.getLogger();
        this.localization = localization;
    }

    /**
     * Runs the permission check task. This method is invoked repeatedly based on a set interval.
     * It checks for players whose group assignments have expired and updates their group status.
     */
    @Override
    public void run() {
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        playerCacheManager.getAll().stream()
                .filter(playerDAO -> playerDAO.getCurrentGroupExpire() != null && playerDAO.getCurrentGroupExpire().before(currentTime))
                .forEach(playerDAO -> {
                    try {
                        Player player = new Player(playerDAO.getUuid(), playerDAO.getName());
                        playerService.deleteFromCache(playerDAO.getUuid());
                        final var activePlayerDao = playerService.getPlayersActiveGroup(player);
                        Thread.sleep(100);
                        playerService.sendPlayerMsg(player, localization.getFormattedMessage(PLAYER_NTFC_GROUP_EXPIRED, playerDAO.getCurrentGroupName(), activePlayerDao.getCurrentGroupName()));
                    } catch (Exception e) {
                        logger.warning("Could not invalidate players current group please check for: " + playerDAO.getName());
                    }
                });
    }

    /**
     * Starts the permission check task with a fixed delay and period.
     * The task is scheduled to run repeatedly at a fixed rate = 20L -> 1s.
     */
    public void startTask() {
        this.runTaskTimer(plugin, 0L, 20L);
    }
}
