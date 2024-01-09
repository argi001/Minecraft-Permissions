package org.pano.playlegendpermissions.repository;

import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.Player2Group;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.store.CustomResultSet;
import org.pano.playlegendpermissions.store.DatabaseConfig;
import org.pano.playlegendpermissions.store.DatabaseStoreInterface;
import org.pano.playlegendpermissions.store.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Player entities in the database.
 * This class provides methods to find, save, and update Player entities.
 */
public class PlayerRepository extends DatabaseUtils<Player> implements DatabaseStoreInterface<Player, String> {

    @Override
    public Player findOne(Player player) throws Exception {
        return this.findById(player.getUuid());
    }

    @Override
    public Player findById(String id) throws Exception {
        try (CustomResultSet<Player> resultSet = super.findById(Player.class, id)) {
            return resultSet.getResults().isEmpty() ? null : resultSet.getResults().get(0);
        }
    }

    @Override
    public List<Player> findAll() throws Exception {
        try (CustomResultSet<Player> resultSet = super.findAll(Player.class)) {
            return new ArrayList<>(resultSet.getResults());
        }
    }

    @Override
    public Player save(Player player) throws Exception {
        if (findById(player.getUuid()) != null) {
            return update(player);
        }
        saveObjectToDatabase(player);
        return player;
    }

    @Override
    public Player update(Player player){
        // Implement the update logic.
        return null;
    }

    /**
     * Finds the first Player entity by their display name.
     *
     * @param playerName The display name of the player to find.
     * @return The Player entity, or null if not found.
     * @throws Exception If there is an issue during the database operation.
     */
    public Player findFirstByName(String playerName) throws Exception {
        try (CustomResultSet<Player> resultSet = findByField(Player.class, Player.class.getDeclaredField("displayName"), playerName)) {
            return resultSet.getResults().isEmpty() ? null : resultSet.getResults().get(0);
        }
    }

    /**
     * Retrieves a list of Player entities who have a history in a given UserGroup.
     *
     * @param userGroup The UserGroup to check the players' history against.
     * @return A list of Player entities.
     * @throws Exception If there is an issue during the database operation.
     */
    public List<Player> getPlayerHistoryInGroup(final UserGroup userGroup) throws Exception {
        String sql = "SELECT DISTINCT " +
                getPrefixedTableName(Player.class) +
                ".uuid as Player_uuid, " +
                getPrefixedTableName(Player.class) +
                ".display_name as Player_displayName, " +
                getPrefixedTableName(Player.class) +
                ".create_date as Player_createDate " +
                "FROM " +
                getPrefixedTableName(Player2Group.class) +
                generateJoinString(Player2Group.class) +
                " WHERE " +
                getPrefixedTableName(UserGroup.class) +
                ".id" +
                " = ? ";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userGroup.getId());
            final var resultSet = stmt.executeQuery();
            try (CustomResultSet<Player> result = new CustomResultSet<>(resultSet, Player.class)) {
                return new ArrayList<>(result.getResults());
            }
        }
    }
}
