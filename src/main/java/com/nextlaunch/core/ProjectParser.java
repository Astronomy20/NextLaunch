package com.nextlaunch.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlaunch.models.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Parses a project folder and creates a Project object.
 */
public class ProjectParser {

    private final ObjectMapper objectMapper;

    public ProjectParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parses a project.json file inside the given folder and returns a Project object.
     *
     * @param projectFolder Path to the project folder
     * @return Project object
     * @throws InvalidProjectException if JSON is invalid or required fields are missing
     */
    public Project parseProject(Path projectFolder) throws InvalidProjectException {
        Objects.requireNonNull(projectFolder, "Project folder cannot be null");

        if (!Files.isDirectory(projectFolder)) {
            throw new InvalidProjectException("Project folder does not exist: " + projectFolder);
        }

        Path jsonFile = projectFolder.resolve("project.json");

        if (!Files.exists(jsonFile)) {
            throw new InvalidProjectException("Missing project.json in folder: " + projectFolder);
        }

        try {
            JsonNode root = objectMapper.readTree(jsonFile.toFile());

            String id = getRequiredText(root, "id");
            String name = getRequiredText(root, "name");
            String version = getRequiredText(root, "version");
            String entryPoint = getRequiredText(root, "entryPoint");

            String description = root.has("description") ? root.get("description").asText("") : "";
            String author = root.has("author") ? root.get("author").asText("") : "";

            return new Project(id, name, version, description, author, entryPoint, projectFolder);

        } catch (IOException e) {
            throw new InvalidProjectException("Failed to read project.json in folder: " + projectFolder, e);
        } catch (IllegalArgumentException e) {
            throw new InvalidProjectException("Invalid project data in folder: " + projectFolder + " - " + e.getMessage(), e);
        }
    }

    private String getRequiredText(JsonNode node, String fieldName) throws InvalidProjectException {
        if (!node.has(fieldName)) {
            throw new InvalidProjectException("Missing required field '" + fieldName + "'");
        }
        String value = node.get(fieldName).asText().trim();
        if (value.isEmpty()) {
            throw new InvalidProjectException("Field '" + fieldName + "' cannot be empty");
        }
        return value;
    }
    public static class InvalidProjectException extends Exception {
        public InvalidProjectException(String message) {
            super(message);
        }

        public InvalidProjectException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}