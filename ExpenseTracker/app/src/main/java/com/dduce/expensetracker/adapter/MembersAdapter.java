package com.dduce.expensetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dduce.expensetracker.activities.EditMemberActivity;
import com.dduce.expensetracker.databinding.RowMemberBinding;
import com.dduce.expensetracker.models.Member;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MembersAdapter extends FirebaseRecyclerAdapter<Member, MembersAdapter.MemberViewHolder> {

    Context mContext;

    public MembersAdapter(Context mContext, @NonNull FirebaseRecyclerOptions<Member> options) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(MemberViewHolder viewHolder, int position, @NonNull Member member) {
        viewHolder.binding.tvMemberName.setText(member.name);
        viewHolder.binding.btnEditMember.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EditMemberActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("member-id", member.getMemberId());
            intent.putExtra("member-name", member.getMemberName());
            mContext.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberViewHolder(RowMemberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder{
        RowMemberBinding binding;
        public MemberViewHolder(RowMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
