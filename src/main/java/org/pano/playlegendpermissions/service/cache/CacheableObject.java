package org.pano.playlegendpermissions.service.cache;

import java.util.List;

/**
 * Interface for cacheable objects in the PlaylegendPermissions plugin.
 *
 * @param <T>       The type of the object being cached.
 * @param <PRIMARY> The type of the primary key for the object.
 */
public interface CacheableObject<T, PRIMARY> {
    /**
     * Loads objects into the cache.
     *
     * @throws Exception If there is an issue loading objects into the cache.
     */
    void loadIntoCache() throws Exception;

    /**
     * Retrieves an object by its ID from the cache.
     *
     * @param id The primary key ID of the object to retrieve.
     * @return The object corresponding to the given ID, or null if not found in the cache.
     */
    T getById(final PRIMARY id);

    /**
     * Retrieves an object by its name from the cache.
     *
     * @param name The name of the object to retrieve.
     * @return The object corresponding to the given name.
     * @throws Exception If there is an issue retrieving the object.
     */
    T getByName(final String name) throws Exception;

    /**
     * Retrieves all objects from the cache.
     *
     * @return A list of all cached objects.
     */
    List<T> getAll();

    /**
     * Adds an object to the cache.
     *
     * @param entity The object to be added to the cache.
     */
    void addToCache(T entity);

    /**
     * Removes an object from the cache by its ID.
     *
     * @param id The primary key ID of the object to remove.
     */
    void removeById(final PRIMARY id);
}
