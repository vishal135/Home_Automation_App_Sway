package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models;

public class CategoryModel {

    private int image;
    private String title;
    private String category;

    public CategoryModel() {
    }

    public CategoryModel(int image, String title, String category) {
        this.image = image;
        this.title = title;
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
