package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models;

public class FanApplianceModel {
    private String fanName;
    private boolean fanStatus;

    public FanApplianceModel(String fanName, boolean fanStatus) {
        this.fanName = fanName;
        this.fanStatus = fanStatus;
    }

    public String getFanName() {
        return fanName;
    }

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    public boolean isFanStatus() {
        return fanStatus;
    }

    public void setFanStatus(boolean fanStatus) {
        this.fanStatus = fanStatus;
    }
}
