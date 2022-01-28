package com.dduproject.expensetracker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dduproject.expensetracker.models.Member;
import com.dduproject.expensetracker.models.User;

public class MembersHelper {
    private static final Member[] member_default = new Member[]{
            new Member(":Admin","Admin")
    };
    public static List<Member> getMembers(User user) {
        ArrayList<Member> members = new ArrayList<>();
        members.addAll(Arrays.asList(member_default));
        members.addAll(getCustomMembers(user));
        return members;
    }
    public static List<Member> getCustomMembers(User user) {
        ArrayList<Member> members = new ArrayList<>();
        for(Map.Entry<String, Member> member : user.members.entrySet()) {
            String memberId = member.getKey();
            String memberName = member.getValue().name;
            members.add(new Member(memberId,memberName));
        }
        return members;
    }
}
