package org.pano.playlegendpermissions.config.localization;

/**
 * Enumerates the keys for messages used in the application.
 * This enum provides a standardized way to refer to message keys that are used to fetch localized strings.
 */
public enum MessageKey {
    GROUP_ALREADY_EXIST("group_already_exist"),
    GROUP_CREATED("group_created"),
    GROUP_NOT_FOUND("group_not_found"),
    GROUP_UPDATE_PREFIX("group_update_prefix"),
    GROUP_UPDATE_PREFIX_ERROR("group_update_prefix_error"),
    JOIN_MESSAGE("join_message"),
    MISSING_COMMAND("missing_command"),
    PERMISSION_DENIED("permission_denied"),
    PLAYER_ADD_ADMIN_RESPONSE("player_add_admin_response"),
    PLAYER_ADD_WITH_EXPIRE_ADMIN_RESPONSE("player_add_with_expire_admin_response"),
    PLAYER_EDIT_ERROR("player_edit_error"),
    PLAYER_INFO_ERROR("player_info_error"),
    PLAYER_INFO_RSP_CURRENT_GROUP("player_info_rsp_current_group"),
    PLAYER_INFO_RSP_CURRENT_GROUP_EXPIRE("player_info_rsp_current_group_expire"),
    PLAYER_INFO_RSP_CURRENT_GROUP_SINCE("player_info_rsp_current_group_since"),
    PLAYER_INFO_RSP_HEADER("player_info_rsp_header"),
    PLAYER_INFO_RSP_PLAYER("player_info_rsp_player"),
    PLAYER_INFO_RSP_PLAYER_ID("player_info_rsp_player_id"),
    PLAYER_NTFC_ADDED_TO_GROUP("player_ntfc_added_to_group"),
    PLAYER_NTFC_ADDED_TO_GROUP_WITH_EXPIRE("player_ntfc_added_to_group_with_expire"),
    PLAYER_NTFC_GROUP_EXPIRED("player_ntfc_group_expired"),
    PLAYER_ONLY_ERROR("player_only_error"),
    SIGN_GENERATE_ERROR("sign_generate_error"),
    UNKNOWN_COMMAND("unknown_command"),
    UNKNOWN_DATE_FORMAT("unknown_date_format"),
    UNKNOWN_PLAYER("unknown_player");

    private final String key;

    /**
     * Constructs a new MessageKey enum constant.
     *
     * @param key The string key associated with the message.
     */
    MessageKey(String key) {
        this.key = key;
    }

    /**
     * Retrieves the string key associated with this message key.
     *
     * @return The string key.
     */
    public String getKey() {
        return key;
    }
}
