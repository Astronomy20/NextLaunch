package com.nextlaunch.ui;

import com.nextlaunch.manager.ProjectManager;
import com.nextlaunch.models.Project;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class MainView {

    private final BorderPane root = new BorderPane();
    private final FlowPane projectContainer = new FlowPane();
    private Button themeToggle = new Button();

    private final ProjectManager projectManager =
            new ProjectManager(Path.of("projects"));

    private boolean isDarkMode = false;

    public MainView() {
        initializeLayout();
        loadProjects();
    }

    public Parent getRoot() {
        return root;
    }

    private void initializeLayout() {

        root.getStyleClass().add("root-pane");

        Label header = new Label("NextLaunch");
        header.getStyleClass().add("header-title");

        Button reloadButton = new Button("\uD83D\uDD04");
        reloadButton.getStyleClass().add("reload-button");
        reloadButton.setOnAction(e -> reloadProjects());

        themeToggle = new Button("🌙");
        themeToggle.getStyleClass().add("reload-button");
        themeToggle.setOnAction(e -> toggleTheme());

        VBox rightBox = new VBox(10, themeToggle, reloadButton);
        rightBox.setPadding(new Insets(0, 0, 0, 0));

        BorderPane headerBox = new BorderPane();
        headerBox.setLeft(header);
        headerBox.setRight(rightBox);
        headerBox.setPadding(new Insets(20));
        headerBox.getStyleClass().add("header-bar");

        root.setTop(headerBox);

        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        projectContainer.setPadding(new Insets(25));

        ScrollPane scrollPane = new ScrollPane(projectContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");

        root.setCenter(scrollPane);
    }

    private void loadProjects() {
        projectManager.loadProjects();
        List<Project> projects = projectManager.getProjects();

        for (Project project : projects) {
            ProjectCard card = new ProjectCard(project, projectManager);
            projectContainer.getChildren().add(card);
        }
    }

    private void reloadProjects() {
        projectContainer.getChildren().clear();
        loadProjects();
    }

    private void toggleTheme() {
        Scene scene = root.getScene();
        if (scene == null) return;

        scene.getStylesheets().clear();
        if (isDarkMode) {
            URL lightCss = getClass().getResource("/styles/light_mode.css");
            if (lightCss != null) {
                scene.getStylesheets().add(lightCss.toExternalForm());
                isDarkMode = false;
                themeToggle.setText("🌙");
            }
        } else {
            URL darkCss = getClass().getResource("/styles/dark_mode.css");
            if (darkCss != null) {
                scene.getStylesheets().add(darkCss.toExternalForm());
                isDarkMode = true;
                themeToggle.setText("☀");
            }
        }
    }
}