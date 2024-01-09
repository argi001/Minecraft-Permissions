package org.pano.playlegendpermissions.service.listener;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pano.playlegendpermissions.model.Player;

import static org.mockito.Mockito.*;

class GroupSignListenerTest {

    @Mock
    private BlockPlaceEvent event;
    @Mock
    private org.bukkit.entity.Player bukkitPlayer;
    @Mock
    private ItemStack itemStack;
    @Mock
    private ItemMeta meta;
    @Mock
    private Block block;
    @Mock
    private Sign sign;
    @Mock
    private SignSide signSide;

    private GroupSignListener underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new GroupSignListener();
    }

    @Test
    void onSignPlace_WhenSignIsPlaced_ShouldEditText() {
        //given
        final var player = createMockedPlayer();
        when(event.getItemInHand()).thenReturn(itemStack);
        when(itemStack.getItemMeta()).thenReturn(meta);
        when(event.getPlayer()).thenReturn(bukkitPlayer);
        when(bukkitPlayer.getName()).thenReturn(player.getDisplayName());
        when(meta.getDisplayName()).thenReturn(player.getDisplayName() + " Gruppenschild");
        when(event.getBlockPlaced()).thenReturn(block);
        when(block.getState()).thenReturn(sign);
        when(sign.getSide(Side.FRONT)).thenReturn(signSide);
        //when
        underTest.onSignPlace(event);

        //then
        verify(sign, atLeastOnce()).update();

    }

    private Player createMockedPlayer() {
        return new Player("uuid", "displayName");
    }

}