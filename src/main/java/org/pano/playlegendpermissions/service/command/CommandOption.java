package org.pano.playlegendpermissions.service.command;

import java.util.Optional;

public enum CommandOption {
    USER("user"),
    GROUP("group"),
    CREATE_GROUP("creategroup"),
    INFO("info"),
    SIGN("sign"),
    SET_GROUP("setgroup"),
    REMOVE_GROUP("removegroup"),
    SHOW_GROUP("showgroup"),
    SET_PREFIX("setprefix"),
    LIST_PLAYER("listplayer"),
    LIST_GROUPS("listgroups"),
    ADD_PLAYER("addplayer");

    private final String command;

    CommandOption(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Optional<CommandOption> getSubCommandOption(String command) {
        for (CommandOption option : CommandOption.values()) {
            if (command.equalsIgnoreCase(option.getCommand())) {
                return Optional.of(option);
            }
        }
        return Optional.empty();
    }
}
