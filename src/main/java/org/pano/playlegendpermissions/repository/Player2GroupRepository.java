package org.pano.playlegendpermissions.repository;

import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.Player2Group;
import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.store.DatabaseConfig;
import org.pano.playlegendpermissions.store.DatabaseStoreInterface;
import org.pano.playlegendpermissions.store.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for handling database operations related to the Player2Group entity.
 * This class extends the generic DatabaseUtils for Player2Group and implements the DatabaseStoreInterface.
 */
public class Player2GroupRepository extends DatabaseUtils<Player2Group> implements DatabaseStoreInterface<Player2Group, Long> {


    @Override
    public Player2Group findOne(Player2Group player2Group) throws Exception {
        return this.findById(player2Group.getId());
    }

    @Override
    public Player2Group findById(Long id) throws Exception {
        try (final var resultSet = super.findById(Player2Group.class, id)) {
            return resultSet.getResults().isEmpty() ? null : resultSet.getResults().get(0);
        }
    }

    @Override
    public List<Player2Group> findAll() throws Exception {
        try (final var resultSet = findAll(Player2Group.class)) {
            return new ArrayList<>(resultSet.getResults());
        }
    }

    @Override
    public Player2Group save(Player2Group player2Group) throws SQLException, IllegalAccessException {
        if (player2Group.getId() > 0) {
            return update(player2Group);
        }
        final var generatedKey = saveObjectToDatabase(player2Group);
        player2Group.setId(generatedKey);
        return player2Group;
    }

    @Override
    public Player2Group update(Player2Group entity) {
        return null;
    }

    /**
     * Finds all Player2Group entities associated with a specific Player.
     *
     * @param player The Player whose Player2Group associations are to be found.
     * @return A list of Player2Group entities associated with the given Player.
     * @throws SQLException If there is an issue during the database operation.
     */
    public List<Player2Group> findAllByPlayer(Player player) throws SQLException {
        String sql = "SELECT * FROM " +
                getPrefixedTableName(Player2Group.class) +
                generateJoinString(Player2Group.class) +
                " WHERE " +
                getPrefixedTableName(Player.class) +
                ".uuid" +
                " = ? " +
                " ORDER BY " +
                getPrefixedTableName(Player2Group.class) +
                ".create_date DESC ";
        System.out.println(sql);
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, player.getUuid());
            final var resultSet = stmt.executeQuery();

            List<Player2Group> player2Groups = new ArrayList<>();
            while (resultSet.next()) {
                player2Groups.add(mapRowToGroup(resultSet));
            }
            resultSet.close();
            return player2Groups;
        }
    }

    /**
     * Maps a row in a ResultSet to a Player2Group entity.
     *
     * @param rs The ResultSet from which to map the data.
     * @return A Player2Group entity populated with data from the ResultSet row.
     * @throws SQLException If there is an issue reading from the ResultSet.
     */
    private Player2Group mapRowToGroup(ResultSet rs) throws SQLException {
        return new Player2Group(
                rs.getLong(1),
                new Player(rs.getString("uuid"), rs.getString("display_name"), rs.getTimestamp(9)),
                new UserGroup(rs.getLong(10), rs.getString(11), rs.getString(12)),
                rs.getTimestamp("expire_datetime"),
                rs.getTimestamp("create_date"),
                rs.getLong("last_group_id"));
    }


}
