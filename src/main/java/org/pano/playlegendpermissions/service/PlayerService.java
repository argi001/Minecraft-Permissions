package org.pano.playlegendpermissions.service;

import org.bukkit.Bukkit;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.Player2Group;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.Player2GroupRepository;
import org.pano.playlegendpermissions.repository.PlayerRepository;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;

import java.sql.Timestamp;

/**
 * Service class for managing player-related operations in the PlaylegendPermissions plugin.
 * It handles interactions with Player and Player2Group entities, as well as caching and messaging functionalities.
 */
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final Player2GroupRepository player2GroupRepository;
    private final PlayerCacheManager playerCacheManager;

    /**
     * Constructs a new PlayerService instance.
     *
     * @param playerRepository       Player repository for database operations related to players.
     * @param player2GroupRepository Player2Group repository for database operations related to player-group relationships.
     * @param playerCacheManager     Cache manager for player data.
     */
    public PlayerService(final PlayerRepository playerRepository, Player2GroupRepository player2GroupRepository, final PlayerCacheManager playerCacheManager) {
        this.playerRepository = playerRepository;
        this.player2GroupRepository = player2GroupRepository;
        this.playerCacheManager = playerCacheManager;
    }

    /**
     * Retrieves a player entity based on UUID or display name.
     *
     * @param player The player object with either UUID or display name.
     * @return The retrieved player entity, or null if not found.
     * @throws Exception If a database access error occurs.
     */
    public Player getPlayer(Player player) throws Exception {
        if (!player.getUuid().isEmpty()) {
            return playerRepository.findById(player.getUuid());
        } else if (!player.getDisplayName().isEmpty()) {
            return playerRepository.findFirstByName(player.getDisplayName());
        }
        return null;
    }

    /**
     * Adds a player's data object to the cache.
     *
     * @param playerDAO The player data object to cache.
     */
    public void addToCache(PlayerDAO playerDAO) {
        if (playerCacheManager.getById(playerDAO.getUuid()) != null) {
            deleteFromCache(playerDAO.getUuid());
        }
        playerCacheManager.addToCache(playerDAO);
    }

    /**
     * Retrieves the active group information for a given player.
     *
     * @param player The player whose active group is to be retrieved.
     * @return The active group data of the player.
     * @throws Exception If a database access error occurs.
     */
    public PlayerDAO getPlayersActiveGroup(Player player) throws Exception {
        PlayerDAO playerDAO = playerCacheManager.getById(player.getUuid());
        if (playerDAO == null) {
            final var playerGroups = player2GroupRepository.findAllByPlayer(player);
            for (Player2Group player2Group : playerGroups) {
                if (isActive(player2Group)) {
                    playerCacheManager.addToCache(toDAO(player2Group));
                    return toDAO(player2Group);
                }
            }
        }
        return playerDAO;
    }

    /**
     * Saves a player entity to the database.
     *
     * @param player The player entity to save.
     * @return The saved player entity.
     * @throws Exception If a database access error occurs.
     */
    public Player savePlayer(Player player) throws Exception {
        player = playerRepository.save(player);
        return player;
    }

    /**
     * Adds a player to a group with an optional expiration date.
     *
     * @param player     The player to add to the group.
     * @param group      The group to which the player is added.
     * @param expireDate The expiration date of the group assignment (null for no expiration).
     * @throws Exception If a database access error occurs.
     */
    public void addPlayerToGroup(Player player, UserGroup group, Timestamp expireDate) throws Exception {
        PlayerDAO playerDAO = getPlayersActiveGroup(player);
        final var player2Group = player2GroupRepository.save(new Player2Group(0, player, group, expireDate, new Timestamp(System.currentTimeMillis()), getLastUserGroupId(playerDAO)));
        addToCache(toDAO(player2Group));
    }
    public void addPlayerToGroup(Player player, UserGroup group) throws Exception {
        addPlayerToGroup(player, group, null);
    }

    /**
     * Checks whether a Player2Group association is active based on its expiration date.
     *
     * @param player2Group The Player2Group object to check.
     * @return True if the association is active, false otherwise.
     */
    public static boolean isActive(Player2Group player2Group) {
        if (player2Group.getExpireDate() == null) {
            return true;
        }
        return player2Group.getExpireDate().after(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Retrieves the last user group ID associated with the given PlayerDAO.
     * If the player is not null, returns the current group ID. Otherwise, returns 0.
     *
     * @param player The PlayerDAO instance whose last user group ID is to be retrieved.
     * @return Long representing the last user group ID or 0 if the player is null.
     */
    private Long getLastUserGroupId(PlayerDAO player) {
        if (player != null) {
            return player.getCurrentGroupId();
        }
        return 0L;
    }


    /**
     * Converts a Bukkit player object to a custom Player object.
     *
     * @param bukkitPlayer The Bukkit player object to convert.
     * @return A new Player object with the UUID and display name from the Bukkit player.
     */
    public Player toPlayer(org.bukkit.entity.Player bukkitPlayer) {
        return new Player(bukkitPlayer.getUniqueId().toString(), bukkitPlayer.getDisplayName());
    }

    /**
     * Converts a Player2Group object to a PlayerDAO object.
     * The conversion includes player UUID, group ID, display name, group name, group prefix,
     * expiration date, last group ID, and creation date.
     *
     * @param player2Group The Player2Group object to convert.
     * @return A new PlayerDAO object containing data from the Player2Group object.
     */
    public PlayerDAO toDAO(Player2Group player2Group) {
        return new PlayerDAO(
                player2Group.getPlayer().getUuid(),
                player2Group.getId(),
                player2Group.getPlayer().getDisplayName(),
                player2Group.getUserGroup().getId(),
                player2Group.getUserGroup().getGroupName(),
                player2Group.getUserGroup().getPrefix(),
                player2Group.getExpireDate(),
                player2Group.getLastGroupId(),
                player2Group.getCreateDate());
    }

    /**
     * Delete Player from current Cache by Players UUID
     *
     * @param id - Players UUID
     */
    public void deleteFromCache(String id) {
        playerCacheManager.removeById(id);
    }

    /**
     * Sends A Message to a specific User if the User is currently online
     *
     * @param player  The Player who should receive the Message
     * @param message the Message String which should be sent (colors also possible)
     */
    public void sendPlayerMsg(Player player, String message) {
        org.bukkit.entity.Player bukkitPlayer = Bukkit.getServer().getPlayer(player.getDisplayName());
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(message);
        }
    }
}
