package org.pano.playlegendpermissions.service.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;
import org.pano.playlegendpermissions.service.command.executor.GroupCommandExecutor;
import org.pano.playlegendpermissions.service.command.executor.InfoCommandExecutor;
import org.pano.playlegendpermissions.service.command.executor.PlayerCommandExecutor;
import org.pano.playlegendpermissions.service.command.executor.SignCommandExecutor;

import static org.pano.playlegendpermissions.config.localization.MessageKey.*;

/**
 * Command executor for the PlaylegendPermissions plugin.
 * This class handles the command execution for different subcommands like player, group, info, and sign.
 */
public class PluginCommandExecutor implements CommandExecutor {

    private final PlayerCommandExecutor playerCommandExecutor;
    private final GroupCommandExecutor groupCommandExecutor;
    private final InfoCommandExecutor infoCommandExecutor;
    private final SignCommandExecutor signCommandExecutor;
    final LocalizationManager localization;

    /**
     * Constructs a new PluginCommandExecutor with the necessary services and localization manager.
     *
     * @param javaPlugin       The JavaPlugin instance.
     * @param userGroupService The service for user group operations.
     * @param playerService    The service for player operations.
     * @param localization     The manager for localization and message formatting.
     */
    public PluginCommandExecutor(JavaPlugin javaPlugin, UserGroupService userGroupService, PlayerService playerService, LocalizationManager localization) {
        this.localization = localization;
        this.infoCommandExecutor = new InfoCommandExecutor(playerService, localization);
        this.playerCommandExecutor = new PlayerCommandExecutor(javaPlugin, userGroupService, playerService, localization);
        this.groupCommandExecutor = new GroupCommandExecutor(userGroupService, playerService, localization, javaPlugin);
        this.signCommandExecutor = new SignCommandExecutor(playerService, localization);
    }

    /**
     * Handles the command execution for the plugin.
     * This method delegates the command handling to specific executors based on the subcommand.
     *
     * @param sender  The entity who sent the command.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided with the command.
     * @return true if a valid command was executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(CommandOption.INFO.getCommand())) {
                return infoCommandExecutor.onCommand(sender, command, label, args);
            }
            if (args[0].equalsIgnoreCase(CommandOption.SIGN.getCommand())) {
                return signCommandExecutor.onCommand(sender, command, label, args);
            } else if (sender.hasPermission("pper.administration")) {
                if (CommandOption.USER.getCommand().equalsIgnoreCase(args[0])) {
                    return playerCommandExecutor.onCommand(sender, command, label, args);
                } else if (args[0].equalsIgnoreCase(CommandOption.GROUP.getCommand()) || CommandOption.CREATE_GROUP.getCommand().equalsIgnoreCase(args[0])) {
                    return groupCommandExecutor.onCommand(sender, command, label, args);
                } else {
                    sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(UNKNOWN_COMMAND));
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(PERMISSION_DENIED));
                return false;
            }

        } else {
            sender.sendMessage(ChatColor.RED + localization.getFormattedMessage(MISSING_COMMAND));
        }
        return true;
    }

}
