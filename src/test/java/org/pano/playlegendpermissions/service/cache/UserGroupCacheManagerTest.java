package org.pano.playlegendpermissions.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.exceptions.GroupNotFoundException;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.repository.UserGroupRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserGroupCacheManagerTest {
    @Mock
    private Cache<Long, UserGroup> userGroupCache;
    @Mock
    private UserGroupRepository userGroupRepository;

    private UserGroupCacheManager underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new UserGroupCacheManager(userGroupRepository, userGroupCache);
    }

    @Test
    void loadIntoCache_WhenCachePresent_ShouldNotThrow() {
        //given
        final var userGroup = createMockUserGroup();
        when(userGroupCache.getIfPresent(userGroup.getId())).thenReturn(userGroup);

        //when
        final var result = underTest.getById(userGroup.getId());

        //then
        assertEquals(result.getGroupName(), userGroup.getGroupName());
    }

    @Test
    void loadIntoCache_CachePresent_ShouldNotThrow() throws Exception {
        //given
        final var userGroup = createMockUserGroup();
        when(userGroupRepository.findAll()).thenReturn(List.of(userGroup));

        //when//then
        underTest.loadIntoCache();
    }

    @Test
    void getByName_CachePresent_ShouldReturnPlayer() throws Exception {
        //given
        final var userGroup = createMockUserGroup();
        ConcurrentMap<Long, UserGroup> map = new ConcurrentHashMap<>();
        map.put(userGroup.getId(), userGroup);
        when(userGroupCache.asMap()).thenReturn(map);

        //when
        final var result = underTest.getByName(userGroup.getGroupName());

        //then
        assertEquals(result.getGroupName(), userGroup.getGroupName());
    }

    @Test
    void getByName_WhenGroupNotFound_ShouldThrow() {
        //given
        final var userGroup = createMockUserGroup();
        ConcurrentMap<Long, UserGroup> map = new ConcurrentHashMap<>();
        when(userGroupCache.asMap()).thenReturn(map);

        //when //then
        Assertions.assertThrows(
                GroupNotFoundException.class,
                () -> underTest.getByName(userGroup.getGroupName())
        );
    }

    private UserGroup createMockUserGroup() {
        return new UserGroup(2L, "groupName", "PREFIX");
    }
}