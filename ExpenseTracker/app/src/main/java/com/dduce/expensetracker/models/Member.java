package com.dduce.expensetracker.models;

public class Member {
    public String id;
    public String name;

    public Member() {

    }
    public Member(String name) {
        this.name = name;
    }

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getMemberName() {
        return name;
    }

    public String getMemberId() {
        return id;
    }
}
