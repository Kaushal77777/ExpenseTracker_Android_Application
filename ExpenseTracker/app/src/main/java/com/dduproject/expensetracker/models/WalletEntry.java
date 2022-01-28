package com.dduproject.expensetracker.models;

public class WalletEntry {
    public String id;
    public String category;
    public String member;
    public String name;
    public long timestamp;
    public long balanceDifference;
    public WalletEntry() {

    }
    public WalletEntry(String category, String member, String name, long timestamp, long balanceDifference) {
        this.category = category;
        this.member = member;
        this.name = name;
        this.timestamp = -timestamp;
        this.balanceDifference = balanceDifference;
    }
    public WalletEntry(String id, String category, String member, String name, long timestamp, long balanceDifference) {
        this.id = id;
        this.category = category;
        this.member = member;
        this.name = name;
        this.timestamp = -timestamp;
        this.balanceDifference = balanceDifference;
    }

}