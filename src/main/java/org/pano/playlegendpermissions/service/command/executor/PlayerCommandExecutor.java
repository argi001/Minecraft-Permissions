package org.pano.playlegendpermissions.service.command.executor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;
import org.pano.playlegendpermissions.service.command.CommandOption;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static org.pano.playlegendpermissions.config.localization.MessageKey.*;

/**
 * Command executor for handling user (player)-related commands in the PlaylegendPermissions plugin.
 */
public class PlayerCommandExecutor implements CommandExecutor {

    private final static String DAY_IDENTIFIER = "d";
    private final static String HOUR_IDENTIFIER = "h";
    private final static String MINUTE_IDENTIFIER = "m";
    private final static String SECOND_IDENTIFIER = "s";
    private final Logger logger;
    private final UserGroupService userGroupService;
    private final PlayerService playerService;
    final LocalizationManager localization;

    /**
     * Constructs a PlayerCommandExecutor with the necessary services and localization manager.
     *
     * @param userGroupService The service for user group operations.
     * @param playerService    The service for player operations.
     * @param localization     The manager for localization and message formatting.
     * @param javaPlugin       The JavaPlugin instance.
     */
    public PlayerCommandExecutor(JavaPlugin javaPlugin, UserGroupService userGroupService, PlayerService playerService, LocalizationManager localization) {
        this.userGroupService = userGroupService;
        this.logger = javaPlugin.getLogger();

        this.playerService = playerService;
        this.localization = localization;
    }

    /**
     * Handles user-related commands.
     *
     * @param sender  The entity who sent the command.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided with the command.
     * @return true if a valid command was executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 2) {
            try {
                Player player = playerService.getPlayer(new Player("", (args[1])));
                var commandOption = CommandOption.getSubCommandOption(args[2].toLowerCase());
                if (player != null && commandOption.isPresent()) {
                    switch (commandOption.get()) {
                        case SET_GROUP:
                            return handleSetUserGroup(sender, player, args);
                        case INFO:
                            return handleInfoUser();
                        default:
                            sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(UNKNOWN_COMMAND));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(UNKNOWN_PLAYER));
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(PLAYER_EDIT_ERROR, e.getMessage()));
            }
        }
        return false;
    }

    private boolean handleInfoUser() {
        return true;
    }

    private boolean handleSetUserGroup(final CommandSender sender, final Player player, final String[] args) {
        try {
            final UserGroup userGroup = userGroupService.getGroupByName(args[3]);

            if (userGroup != null) {
                if (args.length == 4) {
                    playerService.addPlayerToGroup(player, userGroup);
                    sendExecutionConfirmation(sender, player, userGroup, null);
                } else {
                    Timestamp expireDate = new Timestamp(System.currentTimeMillis());
                    var i = 4;
                    while (args.length > i) {
                        if (args[i].toLowerCase().contains(DAY_IDENTIFIER)) {
                            expireDate = addTimeToTimestamp(expireDate, Integer.parseInt(args[i].replace(DAY_IDENTIFIER, "")), 0, 0, 0);
                        } else if (args[i].toLowerCase().contains(HOUR_IDENTIFIER)) {
                            expireDate = addTimeToTimestamp(expireDate, 0, Integer.parseInt(args[i].replace(HOUR_IDENTIFIER, "")), 0, 0);
                        } else if (args[i].toLowerCase().contains(MINUTE_IDENTIFIER)) {
                            expireDate = addTimeToTimestamp(expireDate, 0, 0, Integer.parseInt(args[i].replace(MINUTE_IDENTIFIER, "")), 0);
                        } else if (args[i].toLowerCase().contains(SECOND_IDENTIFIER)) {
                            expireDate = addTimeToTimestamp(expireDate, 0, 0, 0, Integer.parseInt(args[i].replace(SECOND_IDENTIFIER, "")));
                        } else {
                            sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(UNKNOWN_DATE_FORMAT));
                        }
                        i++;
                    }
                    playerService.addPlayerToGroup(player, userGroup, expireDate);
                    sendExecutionConfirmation(sender, player, userGroup, expireDate);

                }
            }
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
            logger.warning(exception.getMessage());
        }
        return true;
    }

    private Timestamp addTimeToTimestamp(Timestamp originalTimestamp, int days, int hours, int minutes, int seconds) {
        LocalDateTime localDateTime = originalTimestamp.toLocalDateTime();
        localDateTime = localDateTime.plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
        return Timestamp.valueOf(localDateTime);
    }

    private void sendExecutionConfirmation(CommandSender sender, Player player, UserGroup userGroup, Timestamp expireDateTime) {
        String adminMessage;
        String playerNtfc;
        if (expireDateTime == null) {
            adminMessage = localization.getFormattedMessage(PLAYER_ADD_ADMIN_RESPONSE, player.getDisplayName(), userGroup.getGroupName());
            playerNtfc = localization.getFormattedMessage(PLAYER_NTFC_ADDED_TO_GROUP, player.getDisplayName(), userGroup.getGroupName());
        } else {
            adminMessage = localization.getFormattedMessage(PLAYER_ADD_WITH_EXPIRE_ADMIN_RESPONSE, player.getDisplayName(), userGroup.getGroupName(), expireDateTime);
            playerNtfc = localization.getFormattedMessage(PLAYER_NTFC_ADDED_TO_GROUP_WITH_EXPIRE, expireDateTime, userGroup.getGroupName());
        }
        boolean notifyPlayer = true;
        if (sender instanceof org.bukkit.entity.Player senderPlayer) {
            if (senderPlayer.getUniqueId().toString().equals(player.getUuid())) {
                notifyPlayer = false;
            }
        }
        if (notifyPlayer) {
            playerService.sendPlayerMsg(player, ChatColor.GREEN + playerNtfc);
        }
        sender.sendMessage(ChatColor.GREEN + adminMessage);
    }
}
