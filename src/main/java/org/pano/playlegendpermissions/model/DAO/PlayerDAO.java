package org.pano.playlegendpermissions.model.DAO;

import java.sql.Timestamp;

public class PlayerDAO {
    private String uuid;
    private Long player2groupId;
    private String name;
    private Long currentGroupId;
    private String currentGroupName;
    private String currentGroupPrefix;
    private Timestamp currentGroupExpire;
    private Long lastGroupId;
    private Timestamp createDate;


    public PlayerDAO(String uuid, Long player2groupId, String name, Long currentGroupId, String currentGroupName, String currentGroupPrefix, Timestamp currentGroupExpire, Long lastGroupId, Timestamp createDate) {
        this.uuid = uuid;
        this.player2groupId = player2groupId;
        this.name = name;
        this.currentGroupId = currentGroupId;
        this.currentGroupName = currentGroupName;
        this.currentGroupPrefix = currentGroupPrefix;
        this.currentGroupExpire = currentGroupExpire;
        this.lastGroupId = lastGroupId;
        this.createDate = createDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(Long currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public String getCurrentGroupName() {
        return currentGroupName;
    }

    public void setCurrentGroupName(String currentGroupName) {
        this.currentGroupName = currentGroupName;
    }

    public String getCurrentGroupPrefix() {
        return currentGroupPrefix;
    }

    public void setCurrentGroupPrefix(String currentGroupPrefix) {
        this.currentGroupPrefix = currentGroupPrefix;
    }

    public Timestamp getCurrentGroupExpire() {
        return currentGroupExpire;
    }

    public void setCurrentGroupExpire(Timestamp currentGroupExpire) {
        this.currentGroupExpire = currentGroupExpire;
    }

    public Long getLastGroupId() {
        return lastGroupId;
    }

    public void setLastGroupId(Long lastGroupId) {
        this.lastGroupId = lastGroupId;
    }

    public Long getPlayer2groupId() {
        return player2groupId;
    }

    public void setPlayer2groupId(Long player2groupId) {
        this.player2groupId = player2groupId;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
