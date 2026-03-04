package com.nextlaunch.manager;

import com.nextlaunch.config.settings.ThemeSettings;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manages theme discovery and application at runtime.
 * Themes are CSS files located in /resources/styles/.
 * A theme name maps directly to its filename without the .css extension.
 * Example: "dark_mode" -> /styles/dark_mode.css
 */
public class ThemeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeManager.class);
    private static final String STYLES_RESOURCE_DIR = "/styles/";
    private static final String CSS_EXTENSION = ".css";

    private final ThemeSettings themeSettings;
    private List<String> availableThemes = new ArrayList<>();

    public ThemeManager(ThemeSettings themeSettings) {
        this.themeSettings = Objects.requireNonNull(themeSettings, "ThemeSettings cannot be null");
        discoverThemes();
    }

    // -------------------------------------------------------------------------
    // Discovery
    // -------------------------------------------------------------------------

    /**
     * Scans the /styles/ resource directory and populates the list of available themes.
     */
    public void discoverThemes() {
        availableThemes = new ArrayList<>();

        try {
            URL stylesUrl = getClass().getResource(STYLES_RESOURCE_DIR);
            if (stylesUrl == null) {
                LOGGER.warn("Styles resource directory not found.");
                return;
            }

            URI uri = stylesUrl.toURI();

            if (uri.getScheme().equals("jar")) {
                discoverFromJar(uri);
            } else {
                discoverFromFileSystem(Path.of(uri));
            }

        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Failed to discover themes", e);
        }

        Collections.sort(availableThemes);
        LOGGER.info("Discovered {} theme(s): {}", availableThemes.size(), availableThemes);
    }

    private void discoverFromFileSystem(Path stylesDir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(stylesDir, "*" + CSS_EXTENSION)) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                availableThemes.add(stripExtension(fileName));
            }
        }
    }

    private void discoverFromJar(URI jarUri) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap())) {
            Path stylesPath = fs.getPath(STYLES_RESOURCE_DIR);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(stylesPath, "*" + CSS_EXTENSION)) {
                for (Path file : stream) {
                    String fileName = file.getFileName().toString();
                    availableThemes.add(stripExtension(fileName));
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Application
    // -------------------------------------------------------------------------

    /**
     * Applies the currently active theme from ThemeSettings to the given scene.
     */
    public void applyTheme(Scene scene) {
        applyTheme(scene, themeSettings.getActiveTheme());
    }

    /**
     * Applies a specific theme by name to the given scene and persists the choice.
     */
    public void applyTheme(Scene scene, String themeName) {
        Objects.requireNonNull(scene, "Scene cannot be null");
        Objects.requireNonNull(themeName, "Theme name cannot be null");

        if (!availableThemes.contains(themeName)) {
            LOGGER.warn("Theme '{}' not found. Falling back to default.", themeName);
            themeName = ThemeSettings.DEFAULT_THEME;
        }

        String resourcePath = STYLES_RESOURCE_DIR + themeName + CSS_EXTENSION;
        URL cssUrl = getClass().getResource(resourcePath);

        if (cssUrl == null) {
            LOGGER.error("CSS file not found for theme '{}' at {}", themeName, resourcePath);
            return;
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(cssUrl.toExternalForm());

        themeSettings.setActiveTheme(themeName);
        LOGGER.info("Applied theme '{}'", themeName);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the list of discovered theme names (without .css extension).
     */
    public List<String> getAvailableThemes() {
        return Collections.unmodifiableList(availableThemes);
    }

    public String getActiveTheme() {
        return themeSettings.getActiveTheme();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - CSS_EXTENSION.length());
    }
}