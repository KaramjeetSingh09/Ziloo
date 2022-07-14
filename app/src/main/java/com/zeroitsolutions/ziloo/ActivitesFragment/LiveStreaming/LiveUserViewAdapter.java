package com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming;

import android.annotation.SuppressLint;
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

public class LiveUserViewAdapter extends RecyclerView.Adapter<LiveUserViewAdapter.CustomViewHolder> {

    ArrayList<LiveUserModel> dataList;
    AdapterClickListener adapterClickListener;

    public LiveUserViewAdapter(ArrayList<LiveUserModel> userDatalist, AdapterClickListener adapterClickListener) {
        this.dataList = userDatalist;
        this.adapterClickListener = adapterClickListener;

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_live_view_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {

        final LiveUserModel item = dataList.get(i);

        holder.name.setText(item.getUser_name() + " " + holder.itemView.getContext().getString(R.string.joined));
        holder.image.setController(Functions.frescoImageLoad(item.getUser_picture(), holder.image, false));
        holder.bind(i, item, adapterClickListener);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public SimpleDraweeView image;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            image = view.findViewById(R.id.ivProfile);

        }

        public void bind(final int pos, final LiveUserModel item,
                         final AdapterClickListener adapterClickListener) {
            itemView.setOnClickListener(v -> adapterClickListener.onItemClick(v, pos, item));
        }
    }

}