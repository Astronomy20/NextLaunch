package com.nextlaunch.models;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable model representing a valid NextLaunch project.
 */
public final class Project {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]+$");

    private final String id;
    private final String name;
    private final String version;
    private final String description;
    private final String author;
    private final String entryPoint;
    private final Path projectDirectory;

    public Project(
            String id,
            String name,
            String version,
            String description,
            String author,
            String entryPoint,
            Path projectDirectory
    ) {
        this.id = validateId(id);
        this.name = requireNonBlank(name, "Project name");
        this.version = requireNonBlank(version, "Project version");
        this.description = description == null ? "" : description;
        this.author = author == null ? "" : author;

        this.entryPoint = requireNonBlank(entryPoint, "Entry point");
        this.projectDirectory = Objects.requireNonNull(projectDirectory, "Project directory cannot be null");

        validateStructure();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public Path getProjectDirectory() {
        return projectDirectory;
    }

    public Path getEntryPath() {
        return projectDirectory.resolve(entryPoint);
    }

    public boolean isExecutablePresent() {
        return !Files.exists(getEntryPath());
    }


    private String validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Project id cannot be null or blank");
        }

        String trimmed = id.trim();

        if (!ID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "Project id must contain only letters, numbers, hyphens or underscores"
            );
        }

        return trimmed;
    }

    private void validateStructure() {
        if (!Files.isDirectory(projectDirectory)) {
            throw new IllegalArgumentException("Project directory does not exist: " + projectDirectory);
        }

        if (isExecutablePresent()) {
            throw new IllegalArgumentException(
                    "Entry file not found: " + getEntryPath()
            );
        }
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value.trim();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", directory=" + projectDirectory +
                '}';
    }
}