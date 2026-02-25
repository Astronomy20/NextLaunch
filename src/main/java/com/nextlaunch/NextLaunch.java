package com.nextlaunch;

import com.nextlaunch.core.ProjectParser;
import com.nextlaunch.core.ProjectScanner;
import com.nextlaunch.models.Project;

import java.nio.file.Path;
import java.util.List;

public class NextLaunch {
    public static void main(String[] args) {
        Path projectsRoot = Path.of("projects");
        ProjectParser parser = new ProjectParser();
        ProjectScanner scanner = new ProjectScanner(parser);

        List<Project> projects = scanner.scanProjects(projectsRoot);

        for (Project p : projects) {
            System.out.println("Found project: " + p.getName() + " (ID: " + p.getId() + ")");
        }
    }
}