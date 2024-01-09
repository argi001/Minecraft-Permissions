package org.pano.playlegendpermissions.service.command.executor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.service.PlayerService;

import static org.pano.playlegendpermissions.config.localization.MessageKey.*;

/**
 * Command executor for handling user-info-related commands in the PlaylegendPermissions plugin.
 */
public class InfoCommandExecutor implements CommandExecutor {
    final PlayerService playerService;
    final LocalizationManager localization;

    /**
     * Constructs a InfoCommandExecutor with the necessary services and localization manager.
     *
     * @param playerService The service for player operations.
     * @param localization  The manager for localization and message formatting.
     */
    public InfoCommandExecutor(PlayerService playerService, LocalizationManager localization) {
        this.playerService = playerService;
        this.localization = localization;
    }

    /**
     * Handles info-related commands.
     *
     * @param sender The entity who sent the command.
     * @param command The command being executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return true if a valid command was executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("info") && sender instanceof Player) {
            try {

                sendPlayerInfo((Player) sender, playerService.getPlayersActiveGroup(playerService.toPlayer((Player) sender)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(PLAYER_INFO_ERROR));
            }
        }
        return true;
    }

    private void sendPlayerInfo(Player player, PlayerDAO playerDAO) {

        player.sendMessage(ChatColor.GREEN + "===== " + ChatColor.YELLOW + localization.getFormattedMessage(PLAYER_INFO_RSP_HEADER) + ChatColor.GREEN + " =====");
        player.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_PLAYER) + ": " + ChatColor.WHITE + playerDAO.getName());
        player.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_PLAYER_ID) + ": " + ChatColor.WHITE + playerDAO.getUuid());
        player.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_CURRENT_GROUP) + ": " + ChatColor.WHITE + playerDAO.getCurrentGroupName());
        player.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_CURRENT_GROUP_SINCE) + ": " + ChatColor.WHITE + playerDAO.getCreateDate());
        if (playerDAO.getCurrentGroupExpire() != null) {
            player.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_CURRENT_GROUP_EXPIRE) + ": " + ChatColor.WHITE + playerDAO.getCurrentGroupExpire());
        }

        player.sendMessage(ChatColor.GRAY + "--------------------------------");
    }


}
