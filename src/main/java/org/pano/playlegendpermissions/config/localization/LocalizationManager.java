package org.pano.playlegendpermissions.config.localization;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages localization resources for the PlaylegendPermissions plugin.
 * Handles loading and formatting of localized messages.
 */
public class LocalizationManager {
    private final static List<String> AVAILABLE_LANGUAGES = List.of("de", "en", "es");
    private final static String BASE_NAME = "messages";
    private final ResourceBundle messages;

    /**
     * Creates an instance of LocalizationManager, loading the appropriate resource bundle based on the specified language.
     *
     * @param javaPlugin The JavaPlugin instance.
     * @param language   The language code for the desired localization.
     * @throws RuntimeException if the localization file cannot be read from the filesystem.
     */
    public LocalizationManager(JavaPlugin javaPlugin, String language) throws RuntimeException {
        File dataFolder = javaPlugin.getDataFolder();
        Locale locale = new Locale(language);
        AVAILABLE_LANGUAGES.forEach(languageString -> saveResourceIfNotExist(javaPlugin, getResourceNameByLang(languageString)));
        try {
            ClassLoader loader = new URLClassLoader(new URL[]{dataFolder.toURI().toURL()});
            ResourceBundle.Control control = new FileResourceBundleControl(dataFolder);
            messages = ResourceBundle.getBundle(BASE_NAME, locale, loader, control);
        } catch (Exception e) {
            throw new RuntimeException("Could not read localization file from filesystem");
        }
    }

    /**
     * Retrieves a formatted message from the resource bundle.
     *
     * @param messageKey The key of the message in the resource bundle.
     * @param args       Arguments for formatting the message (if needed).
     * @return A formatted localized message.
     */
    public String getFormattedMessage(MessageKey messageKey, Object... args) {
        String message = messages.getString(messageKey.getKey());
        return java.text.MessageFormat.format(message, args);
    }

    /**
     * Constructs the resource name for a given language.
     *
     * @param language The language code.
     * @return The resource name for the specified language.
     */
    private String getResourceNameByLang(final String language) {
        return BASE_NAME + "_" + language + ".properties";
    }

    /**
     * Saves a resource file into the plugin's data folder if it does not already exist.
     *
     * @param javaPlugin   The JavaPlugin instance.
     * @param resourceName The name of the resource to be saved.
     */
    private void saveResourceIfNotExist(JavaPlugin javaPlugin, String resourceName) {
        File file = new File(javaPlugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            try (InputStream in = javaPlugin.getResource(resourceName);
                 FileOutputStream out = new FileOutputStream(file)) {
                if (in == null) {
                    javaPlugin.getLogger().warning("Resource " + resourceName + " not found in jar.");
                    return;
                }
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                javaPlugin.getLogger().warning("Error while saving file: " + resourceName + " into Plugin folder");
            }
        }
    }
}
