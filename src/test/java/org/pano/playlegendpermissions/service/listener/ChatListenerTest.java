package org.pano.playlegendpermissions.service.listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class ChatListenerTest {
    @Mock
    private JavaPlugin javaPlugin;
    @Mock
    private PlayerService playerService;
    @Mock
    private AsyncPlayerChatEvent playerChatEvent;
    @Mock
    private org.bukkit.entity.Player bukkitPlayer;

    private ChatListener underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new ChatListener(playerService, javaPlugin);
    }

    @Test
    void onPlayerChat_WhenPlayerHasGroupWithPrefix_ShouldShowPrefix() throws Exception {
        //given
        final var player = createMockedPlayer();
        final var playerDAO = createMockPlayerDao();
        when(playerService.toPlayer(any())).thenReturn(player);
        when(playerService.getPlayersActiveGroup(player)).thenReturn(playerDAO);

        //when
        underTest.onPlayerChat(playerChatEvent);
        //then
        verify(playerChatEvent).setFormat(contains("[" + playerDAO.getCurrentGroupPrefix() + "] "));
    }

    @Test
    void onPlayerChat_WhenOperationFailed_ShouldCaptureException() {
        //given
        final var player = createMockedPlayer();
        when(playerChatEvent.getPlayer()).thenReturn(bukkitPlayer);
        when(bukkitPlayer.getName()).thenReturn(player.getDisplayName());
        when(playerService.toPlayer(any())).thenThrow(new RuntimeException());

        //when
        final NullPointerException thrown = Assertions.assertThrows(
                NullPointerException.class,
                () -> underTest.onPlayerChat(playerChatEvent)
        );

        //then
        verify(playerChatEvent, never()).setFormat(any());
        assertTrue(thrown.getMessage().contains("java.util.logging.Logger.warning(String)"));

    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 2L, "playerName", 3L, "groupName2", "PREFIX2", new Timestamp(4070905200L), 2L, new Timestamp(System.currentTimeMillis()));
    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }

}