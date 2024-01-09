package org.pano.playlegendpermissions.service.command.executor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.model.DAO.PlayerDAO;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;
import org.pano.playlegendpermissions.service.command.CommandOption;

import java.util.Optional;
import java.util.logging.Logger;

import static org.pano.playlegendpermissions.config.localization.MessageKey.*;

/**
 * Command executor for handling group-related commands in the PlaylegendPermissions plugin.
 */
public class GroupCommandExecutor implements CommandExecutor {


    private final UserGroupService userGroupService;
    private final PlayerService playerService;
    private final LocalizationManager localization;
    private final Logger logger;

    /**
     * Constructs a GroupCommandExecutor with the necessary services and localization manager.
     *
     * @param userGroupService The service for user group operations.
     * @param playerService    The service for player operations.
     * @param localization     The manager for localization and message formatting.
     * @param javaPlugin       The JavaPlugin instance.
     */
    public GroupCommandExecutor(UserGroupService userGroupService, PlayerService playerService, LocalizationManager localization, JavaPlugin javaPlugin) {
        this.userGroupService = userGroupService;
        this.playerService = playerService;
        this.localization = localization;
        this.logger = javaPlugin.getLogger();
    }

    /**
     * Handles group-related commands.
     *
     * @param sender  The entity who sent the command.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided with the command.
     * @return true if a valid command was executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        final Optional<CommandOption> commandOption = CommandOption.getSubCommandOption(args[0]);
        return commandOption.map(option -> switch (option) {
            case CREATE_GROUP -> handleCreateGroup(args, sender);
            case GROUP -> handleUserGroupCommand(args, sender);
            default -> handleDefault(sender);
        }).orElse(false);
    }

    private boolean handleUserGroupCommand(String[] args, CommandSender sender) {
        final Optional<CommandOption> commandOption = CommandOption.getSubCommandOption(args[2]);
        String groupName = args[1];
        return commandOption.map(option -> switch (option) {
            case SET_PREFIX -> handleSetPrefix(args, sender, groupName);
            case LIST_PLAYER -> handleListPlayer(sender, groupName);
            default -> handleDefault(sender);
        }).orElse(false);
    }

    private boolean handleDefault(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(UNKNOWN_COMMAND));
        return false;
    }

    private void sendPlayerInfo(CommandSender sender, PlayerDAO playerDAO) {
        sender.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_PLAYER) + ": " + ChatColor.WHITE + playerDAO.getName());
        sender.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_CURRENT_GROUP_SINCE) + ": " + ChatColor.WHITE + playerDAO.getCreateDate());
        if (playerDAO.getCurrentGroupExpire() != null) {
            sender.sendMessage(ChatColor.AQUA + localization.getFormattedMessage(PLAYER_INFO_RSP_CURRENT_GROUP_EXPIRE) + ": " + ChatColor.WHITE + playerDAO.getCurrentGroupExpire());
        }

        sender.sendMessage(ChatColor.GRAY + "--------------------------------");
    }

    private boolean handleListPlayer(CommandSender sender, String groupName) {
        try {
            final var players = userGroupService.getPlayerByGroupName(groupName);
            sender.sendMessage(ChatColor.GREEN + "===== " + ChatColor.YELLOW + groupName + ChatColor.GREEN + " =====");
            for (Player player : players) {
                final var playerDAO = playerService.getPlayersActiveGroup(player);
                if (playerDAO.getCurrentGroupName().equals(groupName)) {
                    sendPlayerInfo(sender, playerDAO);
                }
            }
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(PLAYER_INFO_ERROR));
            logger.warning("Could not get Group Information Error: " + e.getMessage());
            return false;
        }
    }

    private boolean handleSetPrefix(String[] args, CommandSender sender, final String groupName) {
        String prefix = args[3];
        try {
            userGroupService.updatePrefix(groupName, prefix);
            sender.sendMessage(ChatColor.GREEN + localization.getFormattedMessage(GROUP_UPDATE_PREFIX, groupName, prefix));
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(GROUP_UPDATE_PREFIX_ERROR));
            return false;
        }
    }

    private boolean handleCreateGroup(String[] args, CommandSender sender) {
        String groupName = args[1];
        String prefix = "";
        if (args.length > 2) {
            prefix = args[2];
        }
        try {
            userGroupService.create(new UserGroup(groupName, prefix));
            sender.sendMessage(ChatColor.GREEN + localization.getFormattedMessage(GROUP_CREATED, groupName));
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return false;
        }
    }


}
