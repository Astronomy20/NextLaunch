package com.nextlaunch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path configPath;
    private final Path tempPath;

    public ConfigManager(Path configPath) {
        Objects.requireNonNull(configPath, "Config path cannot be null");
        this.configPath = configPath;
        this.tempPath = configPath.resolveSibling(configPath.getFileName() + ".tmp");
    }

    /**
     * Loads config from disk. Returns a fresh default config if the file doesn't exist.
     */
    public LauncherConfig load() throws IOException {
        if (!Files.exists(configPath)) {
            LOGGER.info("No config file found at {}. Using defaults.", configPath);
            return new LauncherConfig();
        }

        LauncherConfig config = MAPPER.readValue(configPath.toFile(), LauncherConfig.class);
        LOGGER.info("Config loaded from {}", configPath);
        return config;
    }

    /**
     * Loads config, falling back silently to defaults on any error.
     * Preferred for use at application startup.
     */
    public LauncherConfig loadOrDefault() {
        try {
            return load();
        } catch (IOException e) {
            LOGGER.warn("Failed to load config, falling back to defaults: {}", e.getMessage());
            return new LauncherConfig();
        }
    }

    /**
     * Saves config to disk using a temp file to prevent corruption.
     * Writes to .tmp first, then atomically replaces the real file.
     */
    public void save(LauncherConfig config) throws IOException {
        Objects.requireNonNull(config, "Config cannot be null");

        Files.createDirectories(configPath.getParent());

        MAPPER.writeValue(tempPath.toFile(), config);

        Files.move(tempPath, configPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        LOGGER.info("Config saved to {}", configPath);
    }
}