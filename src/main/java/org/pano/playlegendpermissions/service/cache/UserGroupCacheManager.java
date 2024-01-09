package org.pano.playlegendpermissions.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.VisibleForTesting;
import org.pano.playlegendpermissions.exceptions.GroupNotFoundException;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.UserGroupRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the CacheableObject Interface for UserGroups
 */
public class UserGroupCacheManager implements CacheableObject<UserGroup, Long> {
    private final static int MAX_SIZE = 1000;
    private final static int EXPIRE_IN_MIN = 60;
    private final Cache<Long, UserGroup> userGroupCache;
    private final UserGroupRepository userGroupRepository;

    public UserGroupCacheManager(final UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;

        this.userGroupCache = Caffeine.newBuilder()
                .expireAfterWrite(EXPIRE_IN_MIN, TimeUnit.MINUTES)
                .maximumSize(MAX_SIZE)
                .build(userGroupRepository::findById);

    }
    @VisibleForTesting
    protected UserGroupCacheManager(final UserGroupRepository userGroupRepository, Cache<Long, UserGroup> userGroupCache) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupCache = userGroupCache;
    }

    @Override
    public void loadIntoCache() throws Exception {
        userGroupRepository.findAll().forEach(this::addToCache);
    }

    @Override
    public UserGroup getById(Long id) {
        return userGroupCache.getIfPresent(id);
    }

    @Override
    public UserGroup getByName(String name) throws Exception {
        try {
            return userGroupCache.asMap().values().stream()
                    .filter(group -> group.getGroupName().equals(name))
                    .findFirst().orElseThrow(GroupNotFoundException::new);
        } catch (GroupNotFoundException e) {
            UserGroup userGroup = userGroupRepository.findFirstByName(name);
            if (userGroup != null) {
                addToCache(userGroup);
                return userGroup;
            }
            throw e;
        }
    }

    @Override
    public List<UserGroup> getAll() {
        return userGroupCache.asMap().values().stream().toList();
    }

    @Override
    public void addToCache(UserGroup userGroup) {
        userGroupCache.put(userGroup.getId(), userGroup);
    }

    @Override
    public void removeById(Long id) {
        userGroupCache.invalidate(id);
    }
}
