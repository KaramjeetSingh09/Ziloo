package com.zeroitsolutions.ziloo.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Models.FollowingModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.CustomViewHolder> {

    public OnItemClickListener listener;
    ArrayList<FollowingModel> datalist;

    public SuggestionAdapter(ArrayList<FollowingModel> arrayList, OnItemClickListener listener) {
        datalist = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion_follower, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        FollowingModel item = datalist.get(i);

        holder.tvUserName.setText(item.username);
        holder.tvName.setText(item.first_name + " " + item.last_name);

        if (item.profile_pic != null && !item.profile_pic.equals("")) {
            holder.userImage.setController(Functions.frescoImageLoad(item.profile_pic, holder.userImage, false));
        }

        holder.bind(i, datalist.get(i), listener);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, FollowingModel item);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView userImage;
        TextView tvName, tvUserName, tvFollowBtn;
        ImageView ivCross;

        public CustomViewHolder(View view) {
            super(view);
            ivCross = view.findViewById(R.id.ivCross);
            userImage = view.findViewById(R.id.user_image);
            tvName = view.findViewById(R.id.tvName);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvFollowBtn = view.findViewById(R.id.tvFollowBtn);
        }

        public void bind(final int pos, final FollowingModel item, final OnItemClickListener listener) {
            tvFollowBtn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            userImage.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            ivCross.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });
        }
    }
}