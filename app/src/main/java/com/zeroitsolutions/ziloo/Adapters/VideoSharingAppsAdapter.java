package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.R;

import java.util.List;

public class VideoSharingAppsAdapter extends RecyclerView.Adapter<VideoSharingAppsAdapter.CustomViewHolder> {

    public Context context;
    private OnItemClickListener listener;
    private List<ResolveInfo> dataList;

    public VideoSharingAppsAdapter(Context context, List<ResolveInfo> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_videosharingapps_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final ResolveInfo item = dataList.get(i);
        holder.setIsRecyclable(false);

        try {

            holder.bind(i, item, listener);
            holder.name_txt.setText(item.loadLabel(context.getPackageManager()));
            holder.image.setImageDrawable(item.loadIcon(context.getPackageManager()));

        } catch (Exception e) {
//
        }
    }


    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, ResolveInfo item, View view);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView name_txt;
        ImageView image;

        public CustomViewHolder(View view) {
            super(view);

            name_txt = view.findViewById(R.id.name_txt);
            image = view.findViewById(R.id.image);
        }

        public void bind(final int postion, final ResolveInfo item, final VideoSharingAppsAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);

            });
        }
    }
}