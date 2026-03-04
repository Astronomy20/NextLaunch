package com.nextlaunch.ui;

import com.nextlaunch.config.ConfigManager;
import com.nextlaunch.config.LauncherConfig;
import com.nextlaunch.models.Project;
import com.nextlaunch.manager.ProjectManager;
import com.nextlaunch.manager.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.nio.file.Path;
import java.util.List;

public class MainView {

    private static final Path PROJECTS_ROOT = Path.of("projects");

    private final BorderPane root = new BorderPane();

    private final ConfigManager configManager;
    private final LauncherConfig config;
    private final ThemeManager themeManager;

    private final ProjectManager projectManager;
    private final FlowPane projectContainer = new FlowPane();

    private Button activeNavButton = null;
    private final StackPane pageArea = new StackPane();

    public MainView(ConfigManager configManager, LauncherConfig config, ThemeManager themeManager) {
        this.configManager = configManager;
        this.config = config;
        this.themeManager = themeManager;
        this.projectManager = new ProjectManager(PROJECTS_ROOT);

        build();
    }

    public Parent getRoot() {
        return root;
    }

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------

    private void build() {
        root.getStyleClass().add("root-pane");
        root.setTop(buildHeader());
        root.setCenter(pageArea);

        navigateTo(buildProjectsPage(), null);
    }

    private BorderPane buildHeader() {
        Label title = new Label("NextLaunch");
        title.getStyleClass().add("header-title");

        Button projectsBtn = navButton("Projects");
        Button settingsBtn = navButton("Settings");

        HBox nav = new HBox(6, projectsBtn, settingsBtn);
        nav.setAlignment(Pos.CENTER_LEFT);

        projectsBtn.setOnAction(e -> navigateTo(buildProjectsPage(), projectsBtn));
        settingsBtn.setOnAction(e -> navigateTo(buildSettingsPage(), settingsBtn));

        BorderPane header = new BorderPane();
        header.setLeft(title);
        header.setRight(nav);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-bar");

        setActiveNav(projectsBtn);

        return header;
    }

    // -------------------------------------------------------------------------
    // Pages
    // -------------------------------------------------------------------------

    private ScrollPane buildProjectsPage() {
        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        projectContainer.setPadding(new Insets(25));

        loadProjects();

        ScrollPane scroll = new ScrollPane(projectContainer);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("scroll-pane");
        return scroll;
    }

    private Parent buildSettingsPage() {
        SettingsView settingsView = new SettingsView(configManager, config, themeManager);
        return settingsView.getRoot();
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    private void navigateTo(Parent page, Button navBtn) {
        pageArea.getChildren().setAll(page);
        if (navBtn != null) {
            setActiveNav(navBtn);
        }
    }

    private void setActiveNav(Button btn) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-button-active");
        }
        btn.getStyleClass().add("nav-button-active");
        activeNavButton = btn;
    }

    // -------------------------------------------------------------------------
    // Projects
    // -------------------------------------------------------------------------

    private void loadProjects() {
        projectContainer.getChildren().clear();
        projectManager.loadProjects();

        List<Project> projects = projectManager.getProjects();

        if (projects.isEmpty()) {
            Label empty = new Label("No projects found. Add a project to the projects/ folder.");
            empty.getStyleClass().add("settings-placeholder");
            empty.setPadding(new Insets(40));
            projectContainer.getChildren().add(empty);
            return;
        }

        for (Project project : projects) {
            ProjectCard card = new ProjectCard(project, projectManager);
            projectContainer.getChildren().add(card);
        }
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        return btn;
    }
}