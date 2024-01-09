package org.pano.playlegendpermissions.service.command;

import org.bukkit.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.service.cache.UserGroupCacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.pano.playlegendpermissions.service.command.CommandOption.INFO;
import static org.pano.playlegendpermissions.service.command.CommandOption.SIGN;

class PluginTabCompleterTest {
    @Mock
    private UserGroupCacheManager userGroupCacheManager;

    @Mock
    private org.bukkit.entity.Player bukkitPlayer;
    @Mock
    private Command command;
    private PluginTabCompleter underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PluginTabCompleter(userGroupCacheManager);
    }


    @Test
    void onTabComplete_WhenUserNonAdmin_ShouldReturnNonAdmin() {
        //given
        final String[] args = new String[]{
                CommandOption.INFO.getCommand(),

        };
        when(command.getName()).thenReturn("pper");
        when(bukkitPlayer.hasPermission("pper.administration")).thenReturn(false);

        //when
        final var result = underTest.onTabComplete(bukkitPlayer, command, "", args);

        //then
        assertEquals(result, List.of(INFO.getCommand(), SIGN.getCommand()));
    }
}