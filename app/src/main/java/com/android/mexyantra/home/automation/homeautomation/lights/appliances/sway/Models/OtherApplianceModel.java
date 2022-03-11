package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models;

public class OtherApplianceModel {
    private String otherName;
    private boolean otherStatus;

    public OtherApplianceModel(String otherName, boolean otherStatus) {
        this.otherName = otherName;
        this.otherStatus = otherStatus;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public boolean isOtherStatus() {
        return otherStatus;
    }

    public void setOtherStatus(boolean otherStatus) {
        this.otherStatus = otherStatus;
    }
}
