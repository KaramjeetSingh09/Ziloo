package com.zeroitsolutions.ziloo.ActivitesFragment.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.ArrayList;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.CustomViewHolder> {
    private final GifAdapter.OnItemClickListener listener;
    public Context context;
    ArrayList<String> gifList;

    public GifAdapter(Context context, ArrayList<String> datalist, GifAdapter.OnItemClickListener listener) {
        this.context = context;
        this.gifList = datalist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GifAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return gifList.size();
    }

    @Override
    public void onBindViewHolder(final GifAdapter.CustomViewHolder holder, final int i) {
        holder.bind(gifList.get(i), listener);


        // show the gif images by fresco
        String url = Variable.GIF_FIRSTPART + gifList.get(i) + Variable.GIF_SECONDPART;
        holder.gifImage.setController(Functions.frescoImageLoad(url, holder.gifImage, true));

        Functions.printLog(Constants.tag, Variable.GIF_FIRSTPART + gifList.get(i) + Variable.GIF_SECONDPART);
    }


    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView gifImage;

        public CustomViewHolder(View view) {
            super(view);
            gifImage = view.findViewById(R.id.gif_image);
        }

        public void bind(final String item, final GifAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}