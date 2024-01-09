package org.pano.playlegendpermissions.service.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.exceptions.PlayerNotFoundException;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;

import java.sql.Timestamp;

class PlayerCacheManagerTest {

    private PlayerCacheManager underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PlayerCacheManager();
    }

    @Test
    void loadIntoCache_CachePresent_ShouldNotThrow() {
        //given
        final var playerDAO = createMockPlayerDao();

        //when//then
        underTest.getById(playerDAO.getUuid());
    }

    @Test
    void getByName_WhenPlayerNotFound_ShouldThrow() {
        //given
        final var playerDAO = createMockPlayerDao();

        //when //then
        Assertions.assertThrows(
                PlayerNotFoundException.class,
                () -> underTest.getByName(playerDAO.getUuid())
        );
    }

    private PlayerDAO createMockPlayerDao() {
        return new PlayerDAO("uuid", 1L, "playerName", 2L, "groupName", "PREFIX", new Timestamp(System.currentTimeMillis()), 1L, new Timestamp(System.currentTimeMillis()));
    }
}