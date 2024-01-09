package org.pano.playlegendpermissions.model;

import org.pano.playlegendpermissions.config.annotations.Column;
import org.pano.playlegendpermissions.config.annotations.Table;

import java.sql.Timestamp;

@Table("player")
public class Player {
    @Column(name = "uuid", isPrimary = true)
    private String uuid;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "create_date")
    private Timestamp createDate;

    public Player() {
    }

    public Player(String uuid, String displayName, Timestamp createDate) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.createDate = createDate;
    }

    public Player(String uuid, String displayName) {
        this.uuid = uuid;
        this.displayName = displayName;
    }

    public Player(String displayName, Timestamp createDate) {
        this.displayName = displayName;
        this.createDate = createDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return "Player{" +
                "uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
