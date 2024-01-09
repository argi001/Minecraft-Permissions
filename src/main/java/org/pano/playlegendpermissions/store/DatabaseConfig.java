package org.pano.playlegendpermissions.store;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for database operations.
 * This class is responsible for setting up and managing the database connection using HikariCP.
 */
public class DatabaseConfig {
    private static final String DEFAULT_TABLE_PREFIX = "pper_";
    public static String tablePrefix;
    public static String DEFAULT_GROUP = "Player";
    public static String DEFAULT_GROUP_PREFIX = "";
    private static Logger LOGGER;
    private static HikariDataSource dataSource;

    /**
     * Sets up the data source for database connection using HikariCP.
     * Configures the data source based on the plugin configuration.
     *
     * @param plugin The JavaPlugin instance for accessing configuration.
     */
    public static void setupDataSource(JavaPlugin plugin) {
        LOGGER = plugin.getLogger();
        tablePrefix = getTablePrefixFromConfig(plugin);

        if (plugin.getConfig().isString("defaultGroup.name")) {
            DEFAULT_GROUP = plugin.getConfig().getString("defaultGroup.name");
        }
        DEFAULT_GROUP_PREFIX = plugin.getConfig().getString("defaultGroup.prefix");
        String host = plugin.getConfig().getString("database.host");
        String port = plugin.getConfig().getString("database.port");
        String dbname = plugin.getConfig().getString("database.dbname");
        String user = plugin.getConfig().getString("database.user");
        String password = plugin.getConfig().getString("database.password");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbname);
        config.setUsername(user);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves a database connection from the data source.
     *
     * @return A Connection object for database operations.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Initializes the database with the necessary tables and data.
     * Loads and executes SQL statements from an SQL file.
     */
    public static void initializeDatabase() {
        String sql = loadSQLFromFile("init_db.sql");
        if (sql != null) {
            executeSQL(sql);
            try {
                executeSQL("INSERT INTO " + tablePrefix + "user_group (id, name, prefix) VALUES (1, '" + DEFAULT_GROUP + "', '" + DEFAULT_GROUP_PREFIX + "');");
            } catch (RuntimeException e) {
                LOGGER.log(Level.CONFIG, "Default group not inserted, it is already created.");
            }
        }
    }

    /**
     * Loads SQL statements from a file.
     *
     * @param filename The name of the file containing SQL statements.
     * @return A String containing the SQL statements.
     */
    private static String loadSQLFromFile(String filename) {
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            String sql = scanner.useDelimiter("\\A").next();
            return sql.replace("{PREFIX}", tablePrefix);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Executes a series of SQL statements.
     *
     * @param sql A String containing multiple SQL statements to be executed.
     */
    private static void executeSQL(String sql) {

        List<String> statements = Arrays.stream(sql.split(";\\s*")).toList();
        statements.forEach(s -> {
            if (!s.trim().isEmpty()) {
                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(s)) {
                    stmt.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Gets the table prefix from the plugin configuration.
     *
     * @param plugin The JavaPlugin instance for accessing configuration.
     * @return A String representing the table prefix.
     */
    private static String getTablePrefixFromConfig(JavaPlugin plugin) {
        final String configPrefix = plugin.getConfig().getString("database.tablePrefix");
        if (configPrefix != null && !configPrefix.isEmpty()) {
            if (isValidPrefix(configPrefix.trim())) {
                return configPrefix.trim();
            }
        }
        return DEFAULT_TABLE_PREFIX;
    }

    /**
     * Validates if a given string is a valid table prefix.
     *
     * @param tablePrefix The table prefix to validate.
     * @return true if the prefix is valid, false otherwise.
     */
    private static boolean isValidPrefix(String tablePrefix) {
        return tablePrefix.matches("[a-zA-Z0-9_]+");
    }

}



