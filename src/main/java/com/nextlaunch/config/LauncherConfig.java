package com.nextlaunch.config;

import com.nextlaunch.config.settings.OAuthSettings;
import com.nextlaunch.config.settings.ThemeSettings;
import com.nextlaunch.config.settings.UpdateSettings;

public class LauncherConfig {

    private final int configVersion = 1;

    public ThemeSettings themeSettings;
    public UpdateSettings updateSettings;
    public OAuthSettings oAuthSettings;

    public LauncherConfig() {
        this.themeSettings = new ThemeSettings();
        this.updateSettings = new UpdateSettings();
        this.oAuthSettings = new OAuthSettings();
    }

    public int getConfigVersion() {
        return configVersion;
    }
}