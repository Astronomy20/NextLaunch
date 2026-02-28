package com.nextlaunch;

import com.nextlaunch.core.ProjectManager;
import com.nextlaunch.models.Project;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class NextLaunch {

    private static final Path PROJECTS_ROOT = Path.of("projects");

    public static void main(String[] args) {

        ProjectManager manager = new ProjectManager(PROJECTS_ROOT);
        manager.loadProjects();

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== NextLaunch Terminal Demo ===");

        while (true) {
            System.out.println("""
                    
                    1. List projects
                    2. Run project
                    3. Refresh projects
                    0. Exit
                    """);

            System.out.print("Select option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listProjects(manager);

                case "2" -> {
                    System.out.print("Enter project ID: ");
                    String id = scanner.nextLine().trim();
                    runProject(manager, id);
                }

                case "3" -> {
                    manager.loadProjects();
                    System.out.println("Projects refreshed.");
                }

                case "0" -> {
                    System.out.println("Exiting NextLaunch.");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void listProjects(ProjectManager manager) {
        List<Project> projects = manager.getProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("\nAvailable Projects:");
        for (Project p : projects) {
            System.out.println("- " + p.getName() + " (ID: " + p.getId() + ")");
        }
    }

    private static void runProject(ProjectManager manager, String id) {
        if (!manager.containsProject(id)) {
            System.out.println("Project not found.");
            return;
        }

        try {
            Process process = manager.runProject(id).join();
            System.out.println("Project started successfully (PID: " + process.pid() + ")");
        } catch (Exception e) {
            System.out.println("Failed to launch project: " + e.getMessage());
        }
    }
}