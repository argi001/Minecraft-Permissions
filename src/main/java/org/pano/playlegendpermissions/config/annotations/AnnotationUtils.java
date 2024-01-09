package org.pano.playlegendpermissions.config.annotations;

import java.lang.reflect.Field;

/**
 * Utility class for handling custom annotations in the PlaylegendPermissions plugin.
 */
public class AnnotationUtils {
    /**
     * Retrieves the table name annotated on a class.
     *
     * @param clazz The class to check for the {@link Table} annotation.
     * @return The table name if present, otherwise null.
     */
    public static String getTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table tableName = clazz.getAnnotation(Table.class);
            return tableName.value();
        }
        return null;
    }

    /**
     * Retrieves the column name annotated on a field.
     *
     * @param field The field to check for the {@link Column} annotation.
     * @return The column name if present, otherwise null.
     */
    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column columnName = field.getAnnotation(Column.class);
            return columnName.name();
        }
        return null;
    }

    /**
     * Checks if a field is marked as a primary key in the database.
     *
     * @param field The field to check for the primary key annotation.
     * @return True if the field is annotated as a primary key, false otherwise.
     */
    public static boolean isColumnPrimary(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).isPrimary();
        }
        return false;
    }

    /**
     * Checks if a field is marked as a foreign key in the database.
     *
     * @param field The field to check for the {@link OneToMany} annotation.
     * @return True if the field is annotated as a foreign key, false otherwise.
     */
    public static boolean isColumnForeignKey(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    /**
     * Retrieves the foreign key field name annotated on a field.
     *
     * @param field The field to check for the {@link OneToMany} annotation.
     * @return The foreign key field name if present, otherwise null.
     */
    public static String getForeignKeyField(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).foreignKeyField();
        }
        return null;
    }

    /**
     * Retrieves the foreign key table name annotated on a field.
     *
     * @param field The field to check for the {@link OneToMany} annotation.
     * @return The foreign key table name if present, otherwise null.
     */
    public static String getForeignKeyTable(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).foreignKeyTable();
        }
        return null;
    }

}
