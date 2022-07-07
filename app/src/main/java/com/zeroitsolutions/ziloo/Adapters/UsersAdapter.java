package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.Models.UsersModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<UsersModel> datalist;
    AdapterClickListener adapterClickListener;

    public UsersAdapter(Context context, ArrayList<UsersModel> arrayList, AdapterClickListener adapterClickListener) {
        this.context = context;
        datalist = arrayList;
        this.adapterClickListener = adapterClickListener;
    }

    @NonNull
    @Override
    public UsersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_users_list2, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final UsersAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);
        UsersModel item = (UsersModel) datalist.get(i);

        if (item.profile_pic != null && !item.profile_pic.equals("")) {
            holder.image.setController(Functions.frescoImageLoad(item.profile_pic,holder.image,false));
        }

        holder.usernameTxt.setText(item.username);

        if (!item.first_name.equals(""))
            holder.nameTxt.setText(item.first_name + " " + item.last_name);
        else
            holder.nameTxt.setVisibility(View.GONE);


        holder.followerVideoTxt.setText(Functions.getSuffix(item.followers_count) + " "+context.getString(R.string.followers)+" " + item.videos + " "+context.getString(R.string.videos));
        holder.bind(i, item, adapterClickListener);

    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        TextView usernameTxt, nameTxt, followerVideoTxt;

        public CustomViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            usernameTxt = view.findViewById(R.id.username_txt);
            followerVideoTxt = view.findViewById(R.id.follower_video_txt);
            nameTxt = view.findViewById(R.id.name_txt);
        }

        public void bind(final int pos, final Object item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });
        }
    }
}