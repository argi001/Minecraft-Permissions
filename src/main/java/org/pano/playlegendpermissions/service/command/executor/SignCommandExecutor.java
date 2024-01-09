package org.pano.playlegendpermissions.service.command.executor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.service.PlayerService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.pano.playlegendpermissions.config.localization.MessageKey.PLAYER_ONLY_ERROR;
import static org.pano.playlegendpermissions.config.localization.MessageKey.SIGN_GENERATE_ERROR;

/**
 * Command executor for handling sign command in the PlaylegendPermissions plugin.
 */
public class SignCommandExecutor implements CommandExecutor {


    final PlayerService playerService;
    final LocalizationManager localization;

    /**
     * Constructs a SignCommandExecutor with the necessary services and localization manager.
     *
     * @param playerService The service for player operations.
     * @param localization  The manager for localization and message formatting.
     */
    public SignCommandExecutor(PlayerService playerService, LocalizationManager localization) {
        this.playerService = playerService;
        this.localization = localization;
    }

    /**
     * Handles the sign command. Which drops the Player a unique Sign Item
     *
     * @param sender  The entity who sent the command.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided with the command.
     * @return true if a valid command was executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("sign")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(localization.getFormattedMessage(PLAYER_ONLY_ERROR));
                return true;
            }

            try {
                final var groupName = playerService.getPlayersActiveGroup(playerService.toPlayer(player)).getCurrentGroupName();
                ItemStack sign = createUserGroupSign(player.getName(), groupName);
                player.getInventory().addItem(sign);
                return true;
            } catch (Exception e) {
                sender.sendMessage(localization.getFormattedMessage(SIGN_GENERATE_ERROR));
                return false;
            }

        }
        return true;
    }

    private ItemStack createUserGroupSign(String playerName, String groupName) {
        ItemStack sign = new ItemStack(Material.OAK_SIGN, 1);
        sign.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
        ItemMeta meta = sign.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setDisplayName(playerName + " Gruppenschild");
            List<String> lore = new ArrayList<>();
            lore.add("[" + playerName + "]");
            lore.add("[" + groupName + "]");
            lore.add("[" + getCurrentTimeStamp() + "]");
            meta.setLore(lore);
            sign.setItemMeta(meta);
            return sign;
        }
        return null;
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        return sdfDate.format(new Date());
    }


}
