package com.nextlaunch.config.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateSettings {

    private boolean autoCheck = true;

    public UpdateSettings() {}

    public boolean isAutoCheck() { return autoCheck; }
    public void setAutoCheck(boolean autoCheck) { this.autoCheck = autoCheck; }
}