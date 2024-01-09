package org.pano.playlegendpermissions.repository;

import org.pano.playlegendpermissions.model.UserGroup;
import org.pano.playlegendpermissions.store.CustomResultSet;
import org.pano.playlegendpermissions.store.DatabaseStoreInterface;
import org.pano.playlegendpermissions.store.DatabaseUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Repository class for handling database operations related to the UserGroup entity.
 * This class extends the generic DatabaseUtils for UserGroup and implements the DatabaseStoreInterface.
 */
public class UserGroupRepository extends DatabaseUtils<UserGroup> implements DatabaseStoreInterface<UserGroup, Long> {

    @Override
    public UserGroup findOne(UserGroup userGroup) throws Exception {
        return this.findById(userGroup.getId());
    }

    @Override
    public UserGroup findById(Long id) throws Exception {
        try (CustomResultSet<UserGroup> resultSet = findById(UserGroup.class, id)) {
            return resultSet.getResults().isEmpty() ? null : resultSet.getResults().get(0);
        }
    }

    @Override
    public List<UserGroup> findAll() throws Exception {
        try (CustomResultSet<UserGroup> resultSet = super.findAll(UserGroup.class)) {
            return new ArrayList<>(resultSet.getResults());
        }
    }

    @Override
    public UserGroup save(UserGroup userGroup) throws SQLException, IllegalAccessException {
        if (userGroup.getId() > 0) {
            return update(userGroup);
        }
        final var generatedKey = saveObjectToDatabase(userGroup);
        userGroup.setId(generatedKey);
        return userGroup;
    }

    @Override
    public UserGroup update(UserGroup userGroup) throws SQLException, IllegalAccessException {
        updateObjectInDatabase(userGroup);
        return userGroup;
    }
    /**
     * Finds the first UserGroup entity by its name.
     *
     * @param name The name of the UserGroup to be found.
     * @return The UserGroup entity if found, or null otherwise.
     * @throws Exception If there is an issue during the database operation.
     */
    public UserGroup findFirstByName(String name) throws Exception {
        try (CustomResultSet<UserGroup> resultSet = super.findByField(UserGroup.class, UserGroup.class.getDeclaredField("groupName"), name)) {
            return resultSet.getResults().isEmpty() ? null : resultSet.getResults().get(0);
        }
    }
}
