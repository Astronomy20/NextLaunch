package com.nextlaunch.ui;

import com.nextlaunch.manager.ProjectManager;
import com.nextlaunch.models.Project;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProjectCard extends VBox {

    private final Project project;
    private final ProjectManager manager;

    public ProjectCard(Project project, ProjectManager manager) {
        this.project = project;
        this.manager = manager;

        initialize();
    }

    private void initialize() {

        getStyleClass().add("project-card");
        setPadding(new Insets(20));
        setSpacing(10);
        setPrefWidth(220);

        Label nameLabel = new Label(project.getName());
        nameLabel.getStyleClass().add("project-name");

        Label idLabel = new Label("ID: " + project.getId());
        idLabel.getStyleClass().add("project-id");

        Label versionLabel = new Label("Version: " + project.getVersion());
        versionLabel.getStyleClass().add("project-version");

        Button launchButton = new Button("Launch");
        launchButton.getStyleClass().add("launch-button");

        launchButton.setOnAction(e ->
                manager.runProject(project.getId())
        );

        getChildren().addAll(nameLabel, idLabel, versionLabel, launchButton);
    }
}