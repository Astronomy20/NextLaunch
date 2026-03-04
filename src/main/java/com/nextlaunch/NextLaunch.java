package com.nextlaunch;

import com.nextlaunch.config.ConfigManager;
import com.nextlaunch.config.LauncherConfig;
import com.nextlaunch.manager.ProjectManager;
import com.nextlaunch.models.Project;
import com.nextlaunch.manager.ThemeManager;
import com.nextlaunch.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class NextLaunch extends Application {

    private static final Path PROJECTS_ROOT  = Path.of("projects");
    private static final Path CONFIG_PATH    = Path.of("config", "launcher.json");

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        for (String arg : args) {
            if ("--terminal".equalsIgnoreCase(arg)) {
                runTerminal();
                return;
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ConfigManager configManager = new ConfigManager(CONFIG_PATH);
        LauncherConfig config = configManager.loadOrDefault();

        ThemeManager themeManager = new ThemeManager(config.themeSettings);

        MainView mainView = new MainView(configManager, config, themeManager);
        Scene scene = new Scene(mainView.getRoot(), 900, 620);

        themeManager.applyTheme(scene);

        stage.setTitle("NextLaunch");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------------------------------------------------------
    // Terminal mode
    // -------------------------------------------------------------------------

    private static void runTerminal() {
        ProjectManager manager = new ProjectManager(PROJECTS_ROOT);
        manager.loadProjects();

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== NextLaunch Terminal Demo ===");

        while (true) {
            System.out.println("""
                    
                    1. List projects
                    2. Run project
                    3. Refresh projects
                    4. Launch GUI
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

                case "4" -> {
                    System.out.println("Launching GUI...");
                    new Thread(() -> Application.launch(NextLaunch.class)).start();
                    return;
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