package org.pano.playlegendpermissions.model;

import org.pano.playlegendpermissions.config.annotations.Column;
import org.pano.playlegendpermissions.config.annotations.OneToMany;
import org.pano.playlegendpermissions.config.annotations.Table;

import java.sql.Timestamp;

@Table("player2group")
public class Player2Group {
    @Column(name = "id", isPrimary = true)
    private long id;
    @OneToMany(foreignKeyField = "player_uuid", foreignKeyTable = "player")
    private Player player;
    @OneToMany(foreignKeyField = "group_id", foreignKeyTable = "user_group")
    private UserGroup userGroup;
    @Column(name = "expire_datetime")
    private Timestamp expireDate;
    @Column(name = "create_date")
    private Timestamp createDate;
    @Column(name = "last_group_id")
    private long lastGroupId;


    public Player2Group(long id, Player player, UserGroup userGroup, Timestamp expireDate, Timestamp createDate, long lastGroupId) {
        this.id = id;
        this.player = player;
        this.userGroup = userGroup;
        this.expireDate = expireDate;
        this.createDate = createDate;
        this.lastGroupId = lastGroupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }


    public long getLastGroupId() {
        return lastGroupId;
    }

}
