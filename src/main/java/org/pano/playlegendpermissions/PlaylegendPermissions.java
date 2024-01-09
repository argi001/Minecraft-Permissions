package org.pano.playlegendpermissions;

import org.bukkit.plugin.java.JavaPlugin;
import org.pano.playlegendpermissions.config.localization.LocalizationManager;
import org.pano.playlegendpermissions.repository.Player2GroupRepository;
import org.pano.playlegendpermissions.repository.PlayerRepository;
import org.pano.playlegendpermissions.repository.UserGroupRepository;
import org.pano.playlegendpermissions.service.PlayerService;
import org.pano.playlegendpermissions.service.UserGroupService;
import org.pano.playlegendpermissions.service.cache.PlayerCacheManager;
import org.pano.playlegendpermissions.service.cache.UserGroupCacheManager;
import org.pano.playlegendpermissions.service.command.PluginCommandExecutor;
import org.pano.playlegendpermissions.service.command.PluginTabCompleter;
import org.pano.playlegendpermissions.service.listener.ChatListener;
import org.pano.playlegendpermissions.service.listener.GroupSignListener;
import org.pano.playlegendpermissions.service.listener.PlayerJoinListener;
import org.pano.playlegendpermissions.service.listener.PlayerQuitListener;
import org.pano.playlegendpermissions.service.scheduler.PermissionCheckScheduler;
import org.pano.playlegendpermissions.store.DatabaseConfig;

import java.util.Objects;

/**
 * Main class for the PlaylegendPermissions plugin.
 * This class handles the initialization and setup of the plugin, including setting up the database,
 * repositories, services, schedulers, and command executors. It also registers event listeners
 * to handle various Bukkit events.
 */
public final class PlaylegendPermissions extends JavaPlugin {
    /**
     * Called when the plugin is enabled. This method sets up the default configuration,
     * initializes the database, services, command executors, and event listeners.
     * It also starts necessary schedulers.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        String language = this.getConfig().getString("language", "en");
        final LocalizationManager localizationManager = new LocalizationManager(this, language);

        try {
            DatabaseConfig.setupDataSource(this);
            DatabaseConfig.initializeDatabase();

            UserGroupRepository userGroupRepository = new UserGroupRepository();
            PlayerRepository playerRepository = new PlayerRepository();
            Player2GroupRepository player2GroupRepository = new Player2GroupRepository();

            UserGroupCacheManager userGroupCacheManager = new UserGroupCacheManager(userGroupRepository);
            PlayerCacheManager playerCacheManager = new PlayerCacheManager();

            PlayerService playerService = new PlayerService(playerRepository, player2GroupRepository, playerCacheManager);
            UserGroupService userGroupService = new UserGroupService(userGroupRepository, userGroupCacheManager, playerCacheManager, playerRepository, localizationManager);

            PermissionCheckScheduler permissionCheckScheduler = new PermissionCheckScheduler(this, playerCacheManager, playerService, localizationManager);

            userGroupCacheManager.loadIntoCache();

            permissionCheckScheduler.startTask();

            Objects.requireNonNull(this.getCommand("pper")).setExecutor(new PluginCommandExecutor(this, userGroupService, playerService, localizationManager));
            Objects.requireNonNull(this.getCommand("pper")).setTabCompleter(new PluginTabCompleter(userGroupCacheManager));

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerService, userGroupService, localizationManager, this), this);
            getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerService), this);
            getServer().getPluginManager().registerEvents(new ChatListener(playerService, this), this);
            getServer().getPluginManager().registerEvents(new GroupSignListener(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
