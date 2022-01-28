package com.dduproject.expensetracker.models;


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
    public String getCategoryId() {
        return id;
    }
}
