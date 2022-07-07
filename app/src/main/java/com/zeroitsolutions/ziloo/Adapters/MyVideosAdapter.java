package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

public class MyVideosAdapter extends RecyclerView.Adapter<MyVideosAdapter.CustomViewHolder> {

    public Context context;
    AdapterClickListener adapterClickListener;
    private ArrayList<HomeModel> dataList;

    public MyVideosAdapter(Context context, ArrayList<HomeModel> dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.adapterClickListener = adapterClickListener;
    }

    @NonNull
    @Override
    public MyVideosAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myvideo_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final MyVideosAdapter.CustomViewHolder holder, final int i) {
        final HomeModel item = dataList.get(i);
        holder.setIsRecyclable(false);
        try {
            if (Constants.IS_SHOW_GIF) {
                holder.thumbImage.setController(Functions.frescoImageLoad(item.gif, holder.thumbImage, true));
            } else {
                if (item.thum != null && !item.thum.equals("")) {
                    holder.thumbImage.setController(Functions.frescoImageLoad(item.thum, holder.thumbImage, false));
                }
            }
        } catch (Exception e) {
            Functions.printLog(Constants.tag, e.toString());
        }

        holder.viewTxt.setText(item.views);
        holder.viewTxt.setText(Functions.getSuffix(item.views));
        holder.bind(i, item, adapterClickListener);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView thumbImage;
        TextView viewTxt;

        public CustomViewHolder(View view) {
            super(view);

            thumbImage = view.findViewById(R.id.thumb_image);
            viewTxt = view.findViewById(R.id.view_txt);

        }

        public void bind(final int position, final HomeModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                adapterClickListener.onItemClick(v, position, item);
            });
        }
    }
}