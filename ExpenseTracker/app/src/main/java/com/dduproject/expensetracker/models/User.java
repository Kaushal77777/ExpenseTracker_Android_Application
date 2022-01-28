package com.dduproject.expensetracker.models;
import java.util.HashMap;
import java.util.Map;

public class User {

    public long wallet;
    public Map<String, Category> categories = new HashMap<>();
    public Map<String, Member> members = new HashMap<>();

    public User() {

    }
}