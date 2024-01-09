package org.pano.playlegendpermissions.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.VisibleForTesting;
import jdk.jshell.spi.ExecutionControl;
import org.pano.playlegendpermissions.exceptions.PlayerNotFoundException;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the CacheableObject Interface for Player (PlayerDAO)
 */
public class PlayerCacheManager implements CacheableObject<PlayerDAO, String> {
    private final static int MAX_SIZE = 10000;
    private final static int EXPIRE_IN_MIN = 60;
    private final Cache<String, PlayerDAO> playerCache;


    public PlayerCacheManager() {
        this.playerCache = Caffeine.newBuilder()
                .expireAfterWrite(EXPIRE_IN_MIN, TimeUnit.MINUTES)
                .maximumSize(MAX_SIZE)
                .build();
    }
    @VisibleForTesting
    protected PlayerCacheManager(Cache<String, PlayerDAO> playerCache) {
        this.playerCache= playerCache;
    }

    @Override
    public void loadIntoCache() throws Exception {
        throw new ExecutionControl.NotImplementedException("This Fuction is not implemented, please load Cache on runtime");
    }

    @Override
    public PlayerDAO getById(String id) {
        return playerCache.getIfPresent(id);
    }

    @Override
    public PlayerDAO getByName(String name) throws PlayerNotFoundException {
        return playerCache.asMap().values().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst().orElseThrow(PlayerNotFoundException::new);
    }

    @Override
    public List<PlayerDAO> getAll() {
        return playerCache.asMap().values().stream().toList();
    }

    @Override
    public void addToCache(PlayerDAO player) {
        playerCache.put(player.getUuid(), player);
    }

    @Override
    public void removeById(String id) {
        playerCache.invalidate(id);
    }
}
