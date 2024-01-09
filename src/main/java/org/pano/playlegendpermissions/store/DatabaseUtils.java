package org.pano.playlegendpermissions.store;

import org.pano.playlegendpermissions.config.annotations.AnnotationUtils;
import org.pano.playlegendpermissions.config.annotations.Column;
import org.pano.playlegendpermissions.config.annotations.OneToMany;
import org.pano.playlegendpermissions.model.Player;
import org.pano.playlegendpermissions.model.UserGroup;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic utility class for database operations.
 * This class provides common database operations for entities of type T.
 *
 * @param <T> The type of the entity.
 */
public class DatabaseUtils<T> {
    /**
     * Saves an entity to the database and returns the generated key.
     *
     * @param entity The entity to be saved.
     * @return The generated key for the saved entity.
     * @throws SQLException           If a database access error occurs.
     * @throws IllegalAccessException If the entity fields are not accessible.
     */
    public long saveObjectToDatabase(final T entity) throws SQLException, IllegalAccessException {
        final var tableName = DatabaseConfig.tablePrefix + AnnotationUtils.getTableName(entity.getClass());
        final var fields = Arrays.stream(entity.getClass().getDeclaredFields()).toList();
        final var sql = generateSqlByFields(fields, tableName);
        System.out.println(sql);
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            generateStatementByEntity(entity, fields, stmt);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity to be updated.
     * @throws SQLException           If a database access error occurs.
     * @throws IllegalAccessException If the entity fields are not accessible.
     */
    public void updateObjectInDatabase(final T entity) throws SQLException, IllegalAccessException {
        final String tableName = DatabaseConfig.tablePrefix + AnnotationUtils.getTableName(entity.getClass());
        final List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> !AnnotationUtils.isColumnPrimary(field))
                .collect(Collectors.toList());
        final String sql = generateSqlForUpdate(fields, tableName, entity);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                stmt.setObject(index++, field.get(entity));
            }

            Field primaryKeyField = getPrimaryKeyField(entity.getClass());
            primaryKeyField.setAccessible(true);
            stmt.setObject(index, primaryKeyField.get(entity));

            stmt.executeUpdate();
        }
    }

    /**
     * Finds an entity by its ID.
     *
     * @param entityClass The class of the entity.
     * @param id          The ID of the entity to find.
     * @return A CustomResultSet containing the found entity.
     * @throws SQLException If a database access error occurs.
     */
    public CustomResultSet<T> findById(final Class<T> entityClass, final Object id) throws SQLException {
        final String tableNameWithAlias = getTableNameWithAlias(getPrefixedTableName(entityClass), entityClass);
        final String selectClause = generateSelectClauseWithAliases(entityClass);
        final String joinString = generateJoinString(entityClass);
        final String primaryKeyFieldName = getPrimaryKeyFieldName(entityClass);

        final String sql = selectClause + " FROM " + tableNameWithAlias + joinString +
                " WHERE " + entityClass.getSimpleName() + "." + primaryKeyFieldName + " = ?";
        System.out.println(sql);
        return executeQuery(entityClass, sql, id);
    }

    /**
     * Retrieves all entities of a certain class.
     *
     * @param entityClass The class of the entities to retrieve.
     * @return A CustomResultSet containing all found entities.
     * @throws SQLException If a database access error occurs.
     */
    public CustomResultSet<T> findAll(final Class<T> entityClass) throws SQLException {
        final String tableName = getTableNameWithAlias(getPrefixedTableName(entityClass), entityClass);
        final String selectClause = generateSelectClauseWithAliases(entityClass);
        final String joinString = generateJoinString(entityClass);
        final String sql = selectClause + " FROM " + tableName + joinString;
        return executeQuery(entityClass, sql);
    }

    /**
     * Finds entities based on a specific field and its value.
     *
     * @param entityClass The class of the entities.
     * @param searchField The field to search by.
     * @param searchValue The value to search for.
     * @return A CustomResultSet containing the found entities.
     * @throws SQLException If a database access error occurs.
     */
    public CustomResultSet<T> findByField(Class<T> entityClass, Field searchField, Object searchValue) throws SQLException {
        final String tableNameWithAlias = getTableNameWithAlias(getPrefixedTableName(entityClass), entityClass);
        final String selectClause = generateSelectClauseWithAliases(entityClass);
        final String joinString = generateJoinString(entityClass);
        final String columnName = AnnotationUtils.getColumnName(searchField);

        if (columnName == null) {
            throw new IllegalArgumentException("Field " + searchField.getName() + " has no Column-Annotation.");
        }

        final String sql = selectClause + " FROM " + tableNameWithAlias + joinString +
                " WHERE " + entityClass.getSimpleName() + "." + columnName + " = ?";
        return executeQuery(entityClass, sql, searchValue);
    }

    private String generateSqlForUpdate(List<Field> fields, String tableName, T entity) {
        String fieldAssignments = fields.stream()
                .map(field -> AnnotationUtils.getColumnName(field) + " = ?")
                .collect(Collectors.joining(", "));

        Field primaryKeyField = getPrimaryKeyField(entity.getClass());
        String primaryKeyColumnName = AnnotationUtils.getColumnName(primaryKeyField);

        return "UPDATE " + tableName + " SET " + fieldAssignments + " WHERE " + primaryKeyColumnName + " = ?";
    }

    private Field getPrimaryKeyField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(AnnotationUtils::isColumnPrimary)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Primary Key found in this Class"));
    }


    private CustomResultSet<T> executeQuery(Class<T> entityClass, String sql, Object... params) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                setStatementParameter(stmt, i + 1, params[i]);
            }
            ResultSet resultSet = stmt.executeQuery();
            return new CustomResultSet<>(resultSet, entityClass);
        }
    }

    protected String getPrefixedTableName(final Class<?> entityClass) {
        return DatabaseConfig.tablePrefix + AnnotationUtils.getTableName(entityClass);
    }

    private String getTableNameWithAlias(final String prefixedTableName, final Class<?> entityClass) {
        return prefixedTableName + " AS " + entityClass.getSimpleName();
    }

    protected String generateJoinString(final Class<?> entityClass) {
        StringBuilder joinString = new StringBuilder();
        String tableName = getPrefixedTableName(entityClass);

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                Class<?> foreignClass = field.getType();
                String foreignTableName = getPrefixedTableName(foreignClass);
                String foreignKeyField = oneToMany.foreignKeyField();
                String primaryKeyField = getPrimaryKeyFieldName(foreignClass);

                joinString.append(" LEFT JOIN ")
                        .append(foreignTableName)
                        .append(" ON ")
                        .append(foreignTableName)
                        .append(".")
                        .append(primaryKeyField)
                        .append("=")
                        .append(tableName)
                        .append(".")
                        .append(foreignKeyField)
                        .append(" ");
            }
        }

        return joinString.toString();
    }

    private String generateSelectClauseWithAliases(final Class<?> entityClass) {
        return generateSelectClauseWithAliases(entityClass, false);
    }

    private String generateSelectClauseWithAliases(final Class<?> entityClass, boolean distinct) {
        StringBuilder selectClause = new StringBuilder("SELECT ");
        if (distinct) {
            selectClause.append("DISTINCT ");
        }
        boolean[] isFirstColumn = new boolean[]{true};

        appendColumnAliases(entityClass, selectClause, isFirstColumn);

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                Class<?> foreignClass = field.getType();
                appendColumnAliases(foreignClass, selectClause, isFirstColumn);
            }
        }

        return selectClause.toString();
    }


    private void appendColumnAliases(Class<?> entityClass, StringBuilder selectClause, boolean[] isFirstColumn) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                if (!isFirstColumn[0]) {
                    selectClause.append(", ");
                }
                Column columnAnnotation = field.getAnnotation(Column.class);
                selectClause.append(entityClass.getSimpleName())
                        .append(".")
                        .append(columnAnnotation.name())
                        .append(" AS ")
                        .append(entityClass.getSimpleName())
                        .append("_")
                        .append(field.getName());
                isFirstColumn[0] = false;
            }
        }
    }


    private String generateSqlByFields(final List<Field> fields, final String tableName) {
        List<String> columnNames = new ArrayList<>();
        List<String> valuePlaceholders = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                columnNames.add(columnAnnotation.name());
                valuePlaceholders.add("?");
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToManyAnnotation = field.getAnnotation(OneToMany.class);
                columnNames.add(oneToManyAnnotation.foreignKeyField());
                valuePlaceholders.add("?");
            }
        }

        String columnsJoined = String.join(", ", columnNames);
        String placeholdersJoined = String.join(", ", valuePlaceholders);

        return "INSERT INTO " + tableName + " (" + columnsJoined + ") VALUES (" + placeholdersJoined + ")";
    }


    private void generateStatementByEntity(final T entity, final List<Field> fields, PreparedStatement stmt) throws SQLException, IllegalAccessException {
        int i = 1;
        for (Field field : fields) {
            field.setAccessible(true);

            var parameter = field.get(entity);
            if (isEmptyPrimary(field, parameter)) {
                parameter = null;
            }

            setStatementParameter(stmt, i, parameter);
            i++;
        }
    }

    private boolean isEmptyPrimary(Field field, Object parameter) {
        if (AnnotationUtils.isColumnPrimary(field)) {
            if (parameter instanceof Long) {
                return (long) parameter == 0;
            } else if (parameter instanceof Integer) {
                return (int) parameter == 0;
            }
        }
        return false;
    }


    private String getPrimaryKeyFieldName(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(AnnotationUtils::isColumnPrimary)
                .findFirst()
                .map(AnnotationUtils::getColumnName)
                .orElseThrow(() -> new IllegalArgumentException("No Primary Key found in Klass"));
    }

    private void setStatementParameter(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, java.sql.Types.NULL);
        } else {
            if (value instanceof Integer) {
                stmt.setInt(parameterIndex, (Integer) value);
            } else if (value instanceof Long) {
                stmt.setLong(parameterIndex, (Long) value);
            } else if (value instanceof String) {
                stmt.setString(parameterIndex, (String) value);
            } else if (value instanceof Date) {
                stmt.setDate(parameterIndex, (Date) value);
            } else if (value instanceof Timestamp) {
                stmt.setTimestamp(parameterIndex, (Timestamp) value);
            } else if (value instanceof Player) {
                stmt.setString(parameterIndex, ((Player) value).getUuid());
            } else if (value instanceof UserGroup) {
                stmt.setLong(parameterIndex, ((UserGroup) value).getId());
            }
        }
    }
}
