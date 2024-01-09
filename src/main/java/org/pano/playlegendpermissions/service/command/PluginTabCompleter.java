package org.pano.playlegendpermissions.service.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.service.cache.UserGroupCacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.pano.playlegendpermissions.service.command.CommandOption.*;

/**
 * A tab completer class for the PlaylegendPermissions plugin commands.
 * This class provides dynamic tab completion suggestions based on the user's permissions and the context of the command being typed.
 */
public class PluginTabCompleter implements TabCompleter {
    private final UserGroupCacheManager userGroupCacheManager;

    /**
     * Constructs a new PluginTabCompleter with the necessary user group cache manager.
     *
     * @param userGroupCacheManager Manager for the user group cache.
     */
    public PluginTabCompleter(UserGroupCacheManager userGroupCacheManager) {
        this.userGroupCacheManager = userGroupCacheManager;
    }

    /**
     * Provides tab completion options for plugin commands based on the command, sender, and current arguments.
     *
     * @param sender  The entity that sent the command.
     * @param command The command being executed.
     * @param alias   The alias of the command.
     * @param args    The arguments provided with the command.
     * @return A list of tab completion options or null if no suggestions are available.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("pper")) {
            return null;
        }

        if (!sender.hasPermission("pper.administration")) {
            return handleNonAdminCommands(args);
        }

        return handleAdminCommands(args);
    }

    private List<String> handleNonAdminCommands(String[] args) {
        if (args.length == 1) {
            return List.of(INFO.getCommand(), SIGN.getCommand());
        }
        return null;
    }

    private List<String> handleAdminCommands(String[] args) {
        final var allGroups = userGroupCacheManager.getAll().stream().map(UserGroup::getGroupName).toList();
        final var onlinePlayer = getOnlinePlayerNames();

        return switch (args.length) {
            case 1 -> Arrays.asList(USER.getCommand(), GROUP.getCommand(),
                    CREATE_GROUP.getCommand(), LIST_GROUPS.getCommand(), INFO.getCommand(), SIGN.getCommand());
            case 2 -> handleAdminCommandsLevel2(args, onlinePlayer, allGroups);
            case 3 -> handleAdminCommandsLevel3(args);
            case 4 -> handleAdminCommandsLevel4(args, onlinePlayer, allGroups);
            case 5 -> handleAdminCommandsLevel5(args);
            default -> null;
        };
    }

    private List<String> handleAdminCommandsLevel2(String[] args, List<String> onlinePlayer, List<String> allGroups) {
        return CommandOption.getSubCommandOption(args[0])
                .map(option -> switch (option) {
                    case USER -> onlinePlayer;
                    case GROUP -> allGroups;
                    case CREATE_GROUP -> List.of("<group-name>");
                    default -> null;
                })
                .orElse(null);
    }

    private List<String> handleAdminCommandsLevel3(String[] args) {
        return CommandOption.getSubCommandOption(args[0])
                .map(option -> switch (option) {
                    case USER ->
                            Arrays.asList(SET_GROUP.getCommand(), REMOVE_GROUP.getCommand(), SHOW_GROUP.getCommand());
                    case GROUP -> Arrays.asList(SET_PREFIX.getCommand(), LIST_PLAYER.getCommand());
                    case CREATE_GROUP -> List.of("<prefix>");
                    default -> null;
                })
                .orElse(null);
    }

    private List<String> handleAdminCommandsLevel4(String[] args, List<String> onlinePlayer, List<String> allGroups) {
        return CommandOption.getSubCommandOption(args[0])
                .map(option -> switch (option) {
                    case ADD_PLAYER -> onlinePlayer;
                    case USER -> args[2].equalsIgnoreCase(SET_GROUP.getCommand()) ? allGroups : null;
                    default -> null;
                })
                .orElse(null);
    }

    private List<String> handleAdminCommandsLevel5(String[] args) {
        if (args[0].equalsIgnoreCase(USER.getCommand()) && args[2].equalsIgnoreCase(SET_GROUP.getCommand())) {
            return List.of("<number>d <number>h <number>m <number>s");
        }
        return null;
    }


    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
