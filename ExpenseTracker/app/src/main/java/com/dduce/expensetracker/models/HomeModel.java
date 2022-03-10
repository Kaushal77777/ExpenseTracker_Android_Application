package com.dduce.expensetracker.models;

public class HomeModel {
    private final long money;
    private final String categoryName;

    public HomeModel(String categoryName, long money) {
        this.categoryName = categoryName;
        this.money = money;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public long getMoney() {
        return money;
    }

}
