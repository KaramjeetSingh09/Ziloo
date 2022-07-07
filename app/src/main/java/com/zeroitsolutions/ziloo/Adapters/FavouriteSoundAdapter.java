package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.SoundsModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;


public class FavouriteSoundAdapter extends RecyclerView.Adapter<FavouriteSoundAdapter.CustomViewHolder> {
    public Context context;
    public FavouriteSoundAdapter.OnItemClickListener listener;
    ArrayList<SoundsModel> datalist;

    public FavouriteSoundAdapter(Context context, ArrayList<SoundsModel> arrayList, FavouriteSoundAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public FavouriteSoundAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        FavouriteSoundAdapter.CustomViewHolder viewHolder = new FavouriteSoundAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @Override
    public void onBindViewHolder(final FavouriteSoundAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        SoundsModel item = datalist.get(i);
        try {

            holder.soundName.setText(item.sound_name);
            holder.descriptionTxt.setText(item.description);
            holder.durationTimeTxt.setText(item.duration);

            if (item.thum != null && !item.thum.equals("")) {
                Functions.printLog(Constants.tag, item.thum);

                holder.soundImage.setController(Functions.frescoImageLoad(item.thum, holder.soundImage, false));

            }

            holder.favBtn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            holder.bind(i, datalist.get(i), listener);


        } catch (Exception e) {

        }

    }


    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done, favBtn;
        TextView soundName, descriptionTxt, durationTimeTxt;
        SimpleDraweeView soundImage;

        public CustomViewHolder(View view) {
            super(view);
            done = view.findViewById(R.id.done);
            favBtn = view.findViewById(R.id.fav_btn);


            soundName = view.findViewById(R.id.sound_name);
            descriptionTxt = view.findViewById(R.id.description_txt);
            soundImage = view.findViewById(R.id.sound_image);

            durationTimeTxt = view.findViewById(R.id.duration_time_txt);

        }

        public void bind(final int pos, final SoundsModel item, final FavouriteSoundAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            done.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            favBtn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });
        }
    }
}