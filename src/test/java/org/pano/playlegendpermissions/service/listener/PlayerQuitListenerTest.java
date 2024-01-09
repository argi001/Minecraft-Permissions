package org.pano.playlegendpermissions.service.listener;

import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerQuitListenerTest {
    @Mock
    private PlayerQuitEvent event;
    @Mock
    private PlayerService playerService;

    private PlayerQuitListener underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PlayerQuitListener(playerService);
    }

    @Test
    void onPlayerQuit_WhenPlayerDisconnects_ShouldDeleteFromCache() {
        //given
        final var player = createMockedPlayer();
        when(playerService.toPlayer(any())).thenReturn(player);
        //when
        underTest.onPlayerQuit(event);

        //then
        verify(playerService, times(1)).deleteFromCache(player.getUuid());

    }


    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }


}