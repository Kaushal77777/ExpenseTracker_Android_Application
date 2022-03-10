package com.dduce.expensetracker.models;

public class Entry {
    public String id;
    public String category;
    public String member;
    public String name;
    public long timestamp;
    public long amount;
    public Entry() {

    }
    public Entry(String category, String member, String name, long timestamp, long amount) {
        this.category = category;
        this.member = member;
        this.name = name;
        this.timestamp = timestamp;
        this.amount = amount;
    }
    public Entry(String id, String category, String member, String name, long timestamp, long amount) {
        this.id = id;
        this.category = category;
        this.member = member;
        this.name = name;
        this.timestamp = timestamp;
        this.amount = amount;
    }

}