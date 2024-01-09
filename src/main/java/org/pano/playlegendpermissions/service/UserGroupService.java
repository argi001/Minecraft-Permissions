package org.pano.playlegendpermissions.service;

import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.config.localization.MessageKey;
import org.pano.playlegendpermissions.exceptions.GroupAlreadyExistsException;
import org.pano.playlegendpermissions.exceptions.GroupNotFoundException;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.PlayerRepository;
import org.pano.playlegendpermissions.repository.UserGroupRepository;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;
import org.pano.playlegendpermissions.service.cache.UserGroupCacheManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing user groups in the PlayLegendPermissions system.
 * This class provides methods to create, retrieve, and update user groups, along with retrieving players by group name.
 */
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserGroupCacheManager userGroupCacheManager;
    private final PlayerCacheManager playerCacheManager;
    private final PlayerRepository playerRepository;
    final LocalizationManager localization;

    /**
     * Constructs a UserGroupService with necessary dependencies.
     *
     * @param userGroupRepository   Repository for user group data access.
     * @param userGroupCacheManager Cache manager for user groups.
     * @param playerCacheManager    Cache manager for players.
     * @param playerRepository      Repository for player data access.
     * @param localization          Localization manager for internationalization.
     */
    public UserGroupService(UserGroupRepository userGroupRepository, UserGroupCacheManager userGroupCacheManager, PlayerCacheManager playerCacheManager, PlayerRepository playerRepository, LocalizationManager localization) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupCacheManager = userGroupCacheManager;
        this.playerCacheManager = playerCacheManager;
        this.playerRepository = playerRepository;
        this.localization = localization;
    }

    /**
     * Creates a new user group and adds it to the repository and cache.
     * If the group already exists, a GroupAlreadyExistsException is thrown.
     *
     * @param userGroup The user group to create.
     * @return The created user group.
     * @throws RuntimeException If an error occurs during the group creation.
     */
    public UserGroup create(UserGroup userGroup) throws GroupAlreadyExistsException {
        try {
            final var group = userGroupRepository.findFirstByName(userGroup.getGroupName());
            if (group != null) {
                throw new GroupAlreadyExistsException(localization.getFormattedMessage(MessageKey.GROUP_ALREADY_EXIST, userGroup.getGroupName()));
            }
            userGroup = userGroupRepository.save(userGroup);
            userGroupCacheManager.addToCache(userGroup);
            return userGroup;

        } catch (GroupAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a user group by its name.
     * If the group is not found, a GroupNotFoundException is thrown.
     *
     * @param groupName The name of the group to retrieve.
     * @return The retrieved user group.
     * @throws GroupNotFoundException If the group is not found.
     */
    public UserGroup getGroupByName(final String groupName) throws GroupNotFoundException {
        try {
            return userGroupCacheManager.getByName(groupName);
        } catch (Exception exception) {
            throw new GroupNotFoundException(localization.getFormattedMessage(MessageKey.GROUP_NOT_FOUND, groupName));
        }
    }

    /**
     * Updates the prefix of a specified group and reflects this change in the cache.
     *
     * @param groupName The name of the group to update.
     * @param prefix    The new prefix to set for the group.
     * @throws GroupNotFoundException If the group is not found.
     * @throws SQLException           If a SQL error occurs during the update.
     * @throws IllegalAccessException If an illegal access occurs during the update.
     */
    public void updatePrefix(final String groupName, final String prefix) throws GroupNotFoundException, SQLException, IllegalAccessException {
        UserGroup userGroup = getGroupByName(groupName);
        userGroup.setPrefix(prefix);
        userGroup = userGroupRepository.save(userGroup);
        updateCacheByGroup(userGroup);
    }

    /**
     * Retrieves a list of players belonging to a specified group.
     *
     * @param groupName The name of the group.
     * @return A list of players in the specified group.
     * @throws Exception If an error occurs during retrieval.
     */
    public List<Player> getPlayerByGroupName(final String groupName) throws Exception {
        final UserGroup userGroup = getGroupByName(groupName);
        return playerRepository.getPlayerHistoryInGroup(userGroup);
    }

    /**
     * Updates the cache with the latest information of a specific user group.
     *
     * @param userGroup The user group with updated information.
     */
    private void updateCacheByGroup(final UserGroup userGroup) {
        playerCacheManager.getAll().stream().filter(playerDAO -> playerDAO.getCurrentGroupName().equalsIgnoreCase(userGroup.getGroupName())).forEach(playerDAO -> {
            playerDAO.setCurrentGroupName(userGroup.getGroupName());
            playerDAO.setCurrentGroupId(userGroup.getId());
            playerDAO.setCurrentGroupPrefix(userGroup.getPrefix());
            playerCacheManager.addToCache(playerDAO);
        });
    }


}
