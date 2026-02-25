package com.nextlaunch.core;

import com.nextlaunch.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Scans a projects folder and returns all valid Project objects.
 */
public class ProjectScanner {

    private static final Logger logger = LoggerFactory.getLogger(ProjectScanner.class);

    private final ProjectParser parser;

    public ProjectScanner(ProjectParser parser) {
        this.parser = Objects.requireNonNull(parser, "ProjectParser cannot be null");
    }

    /**
     * Scans the given root directory for projects.
     *
     * @param projectsRoot the path to the /projects folder
     * @return List of valid Projects
     */
    public List<Project> scanProjects(Path projectsRoot) {
        Objects.requireNonNull(projectsRoot, "Projects root path cannot be null");

        if (!Files.exists(projectsRoot)) {
            try {
                Files.createDirectories(projectsRoot);
                logger.info("Created projects directory at {}", projectsRoot);
            } catch (IOException e) {
                logger.error("Failed to create projects directory: {}", projectsRoot, e);
                return Collections.emptyList();
            }
        }

        if (!Files.isDirectory(projectsRoot)) {
            logger.error("Projects root path is not a directory: {}", projectsRoot);
            return Collections.emptyList();
        }

        Map<String, Project> projectMap = new LinkedHashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectsRoot)) {
            for (Path folder : stream) {
                if (Files.isDirectory(folder)) {
                    try {
                        Project project = parser.parseProject(folder);
                        if (projectMap.containsKey(project.getId())) {
                            logger.warn("Duplicate project ID '{}' found in folder {}. Skipping.",
                                    project.getId(), folder);
                        } else {
                            projectMap.put(project.getId(), project);
                            logger.info("Loaded project '{}' from {}", project.getName(), folder);
                        }
                    } catch (ProjectParser.InvalidProjectException e) {
                        logger.warn("Skipping invalid project in folder {}: {}", folder, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading projects directory {}", projectsRoot, e);
        }

        return new ArrayList<>(projectMap.values());
    }
}