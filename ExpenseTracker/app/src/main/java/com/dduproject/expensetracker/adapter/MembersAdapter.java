package com.dduproject.expensetracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.activities.EditMemberActivity;
import com.dduproject.expensetracker.models.Member;

public class MembersAdapter extends ArrayAdapter<Member> implements View.OnClickListener {

    private final Activity activity;
    Context context;

    public MembersAdapter(Activity activity, List<Member> data, Context context) {
        super(context, R.layout.row_home, data);
        this.context = context;
        this.activity = activity;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.row_member, parent, false);
        }
        Member member = getItem(position);
        TextView tvMemberName = listItem.findViewById(R.id.tvMemberName);
        tvMemberName.setText(member.getMemberName());


        listItem.setOnClickListener(v -> {
            Intent intent = new Intent(activity, EditMemberActivity.class);
            intent.putExtra("member-id", member.getMemberId());
            intent.putExtra("member-name", member.getMemberName());
            activity.startActivity(intent);
        });
        return listItem;
    }


}
