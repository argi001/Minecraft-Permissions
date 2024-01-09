package org.pano.playlegendpermissions.service.command;

import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PluginCommandExecutorTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private UserGroupService userGroupService;
    @Mock
    private LocalizationManager localization;
    @Mock
    private JavaPlugin javaPlugin;
    @Mock
    private org.bukkit.entity.Player bukkitPlayer;
    @Mock
    private Command command;
    @Mock
    private UUID uuid;

    private PluginCommandExecutor underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PluginCommandExecutor(javaPlugin, userGroupService, playerService, localization);
    }

    @Test
    void onCommand_WhenCommandInfo_ShouldReturnTrue() throws Exception {
        //given
        final String[] args = new String[]{
                CommandOption.INFO.getCommand()
        };
        final var playerDAO = createMockPlayerDao();
        when(playerService.getPlayersActiveGroup(any())).thenReturn(playerDAO);

        //when
        final var result = underTest.onCommand(bukkitPlayer, command, "", args);

        //then
        assertTrue(result);
        verify(playerService, times(1)).getPlayersActiveGroup(any());

    }

    @Test
    void onCommand_WhenCommandUserSetGroup_ShouldReturnTrue() throws Exception {
        //given
        final var playerDAO = createMockPlayerDao();
        final var player = createMockedPlayer();
        final var userGroup = createMockUserGroup();
        final String[] args = new String[]{
                CommandOption.USER.getCommand(),
                player.getDisplayName(),
                CommandOption.SET_GROUP.getCommand(),
                userGroup.getGroupName()

        };

        when(playerService.getPlayersActiveGroup(any())).thenReturn(playerDAO);
        when(bukkitPlayer.hasPermission("pper.administration")).thenReturn(true);
        when(playerService.getPlayer(any())).thenReturn(player);
        when(userGroupService.getGroupByName(userGroup.getGroupName())).thenReturn(userGroup);
        when(bukkitPlayer.getUniqueId()).thenReturn(uuid);
        when(uuid.toString()).thenReturn(player.getUuid());


        //when
        final var result = underTest.onCommand(bukkitPlayer, command, "", args);

        //then
        assertTrue(result);
        verify(playerService, times(1)).addPlayerToGroup(player, userGroup);

    }

    @Test
    void onCommand_WhenCommandUserSetGroupWithExpireDate_ShouldReturnTrue() throws Exception {
        //given
        final var playerDAO = createMockPlayerDao();
        final var player = createMockedPlayer();
        final var userGroup = createMockUserGroup();
        final String[] args = new String[]{
                CommandOption.USER.getCommand(),
                player.getDisplayName(),
                CommandOption.SET_GROUP.getCommand(),
                userGroup.getGroupName(),
                "1d", "1h", "1m", "1s"

        };

        when(playerService.getPlayersActiveGroup(any())).thenReturn(playerDAO);
        when(bukkitPlayer.hasPermission("pper.administration")).thenReturn(true);
        when(playerService.getPlayer(any())).thenReturn(player);
        when(userGroupService.getGroupByName(userGroup.getGroupName())).thenReturn(userGroup);
        when(bukkitPlayer.getUniqueId()).thenReturn(uuid);
        when(uuid.toString()).thenReturn(player.getUuid());


        //when
        final var result = underTest.onCommand(bukkitPlayer, command, "", args);

        //then
        assertTrue(result);
        verify(playerService, times(1)).addPlayerToGroup(any(), any(), any());

    }

    @Test
    void onCommand_WhenCommandSetGroupPrefix_ShouldReturnTrue() throws Exception {
        //given
        final var userGroup = createMockUserGroup();
        final String[] args = new String[]{
                CommandOption.GROUP.getCommand(),
                userGroup.getGroupName(),
                CommandOption.SET_PREFIX.getCommand(),
                userGroup.getPrefix()
        };

        when(bukkitPlayer.hasPermission("pper.administration")).thenReturn(true);
        when(userGroupService.getGroupByName(userGroup.getGroupName())).thenReturn(userGroup);


        //when
        final var result = underTest.onCommand(bukkitPlayer, command, "", args);

        //then
        assertTrue(result);
        verify(userGroupService, times(1)).updatePrefix(userGroup.getGroupName(), userGroup.getPrefix());

    }

    @Test
    void onCommand_WhenCommandListPlayer_ShouldReturnTrue() throws Exception {
        //given
        final var userGroup = createMockUserGroup();
        final var player = createMockedPlayer();
        final var playerDAO = createMockPlayerDao();
        final String[] args = new String[]{
                CommandOption.GROUP.getCommand(),
                userGroup.getGroupName(),
                CommandOption.LIST_PLAYER.getCommand()
        };
        when(userGroupService.getPlayerByGroupName(userGroup.getGroupName())).thenReturn(List.of(player));
        when(playerService.getPlayersActiveGroup(player)).thenReturn(playerDAO);
        when(bukkitPlayer.hasPermission("pper.administration")).thenReturn(true);
        when(userGroupService.getGroupByName(userGroup.getGroupName())).thenReturn(userGroup);


        //when
        final var result = underTest.onCommand(bukkitPlayer, command, "", args);

        //then
        assertTrue(result);
        verify(bukkitPlayer, times(5)).sendMessage(any(String.class));

    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(System.currentTimeMillis()), 1L, new Timestamp(System.currentTimeMillis()));
    }

    private UserGroup createMockUserGroup() {
        return new UserGroup(2L, "groupName", "PREFIX");
    }


}