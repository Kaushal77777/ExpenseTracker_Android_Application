package com.dduproject.expensetracker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dduproject.expensetracker.models.Category;
import com.dduproject.expensetracker.models.User;

public class CategoriesHelper {
    private static final Category[] category_default = new Category[]{
            new Category(":Others","Others"),
            new Category(":Clothing","Clothing"),
            new Category(":Food","Food"),
            new Category(":Fuel","Fuel"),
            new Category(":Gaming","Gaming"),
            new Category(":Gift","Gift"),
            new Category(":Holidays","Holidays"),
            new Category(":Home","Home"),
            new Category(":Kids","Kids"),
            new Category(":Pharmacy","Pharmacy"),
            new Category(":Repair","Repair"),
            new Category(":Shopping","Shopping"),
            new Category(":Sport","Sport"),
            new Category(":Transfer","Transfer"),
            new Category(":Transport","Transport"),
            new Category(":Work","Work")
    };
    public static List<Category> getCategories(User user) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.addAll(Arrays.asList(category_default));
        categories.addAll(getCustomCategories(user));
        return categories;
    }
    public static List<Category> getCustomCategories(User user) {
        ArrayList<Category> categories = new ArrayList<>();
        for(Map.Entry<String, Category> category : user.categories.entrySet()) {
            String categoryId = category.getKey();
            String categoryName = category.getValue().name;
            categories.add(new Category(categoryId,categoryName));
        }
        return categories;
    }
}
