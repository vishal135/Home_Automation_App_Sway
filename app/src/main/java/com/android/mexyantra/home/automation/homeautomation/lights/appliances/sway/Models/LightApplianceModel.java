package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models;

public class LightApplianceModel {
    private String lightName;
    private boolean lightStatus;

    public LightApplianceModel(String lightName, boolean lightStatus) {
        this.lightName = lightName;
        this.lightStatus = lightStatus;
    }

    public String getLightName() {
        return lightName;
    }

    public void setLightName(String lightName) {
        this.lightName = lightName;
    }

    public boolean isLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(boolean lightStatus) {
        this.lightStatus = lightStatus;
    }
}
