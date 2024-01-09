package org.pano.playlegendpermissions.exceptions;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException() {
        super("Player could not be found");
    }

}
