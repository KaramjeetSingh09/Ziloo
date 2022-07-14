package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.Models.FollowingModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class FollowingShareAdapter extends RecyclerView.Adapter<FollowingShareAdapter.CustomViewHolder> {

    public Context context;
    ArrayList<FollowingModel> dataList;
    AdapterClickListener adapter_clickListener;

    public FollowingShareAdapter(Context context, ArrayList<FollowingModel> arrayList, AdapterClickListener listener) {
        this.context = context;
        dataList = arrayList;
        this.adapter_clickListener = listener;
    }

    @NonNull
    @Override
    public FollowingShareAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_followers_share_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final FollowingShareAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        FollowingModel item = dataList.get(i);
        holder.user_name.setText(item.username);

        if (item.profile_pic != null && !item.profile_pic.equals("")) {
            holder.user_image.setController(Functions.frescoImageLoad(item.profile_pic, holder.user_image, false));

        }

        if (item.is_select) {
            holder.ziloo_icon.setVisibility(View.VISIBLE);
            holder.user_image.setAlpha((float) 0.5);
        } else {
            holder.ziloo_icon.setVisibility(View.GONE);
            holder.user_image.setAlpha((float) 1.0);
        }

        holder.bind(i, dataList.get(i), adapter_clickListener);

    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView user_image;
        TextView user_name;
        ImageView ziloo_icon;

        public CustomViewHolder(View view) {
            super(view);
            ziloo_icon = view.findViewById(R.id.ziloo_icon);
            user_image = view.findViewById(R.id.user_image);
            user_name = view.findViewById(R.id.user_name);
        }

        public void bind(final int pos, final FollowingModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(v, pos, item));
        }
    }
}