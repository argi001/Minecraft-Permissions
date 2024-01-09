package org.pano.playlegendpermissions.store;

import org.pano.playlegendpermissions.config.annotations.Column;
import org.pano.playlegendpermissions.config.annotations.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom wrapper for {@link ResultSet} that maps database rows to Java objects of a specified type.
 *
 * @param <T> The type of objects to be created from the ResultSet data.
 */
public class CustomResultSet<T> implements AutoCloseable {
    private final List<T> results;
    private final ResultSet resultSet;

    /**
     * Constructs a new CustomResultSet instance and maps the rows of the given ResultSet to the specified entity type.
     *
     * @param resultSet  The ResultSet to wrap and process.
     * @param entityType The class of the entity type to map to.
     * @throws SQLException If a database access error occurs or mapping fails.
     */
    public CustomResultSet(ResultSet resultSet, Class<T> entityType) throws SQLException {
        this.resultSet = resultSet;
        this.results = new ArrayList<>();
        while (this.resultSet.next()) {
            T entity = mapRowToEntity(this.resultSet, entityType);
            results.add(entity);
        }
    }

    /**
     * Maps a single row of a ResultSet to an object of the specified entity type.
     *
     * @param resultSet  The ResultSet from which to map the row.
     * @param entityType The class of the entity type to map to.
     * @return An instance of T that represents the mapped row.
     * @throws SQLException If a database access error occurs or mapping fails.
     */
    private T mapRowToEntity(ResultSet resultSet, Class<T> entityType) throws SQLException {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();
            for (Field field : entityType.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = entityType.getSimpleName() + "_" + field.getName();
                    Object value = resultSet.getObject(columnName);
                    field.set(entity, value);
                } else if (field.isAnnotationPresent(OneToMany.class)) {
                    Class<?> foreignClass = field.getType();
                    Object referencedEntity = loadReferencedEntity(resultSet, foreignClass);
                    field.set(entity, referencedEntity);
                }
            }
            return entity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new SQLException("Mapping error", e);
        }
    }

    /**
     * Loads a referenced entity from a ResultSet.
     *
     * @param resultSet    The ResultSet from which to load the referenced entity.
     * @param foreignClass The class of the foreign entity to be loaded.
     * @return An instance of the foreign entity class.
     * @throws SQLException If a database access error occurs or loading fails.
     */
    private Object loadReferencedEntity(ResultSet resultSet, Class<?> foreignClass) throws SQLException {
        try {
            Object foreignEntity = foreignClass.getDeclaredConstructor().newInstance();
            for (Field foreignField : foreignClass.getDeclaredFields()) {
                foreignField.setAccessible(true);

                if (foreignField.isAnnotationPresent(Column.class)) {
                    String foreignColumnName = foreignClass.getSimpleName() + "_" + foreignField.getName();
                    Object foreignValue = resultSet.getObject(foreignColumnName);
                    foreignField.set(foreignEntity, foreignValue);
                }
            }
            return foreignEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new SQLException("Error loading referenced entity", e);
        }
    }

    /**
     * Gets the list of results mapped from the ResultSet.
     *
     * @return A list of mapped results.
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * Closes the underlying ResultSet.
     *
     * @throws Exception If an error occurs while closing the ResultSet.
     */
    @Override
    public void close() throws Exception {
        if (resultSet != null) {
            resultSet.close();
        }
    }
}
