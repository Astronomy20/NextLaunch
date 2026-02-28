package com.nextlaunch.core;

import com.nextlaunch.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Responsible for executing projects safely in a new terminal window.
 */
public class ProjectRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRunner.class);

    /**
     * Executes a project asynchronously.
     *
     * @param project the project to execute
     * @return CompletableFuture containing the Process
     */
    public CompletableFuture<Process> runProject(Project project) {
        Objects.requireNonNull(project, "Project cannot be null");

        return CompletableFuture.supplyAsync(() -> {
            try {
                return startProcess(project);
            } catch (IOException e) {
                LOGGER.error("Failed to execute project {}", project.getId(), e);
                throw new RuntimeException("Failed to execute project: " + project.getName(), e);
            }
        });
    }

    private Process startProcess(Project project) throws IOException {

        Path entryPath = project.getEntryPath();

        if (project.isExecutablePresent()) {
            throw new IllegalStateException("Entry file not found: " + entryPath);
        }

        String fileName = entryPath.getFileName().toString().toLowerCase(Locale.ROOT);

        ProcessBuilder processBuilder = getProcessBuilder(fileName, entryPath, project.getProjectDirectory());

        LOGGER.info("Launching project '{}' ({}) in a new terminal", project.getName(), project.getId());

        return processBuilder.start();
    }

    private static ProcessBuilder getProcessBuilder(String fileName, Path entryPath, Path workingDir) {
        ProcessBuilder processBuilder;

        if (fileName.endsWith(".exe") || fileName.endsWith(".bat")) {
            processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c", "start", "\"\"", entryPath.toAbsolutePath().toString()
            );

        } else if (fileName.endsWith(".jar")) {
            processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c", "start", "\"\"", "java", "-jar", entryPath.toAbsolutePath().toString()
            );

        } else {
            throw new UnsupportedOperationException(
                    "Unsupported file type: " + fileName
            );
        }

        processBuilder.directory(workingDir.toFile());
        return processBuilder;
    }
}