package org.pano.playlegendpermissions.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.config.localization.MessageKey;
import org.pano.playlegendpermissions.exceptions.GroupAlreadyExistsException;
import org.pano.playlegendpermissions.exceptions.GroupNotFoundException;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.PlayerRepository;
import org.pano.playlegendpermissions.repository.UserGroupRepository;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;
import org.pano.playlegendpermissions.service.cache.UserGroupCacheManager;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.pano.playlegendpermissions.config.localization.MessageKey.GROUP_ALREADY_EXIST;

class UserGroupServiceTest {
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private UserGroupCacheManager userGroupCacheManager;
    @Mock
    private LocalizationManager localization;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerCacheManager playerCacheManager;
    private UserGroupService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new UserGroupService(userGroupRepository, userGroupCacheManager, playerCacheManager, playerRepository, localization);
    }

    @Test
    void create_WhenValidUserGroupAndNotExists_ShouldWriteToDbAndToCache() throws Exception {
        //given
        final var group = createMockUserGroup();
        when(userGroupRepository.findFirstByName(group.getGroupName())).thenReturn(null);
        when(userGroupRepository.save(group)).thenReturn(group);

        //when
        final var result = underTest.create(group);

        //then
        verify(userGroupCacheManager, times(1)).addToCache(any());
        assertNotNull(result);
        assertEquals(group.getGroupName(), result.getGroupName());
    }

    @Test
    void create_WhenValidUserGroupAndAlreadyExists_ShouldThrow() throws Exception {
        //given
        final var group = createMockUserGroup();
        when(userGroupRepository.findFirstByName(group.getGroupName())).thenReturn(group);
        when(localization.getFormattedMessage(GROUP_ALREADY_EXIST, group.getGroupName())).thenReturn("group_already_exist");
        //when
        final GroupAlreadyExistsException thrown = Assertions.assertThrows(
                GroupAlreadyExistsException.class,
                () -> underTest.create(group)
        );

        //then
        verify(userGroupCacheManager, never()).addToCache(any());
        assertEquals(GROUP_ALREADY_EXIST.getKey(), thrown.getMessage());
    }

    @Test
    void getGroupByName_WhenNameExists_ShouldReturnGroup() throws Exception {
        //given
        final var group = createMockUserGroup();
        when(userGroupCacheManager.getByName(any())).thenReturn(group);

        //when
        final var result = underTest.getGroupByName("groupName");

        //then
        assertNotNull(result);
        assertEquals("groupName", result.getGroupName());
    }

    @Test
    void getGroupByName_WhenGroupNotExists_ShouldThrow() throws Exception {
        //given
        final var groupName = "trymacs";
        when(userGroupCacheManager.getByName(any())).thenThrow(new GroupNotFoundException());
        when(localization.getFormattedMessage(MessageKey.GROUP_NOT_FOUND, groupName)).thenReturn("group_already_exist");

        //when
        final GroupNotFoundException thrown = Assertions.assertThrows(
                GroupNotFoundException.class,
                () -> underTest.getGroupByName(groupName)
        );

        //then
        assertEquals(GROUP_ALREADY_EXIST.getKey(), thrown.getMessage());
    }

    @Test
    void updatePrefix_WhenGroupExists_ShouldUpdatePrefix() throws Exception {
        //given
        final var group = createMockUserGroup();
        when(userGroupCacheManager.getByName(any())).thenReturn(group);
        when(userGroupRepository.save(group)).thenReturn(group);
        when(playerCacheManager.getAll()).thenReturn(List.of(createMockPlayerDao()));
        //when
        underTest.updatePrefix("groupName", "PREFIX");

        //then
        verify(playerCacheManager, times(1)).addToCache(any());
    }

    @Test
    void getPlayerByGroupName_WhenGroupIsAvailable_ShouldReturnPlayers() throws Exception {
        //given
        final var group = createMockUserGroup();
        final var player = createMockedPlayer();
        when(userGroupCacheManager.getByName(any())).thenReturn(group);
        when(playerRepository.getPlayerHistoryInGroup(any())).thenReturn(List.of(player));

        //when
        final var result = underTest.getPlayerByGroupName("groupName");

        //then
        assertNotNull(result);
        assertEquals("uuid", result.get(0).getUuid());
    }

    private UserGroup createMockUserGroup() {
        return new UserGroup(2L, "groupName", "PREFIX");
    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(System.currentTimeMillis()), 1L, new Timestamp(System.currentTimeMillis()));
    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }


}