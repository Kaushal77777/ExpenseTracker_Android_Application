package com.dduce.expensetracker.utils;
import com.dduce.expensetracker.models.Category;
import com.dduce.expensetracker.models.Entry;
import com.dduce.expensetracker.models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
public class DBHelper {
    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
    public static FirebaseUser getLoginUser(){
        return getFirebaseAuth().getCurrentUser();
    }
    public static String getUUID(){
        return getLoginUser() != null ? getLoginUser().getUid() : "";
    }

    public static DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }
    public static DatabaseReference getUserReference(){
        return getDatabaseReference().child("users").child(getUUID());
    }
    public static DatabaseReference getEntryReference(){
        return getUserReference().child("entries");
    }
    public static DatabaseReference getMemberReference(){
        return getUserReference().child("members");
    }
    public static DatabaseReference getCategoryReference(){
        return getUserReference().child("categories");
    }

    public static Query getEntries(){
        return getEntryReference().orderByChild("timestamp");
    }
    public static Query getMembers(){
        return getMemberReference();
    }
    public static Query getCategories(){
        return getCategoryReference();
    }

    public static void addCategory(Category category){
        getCategoryReference().push().setValue(category);
    }
    public static void addMember(Member member){
        getMemberReference().push().setValue(member);
    }
    public static void addEntry(Entry entry){
        getEntryReference().push().setValue(entry);
    }

    public static void updateCategory(String id, Category category){
        getCategoryReference().child(id).setValue(category);
    }
    public static void updateMember(String id, Member member){
        getMemberReference().child(id).setValue(member);
    }
    public static void updateEntry(String id, Entry entry){
        getEntryReference().child(id).setValue(entry);
    }

    public static void deleteCategory(String id){
        getCategoryReference().child(id).removeValue();
    }
    public static void deleteMember(String id){
        getMemberReference().child(id).removeValue();
    }
    public static void deleteEntry(String id){
        getEntryReference().child(id).removeValue();
    }
}
