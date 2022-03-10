package com.dduce.expensetracker.models;


public class Category {
    public String id;
    public String name;

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getCategoryName() {
        return name;
    }
    public void setCategoryName(String name) {
        this.name = name;
    }
    public String getCategoryId() {
        return id;
    }
    public void setCategoryId(String id) {
        this.id = id;
    }
}
