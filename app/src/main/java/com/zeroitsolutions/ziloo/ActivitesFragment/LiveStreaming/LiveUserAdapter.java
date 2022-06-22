package com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.CustomViewHolder> {

    public Context context;
    ArrayList<LiveUserModel> dataList;

    AdapterClickListener adapterClickListener;


    public LiveUserAdapter(Context context, ArrayList<LiveUserModel> userDatalist, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = userDatalist;
        this.adapterClickListener = adapterClickListener;

    }

    @NonNull
    @Override
    public LiveUserAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_live_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final LiveUserAdapter.CustomViewHolder holder, final int i) {

        final LiveUserModel item = dataList.get(i);

        holder.bind(i, item, adapterClickListener);

        holder.name.setText(item.getUser_name());
        if (item.getUser_picture() != null && !item.getUser_picture().equals("")) {

            holder.image.setController(Functions.frescoImageLoad(item.getUser_picture(), holder.image, false));

        }

    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public SimpleDraweeView image;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.username);
            image = view.findViewById(R.id.image);

        }

        public void bind(final int pos, final LiveUserModel item,
                         final AdapterClickListener adapterClickListener) {

            itemView.setOnClickListener(v -> adapterClickListener.onItemClick(v, pos, item));

        }

    }

}