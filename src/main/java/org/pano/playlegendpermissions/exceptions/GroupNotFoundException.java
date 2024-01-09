package org.pano.playlegendpermissions.exceptions;

public class GroupNotFoundException extends Exception {
    public GroupNotFoundException() {
        super("Group could not be found");
    }
    public GroupNotFoundException(String message) {
        super(message);
    }
}
