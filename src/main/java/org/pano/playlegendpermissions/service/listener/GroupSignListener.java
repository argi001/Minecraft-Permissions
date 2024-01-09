package org.pano.playlegendpermissions.service.listener;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GroupSignListener implements Listener {
    @EventHandler
    public void onSignPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        final var signName = event.getPlayer().getName() + " Gruppenschild";
        if (meta != null && signName.equals(meta.getDisplayName())) {

            Block block = event.getBlockPlaced();
            if (block.getState() instanceof Sign sign) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    for (int i = 0; i < lore.size(); i++) {
                        sign.getSide(Side.FRONT).setLine(i, lore.get(i));
                    }
                    sign.setWaxed(true);
                    sign.getSide(Side.FRONT).setGlowingText(true);
                    sign.update();
                }
            }
        }
    }
}
