package com.dduce.expensetracker.models;

public class StatisticsModel {
    private final float percentage;
    private final long money;
    private final String categoryName;

    public StatisticsModel(String categoryName, long money, float percentage) {
        this.categoryName = categoryName;
        this.money = money;
        this.percentage = percentage;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public long getMoney() {
        return money;
    }

    public float getPercentage() {
        return percentage;
    }
}
