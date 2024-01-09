package org.pano.playlegendpermissions.service.listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.config.localization.MessageKey;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;

import java.sql.Timestamp;

import static org.mockito.Mockito.*;

class PlayerJoinListenerTest {

    @Mock
    private PlayerJoinEvent event;
    @Mock
    private org.bukkit.entity.Player bukkitPlayer;
    @Mock
    private PlayerService playerService;
    @Mock
    private UserGroupService userGroupService;
    @Mock
    private LocalizationManager localization;
    @Mock
    private JavaPlugin javaPlugin;


    private PlayerJoinListener underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PlayerJoinListener(playerService, userGroupService, localization, javaPlugin);
    }

    @Test
    void onPlayerJoin_WhenPlayerIsInDatabase_ShouldWriteToCache() throws Exception {
        //given
        final var player = createMockedPlayer();
        final var playerDao = createMockPlayerDao();
        final var prefixString = "[" + playerDao.getCurrentGroupPrefix() + "]";
        when(playerService.toPlayer(any())).thenReturn(player);
        when(playerService.getPlayer(any())).thenReturn(player);
        when(playerService.getPlayersActiveGroup(player)).thenReturn(playerDao);
        when(localization.getFormattedMessage(MessageKey.JOIN_MESSAGE, prefixString, playerDao.getName(), playerDao.getCurrentGroupName())).thenReturn(MessageKey.JOIN_MESSAGE.getKey());
        //when
        underTest.onPlayerJoin(event);

        //then
        verify(event, times(1)).setJoinMessage(contains(MessageKey.JOIN_MESSAGE.getKey()));

    }

    @Test
    void onPlayerJoin_WhenPlayerIsNotInDatabase_ShouldAddToDBAndAddToDefaultGroup() throws Exception {
        //given
        final var player = createMockedPlayer();
        final var playerDao = createMockPlayerDao();
        final var prefixString = "[" + playerDao.getCurrentGroupPrefix() + "]";
        when(event.getPlayer()).thenReturn(bukkitPlayer);
        when(playerService.toPlayer(bukkitPlayer)).thenReturn(player);
        when(playerService.getPlayer(any())).thenReturn(null, player);
        when(playerService.getPlayersActiveGroup(player)).thenReturn(null, playerDao);
        when(localization.getFormattedMessage(MessageKey.JOIN_MESSAGE, prefixString, playerDao.getName(), playerDao.getCurrentGroupName())).thenReturn(MessageKey.JOIN_MESSAGE.getKey());
        //when
        underTest.onPlayerJoin(event);

        //then
        verify(event, times(1)).setJoinMessage(contains(MessageKey.JOIN_MESSAGE.getKey()));

    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(System.currentTimeMillis()), 1L, new Timestamp(System.currentTimeMillis()));
    }

}