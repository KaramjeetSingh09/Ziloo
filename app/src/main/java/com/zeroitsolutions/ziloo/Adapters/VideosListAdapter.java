package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

public class VideosListAdapter extends RecyclerView.Adapter<VideosListAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<HomeModel> datalist;
    AdapterClickListener adapterClickListener;

    public VideosListAdapter(Context context, ArrayList<HomeModel> arrayList, AdapterClickListener adapterClickListener) {
        this.context = context;
        datalist = arrayList;
        this.adapterClickListener = adapterClickListener;
    }

    @NonNull
    @Override
    public VideosListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_video_layout, viewGroup, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CustomViewHolder(view);

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @Override
    public void onBindViewHolder(final VideosListAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        final HomeModel item = (HomeModel) datalist.get(i);

        holder.usernameTxt.setText(item.username);
        holder.descriptionTxt.setText(item.video_description);

        if (item.thum != null && !item.thum.equals("")) {
            Uri uri = Uri.parse(item.thum);
            holder.image.setImageURI(uri);
        }

        if (item.profile_pic != null && !item.profile_pic.equals("")) {
            Uri uri = Uri.parse(item.profile_pic);
            holder.userImage.setImageURI(uri);
        }

        holder.firstLastNameTxt.setText(item.first_name.trim() + " " + item.last_name.trim());
        holder.likesCountTxt.setText(Functions.getSuffix(item.like_count));

        holder.bind(i, item, adapterClickListener);

    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image, userImage;
        TextView usernameTxt, descriptionTxt, firstLastNameTxt, likesCountTxt;

        public CustomViewHolder(View view) {
            super(view);
            userImage = view.findViewById(R.id.user_image);
            image = view.findViewById(R.id.image);
            usernameTxt = view.findViewById(R.id.username_txt);
            descriptionTxt = view.findViewById(R.id.description_txt);

            firstLastNameTxt = view.findViewById(R.id.first_last_name_txt);
            likesCountTxt = view.findViewById(R.id.likes_count_txt);
        }

        public void bind(final int pos, final HomeModel item, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });
        }
    }
}