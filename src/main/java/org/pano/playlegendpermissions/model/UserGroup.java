package org.pano.playlegendpermissions.model;

import org.pano.playlegendpermissions.config.annotations.Column;
import org.pano.playlegendpermissions.config.annotations.Table;

@Table("user_group")
public class UserGroup {
    @Column(name = "id", isPrimary = true)
    private long id;
    @Column(name = "name")
    private String groupName;
    @Column(name = "prefix")
    private String prefix;

    public UserGroup() {
    }

    public UserGroup(String groupName) {
        this.groupName = groupName;
    }

    public UserGroup(String groupName, String prefix) {
        this.groupName = groupName;
        this.prefix = prefix;
    }

    public UserGroup(long id, String groupName, String prefix) {
        this.id = id;
        this.groupName = groupName;
        this.prefix = prefix;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
