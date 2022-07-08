package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Models.DraftVideoModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.io.File;
import java.util.ArrayList;

public class DraftVideosAdapter extends RecyclerView.Adapter<DraftVideosAdapter.CustomViewHolder> {

    public Context context;
    private DraftVideosAdapter.OnItemClickListener listener;
    private ArrayList<DraftVideoModel> dataList;


    public DraftVideosAdapter(Context context, ArrayList<DraftVideoModel> dataList, DraftVideosAdapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public DraftVideosAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_galleryvideo_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        DraftVideosAdapter.CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final DraftVideosAdapter.CustomViewHolder holder, final int i) {
        final DraftVideoModel item = dataList.get(i);

        holder.viewTxt.setText(item.video_time);

        if (item.video_path != null && !item.video_path.equals("")) {

            //video_path
            Uri uri = Uri.fromFile(new File(item.video_path));
            holder.thumbImage.setController(Functions.frescoImageLoad(uri.toString(), holder.thumbImage, false));

        }

        holder.bind(i, item, listener);

    }


    public interface OnItemClickListener {
        void onItemClick(int postion, DraftVideoModel item, View view);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView thumbImage;
        TextView viewTxt;
        ImageButton crossBtn;

        public CustomViewHolder(View view) {
            super(view);

            thumbImage = view.findViewById(R.id.thumb_image);
            viewTxt = view.findViewById(R.id.view_txt);
            crossBtn = view.findViewById(R.id.cross_btn);

        }

        public void bind(final int position, final DraftVideoModel item, final DraftVideosAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                listener.onItemClick(position, item, v);

            });

            crossBtn.setOnClickListener(v -> {
                listener.onItemClick(position, item, v);

            });

        }

    }

}