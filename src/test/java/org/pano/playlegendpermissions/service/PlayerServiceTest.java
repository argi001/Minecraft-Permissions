package org.pano.playlegendpermissions.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.Player2Group;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.Player2GroupRepository;
import org.pano.playlegendpermissions.repository.PlayerRepository;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private Player2GroupRepository player2GroupRepository;
    @Mock
    private PlayerCacheManager playerCacheManager;
    @Mock
    private org.bukkit.entity.Player bukkitPlayer;

    private PlayerService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PlayerService(playerRepository, player2GroupRepository, playerCacheManager);
    }

    @Test
    void getPlayerWhenUuidProvided() throws Exception {
        // Arrange
        Player mockPlayer = new Player("uuid", "displayName");
        when(playerRepository.findById("uuid")).thenReturn(mockPlayer);

        // Act
        Player result = underTest.getPlayer(new Player("uuid", ""));

        // Assert
        assertNotNull(result);
        assertEquals("uuid", result.getUuid());
    }

    @Test
    void getPlayerWhenDisplayNameProvided() throws Exception {
        // Arrange
        Player mockPlayer = new Player("uuid", "displayName");
        when(playerRepository.findFirstByName("displayName")).thenReturn(mockPlayer);

        // Act
        Player result = underTest.getPlayer(new Player("", "displayName"));

        // Assert
        assertNotNull(result);
        assertEquals("displayName", result.getDisplayName());
    }


    @Test
    void addToCache_WhenPlayerAlreadyInCache_ShouldDeleteBefore() {
        //given
        final var playerDAO = createMockPlayerDao();
        when(playerCacheManager.getById(playerDAO.getUuid())).thenReturn(playerDAO);

        //when
        underTest.addToCache(playerDAO);

        //then
        verify(playerCacheManager, times(1)).removeById(playerDAO.getUuid());
    }

    @Test
    void getPlayersActiveGroup_WhenPlayerInCache_ShouldReturnDao() throws Exception {
        //given
        final var playerDAO = createMockPlayerDao();
        final var player = createMockedPlayer();
        when(playerCacheManager.getById(player.getUuid())).thenReturn(playerDAO);

        //when
        final var result = underTest.getPlayersActiveGroup(player);

        //then
        assertNotNull(result);
        assertEquals("groupName", result.getCurrentGroupName());

    }

    @Test
    void getPlayersActiveGroup_WhenPlayerNotInCache_ShouldAddToCache() throws Exception {
        //given
        final var playerDAO = createMockPlayerDao();
        final var player = createMockedPlayer();
        final var playersList = createPlayer2Groups();

        when(playerCacheManager.getById(player.getUuid())).thenReturn(null);
        when(player2GroupRepository.findAllByPlayer(player)).thenReturn(playersList);


        //when
        final var result = underTest.getPlayersActiveGroup(player);

        //then
        assertNotNull(result);
        assertEquals(playerDAO.getUuid(), result.getUuid());
        assertEquals(playerDAO.getCurrentGroupId(), result.getCurrentGroupId());
        verify(playerCacheManager, times(1)).addToCache(any());

    }

    @Test
    void savePlayer_WhenPlayerValid_shouldSaveToDB() throws Exception {
        //given
        final var player = createMockedPlayer();
        when(playerRepository.save(player)).thenReturn(player);

        //when
        final var result = underTest.savePlayer(player);

        //then
        assertNotNull(result);
        assertEquals("uuid", result.getUuid());
    }

    @Test
    void addPlayerToGroup_WhenNoExpireDate_ShouldAddWithoutExpireDate() throws Exception {
        //given
        final var player = createMockedPlayer();
        final var group = new UserGroup(2L, "groupName", "PREFIX");
        when(player2GroupRepository.save(any())).thenReturn(createPlayer2Groups().get(0));

        //when
        underTest.addPlayerToGroup(player, group);

        //then
        verify(playerCacheManager, times(1)).addToCache(any());
    }

    @Test
    void toPlayer_WhenValidBukkitPlayer_ShouldConvert() {
        //given
        final var player = createMockedPlayer();
        when(bukkitPlayer.getUniqueId()).thenReturn(new UUID(1293L, 12L));
        when(bukkitPlayer.getDisplayName()).thenReturn("displayName");


        //when
        final var result = underTest.toPlayer(bukkitPlayer);

        //then
        assertNotNull(result);
        assertEquals("00000000-0000-050d-0000-00000000000c", result.getUuid());
        assertEquals(player.getDisplayName(), result.getDisplayName());
    }


    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(System.currentTimeMillis()), 1L, new Timestamp(System.currentTimeMillis()));
    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }

    private List<Player2Group> createPlayer2Groups() {
        return List.of(
                new Player2Group(2L, createMockedPlayer(), new UserGroup(1L, "userGroup1", "PREFIX"), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), 0L),
                new Player2Group(1L, createMockedPlayer(), new UserGroup(2L, "userGroup", "PREFIX"), null, new Timestamp(System.currentTimeMillis()), 1L)
        );
    }
}
