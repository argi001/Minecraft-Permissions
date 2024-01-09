package org.pano.playlegendpermissions.store;

import java.sql.SQLException;
import java.util.List;

/**
 * A generic interface for defining basic database operations.
 * This interface provides methods for finding, saving, and updating entities in the database.
 *
 * @param <T> The type of the entity that the repository works with.
 * @param <ID> The type of the primary key of the entity.
 */
public interface DatabaseStoreInterface<T, ID> {

    /**
     * Finds an entity that matches a given entity.
     *
     * @param entity The entity to be searched for in the database.
     * @return An instance of the entity if found, or null otherwise.
     * @throws Exception If there is an issue during the database operation.
     */
    T findOne(T entity) throws Exception;

    /**
     * Finds an entity by its primary key.
     *
     * @param id The primary key of the entity to be found.
     * @return The found entity, or null if no such entity exists.
     * @throws Exception If there is an issue during the database operation.
     */
    T findById(ID id) throws Exception;

    /**
     * Finds all entities in the database.
     *
     * @return A list of all entities.
     * @throws Exception If there is an issue during the database operation.
     */
    List<T> findAll() throws Exception;

    /**
     * Saves a given entity to the database.
     *
     * @param entity The entity to be saved.
     * @return The saved entity.
     * @throws Exception If there is an issue during the database operation.
     */
    T save(T entity) throws Exception;

    /**
     * Updates a given entity in the database.
     *
     * @param entity The entity to be updated.
     * @return The updated entity.
     * @throws SQLException If there is a SQL issue during the update operation.
     * @throws IllegalAccessException If there is an illegal access issue during the operation.
     */
    T update(T entity) throws SQLException, IllegalAccessException;
}

