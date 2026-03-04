package com.nextlaunch.ui;

import com.nextlaunch.config.ConfigManager;
import com.nextlaunch.config.LauncherConfig;
import com.nextlaunch.manager.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Settings UI with a sidebar-navigation layout.*
 * Sections:
 *   - Appearance  (theme picker)
 *   - Updates     (placeholder)
 *   - Account     (OAuth placeholder)
 */
public class SettingsView {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsView.class);

    private final BorderPane root = new BorderPane();
    private final ConfigManager configManager;
    private final LauncherConfig config;
    private final ThemeManager themeManager;

    private Button activeNavButton = null;

    private final StackPane contentArea = new StackPane();

    public SettingsView(ConfigManager configManager, LauncherConfig config, ThemeManager themeManager) {
        this.configManager = configManager;
        this.config = config;
        this.themeManager = themeManager;

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
        root.setLeft(buildSidebar());
        root.setCenter(buildContent());
    }

    private BorderPane buildHeader() {
        Label title = new Label("Settings");
        title.getStyleClass().add("header-title");

        BorderPane header = new BorderPane();
        header.setLeft(title);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-bar");

        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("settings-sidebar");
        sidebar.setPadding(new Insets(20, 8, 16, 8));
        sidebar.setPrefWidth(180);

        Label title = new Label("Settings");
        title.getStyleClass().add("header-title");
        title.setPadding(new Insets(0, 8, 16, 8));

        Label sectionLabel = new Label("PREFERENCES");
        sectionLabel.getStyleClass().add("settings-nav-section-label");

        Button appearanceBtn = navButton("Appearance");
        Button updatesBtn    = navButton("Updates");
        Button accountBtn    = navButton("Account");

        appearanceBtn.setOnAction(e -> switchTo(appearanceBtn, buildAppearancePane()));
        updatesBtn.setOnAction(e ->    switchTo(updatesBtn,    buildUpdatesPane()));
        accountBtn.setOnAction(e ->    switchTo(accountBtn,    buildAccountPane()));

        sidebar.getChildren().addAll(title, sectionLabel, appearanceBtn, updatesBtn, accountBtn);

        switchTo(appearanceBtn, buildAppearancePane());

        return sidebar;
    }

    private ScrollPane buildContent() {
        contentArea.getStyleClass().add("settings-content-area");
        contentArea.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("scroll-pane");
        return scroll;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    private void switchTo(Button navBtn, Parent pane) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("settings-nav-button-active");
        }
        navBtn.getStyleClass().add("settings-nav-button-active");
        activeNavButton = navBtn;

        contentArea.getChildren().setAll(pane);
    }

    // -------------------------------------------------------------------------
    // Appearance Pane
    // -------------------------------------------------------------------------

    private VBox buildAppearancePane() {
        VBox pane = new VBox(24);
        pane.getStyleClass().add("settings-pane");
        pane.setPadding(new Insets(32));

        Label heading = sectionHeading("Appearance");
        Label sub     = sectionSubtitle("Choose a theme for the launcher.");

        Label themeLabel = fieldLabel();

        List<String> themes = themeManager.getAvailableThemes();
        ToggleGroup toggleGroup = new ToggleGroup();

        VBox themeList = new VBox(10);
        themeList.getStyleClass().add("settings-theme-list");

        String activeTheme = themeManager.getActiveTheme();

        for (String theme : themes) {
            RadioButton rb = new RadioButton(formatThemeName(theme));
            rb.setToggleGroup(toggleGroup);
            rb.getStyleClass().add("settings-radio");
            rb.setUserData(theme);
            rb.setSelected(theme.equals(activeTheme));

            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    onThemeSelected(theme);
                }
            });

            themeList.getChildren().add(rb);
        }

        if (themes.isEmpty()) {
            themeList.getChildren().add(new Label("No themes found."));
        }

        Separator sep = new Separator();
        sep.getStyleClass().add("settings-separator");

        Label comingSoon = new Label("🔍  Browse more themes — coming soon");
        comingSoon.getStyleClass().add("settings-coming-soon");

        pane.getChildren().addAll(heading, sub, themeLabel, themeList, sep, comingSoon);
        return pane;
    }

    // -------------------------------------------------------------------------
    // Updates Pane
    // -------------------------------------------------------------------------

    private VBox buildUpdatesPane() {
        VBox pane = new VBox(24);
        pane.getStyleClass().add("settings-pane");
        pane.setPadding(new Insets(32));

        Label heading = sectionHeading("Updates");
        Label sub     = sectionSubtitle("Manage how NextLaunch checks for updates.");

        Label placeholder = placeholderLabel("Update settings coming soon.");

        pane.getChildren().addAll(heading, sub, placeholder);
        return pane;
    }

    // -------------------------------------------------------------------------
    // Account Pane
    // -------------------------------------------------------------------------

    private VBox buildAccountPane() {
        VBox pane = new VBox(24);
        pane.getStyleClass().add("settings-pane");
        pane.setPadding(new Insets(32));

        Label heading = sectionHeading("Account");
        Label sub     = sectionSubtitle("Connect your GitHub account to browse and download projects.");

        Label placeholder = placeholderLabel("GitHub OAuth integration coming soon.");

        pane.getChildren().addAll(heading, sub, placeholder);
        return pane;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void onThemeSelected(String themeName) {
        themeManager.applyTheme(root.getScene(), themeName);

        try {
            configManager.save(config);
            LOGGER.info("Theme '{}' saved to config.", themeName);
        } catch (IOException e) {
            LOGGER.error("Failed to save config after theme change.", e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("settings-nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    private Label sectionHeading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("settings-heading");
        return label;
    }

    private Label sectionSubtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("settings-subtitle");
        return label;
    }

    private Label fieldLabel() {
        Label label = new Label("Theme");
        label.getStyleClass().add("settings-field-label");
        return label;
    }

    private Label placeholderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("settings-placeholder");
        return label;
    }

    /** "dark_mode" -> "Dark Mode" */
    private String formatThemeName(String raw) {
        return raw.replace("_", " ")
                .substring(0, 1).toUpperCase()
                + raw.replace("_", " ").substring(1);
    }
}