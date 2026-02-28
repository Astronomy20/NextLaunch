package com.nextlaunch.core;

import com.nextlaunch.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Central coordination service for managing projects.
 */
public class ProjectManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectManager.class);

    private final Path projectsRoot;
    private final ProjectScanner scanner;
    private final ProjectRunner runner;

    private final Map<String, Project> loadedProjects = new LinkedHashMap<>();

    public ProjectManager(Path projectsRoot) {
        Objects.requireNonNull(projectsRoot, "Projects root cannot be null");

        this.projectsRoot = projectsRoot;

        ProjectParser parser = new ProjectParser();
        this.scanner = new ProjectScanner(parser);
        this.runner = new ProjectRunner();
    }

    /**
     * Loads or reloads all projects from disk.
     */
    public synchronized void loadProjects() {
        LOGGER.info("Scanning projects directory...");
        List<Project> projects = scanner.scanProjects(projectsRoot);

        loadedProjects.clear();

        for (Project project : projects) {
            loadedProjects.put(project.getId(), project);
        }

        LOGGER.info("Loaded {} projects.", loadedProjects.size());
    }

    /**
     * Returns an unmodifiable list of loaded projects.
     */
    public List<Project> getProjects() {
        return List.copyOf(loadedProjects.values());
    }

    /**
     * Returns a project by ID.
     */
    public Optional<Project> getProjectById(String id) {
        return Optional.ofNullable(loadedProjects.get(id));
    }

    /**
     * Checks if project exists.
     */
    public boolean containsProject(String id) {
        return loadedProjects.containsKey(id);
    }

    /**
     * Runs a project by ID.
     *
     * @param id project ID
     * @return CompletableFuture of Process
     */
    public CompletableFuture<Process> runProject(String id) {
        Project project = loadedProjects.get(id);

        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + id);
        }

        return runner.runProject(project);
    }
}