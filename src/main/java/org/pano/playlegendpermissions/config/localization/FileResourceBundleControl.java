package org.pano.playlegendpermissions.config.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This class provides a control mechanism to create resource bundles
 * from property files located in a specific folder. It extends the standard
 * {@link ResourceBundle.Control} class to allow loading resource bundles from
 * the file system rather than the classpath.
 */
public class FileResourceBundleControl extends ResourceBundle.Control {
    private final File folder;

    /**
     * Constructs a new FileResourceBundleControl instance.
     *
     * @param folder The folder from which the resource bundles will be loaded.
     */
    public FileResourceBundleControl(File folder) {
        this.folder = folder;
    }

    /**
     * Loads a resource bundle for the given base name and locale from the specified folder.
     * This method will try to load a properties file corresponding to the base name and locale from the given folder.
     * If such a file is found, it will be used to create a new {@link PropertyResourceBundle} instance.
     *
     * @param baseName The base name of the resource bundle, a fully qualified class name.
     * @param locale   The locale for which the resource bundle should be instantiated.
     * @param format   The resource bundle format to be loaded.
     * @param loader   The ClassLoader to use to load the resource bundle.
     * @param reload   Flag to indicate whether resource bundle reloading is enabled.
     * @return A resource bundle for the given base name and locale, or {@code null} if no resource bundle is found.
     * @throws IOException If there is an error reading the file.
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
        String bundleName = toBundleName(baseName, locale);
        File file = new File(folder, bundleName + ".properties");

        if (file.isFile()) {
            try (InputStream stream = new FileInputStream(file)) {
                return new PropertyResourceBundle(stream);
            }
        }

        return null;
    }
}
