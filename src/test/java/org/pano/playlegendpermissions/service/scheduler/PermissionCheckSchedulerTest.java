package org.pano.playlegendpermissions.service.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.pano.playlegendpermissions.config.localization.MessageKey.PLAYER_NTFC_GROUP_EXPIRED;

class PermissionCheckSchedulerTest {
    @Mock
    private JavaPlugin javaPlugin;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerCacheManager playerCacheManager;
    @Mock
    private LocalizationManager localization;
    private PermissionCheckScheduler underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PermissionCheckScheduler(javaPlugin, playerCacheManager, playerService, localization);
    }

    @Test
    void run_WhenPlayerAreAvailable_ShouldUpdateCache() throws Exception {
        //Given
        final var playerDAO = createMockPlayerDaoExpired();
        final var activePlayerDao = createMockPlayerDao();
        when(playerCacheManager.getAll()).thenReturn(List.of(playerDAO));
        when(playerService.getPlayersActiveGroup(any())).thenReturn(activePlayerDao);
        when(localization.getFormattedMessage(PLAYER_NTFC_GROUP_EXPIRED, playerDAO.getCurrentGroupName(), activePlayerDao.getCurrentGroupName())).thenReturn(PLAYER_NTFC_GROUP_EXPIRED.getKey());
        //When
        underTest.run();

        //Then
        verify(playerService, times(1)).deleteFromCache(any());
        verify(playerService, times(1)).sendPlayerMsg(any(Player.class), eq(PLAYER_NTFC_GROUP_EXPIRED.getKey()));

    }

    private PlayerDAO createMockPlayerDaoExpired() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(978303600L), 1L, new Timestamp(System.currentTimeMillis()));
    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 2L, "playerName", 3L, "groupName2", "PREFIX2", new Timestamp(4070905200L), 2L, new Timestamp(System.currentTimeMillis()));
    }


}