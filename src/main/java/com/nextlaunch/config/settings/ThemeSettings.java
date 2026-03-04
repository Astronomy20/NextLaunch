package com.nextlaunch.config.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ThemeSettings {

    public static final String DEFAULT_THEME = "light_mode";

    private String activeTheme;
    private String defaultTheme;

    public ThemeSettings() {
        this.defaultTheme = DEFAULT_THEME;
        this.activeTheme = DEFAULT_THEME;
    }

    public String getActiveTheme() {
        return activeTheme;
    }

    public void setActiveTheme(String activeTheme) {
        this.activeTheme = activeTheme;
    }
}