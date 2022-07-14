package com.zeroitsolutions.ziloo.ActivitesFragment.SendGift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.ArrayList;
import java.util.List;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.CustomViewHolder> {

    public Context context;
    List<StickerModel> gif_list = new ArrayList<>();
    private StickerAdapter.OnItemClickListener listener;

    public StickerAdapter(Context context, List<StickerModel> gif_list, StickerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.gif_list = gif_list;
        this.listener = listener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send_gif_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return gif_list.size();
    }

    @Override
    public void onBindViewHolder(final StickerAdapter.CustomViewHolder holder, final int i) {
        StickerModel model = gif_list.get(i);

        if (model.image != null && !model.image.equals("")) {
            if (!model.image.contains(Variable.http)) {
                model.image = Constants.BASE_MEDIA_URL + model.image;
            }
            Functions.printLog(Constants.tag, model.image);
            holder.gif_image.setController(Functions.frescoImageLoad(model.image, holder.gif_image, false));
        }
        holder.name_txt.setText(model.name);
        holder.coins_txt.setText(model.coins + " " + context.getString(R.string.coins));
        if (model.isSelected) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
        }

        holder.bind(model, listener);
    }


    public interface OnItemClickListener {
        void onItemClick(StickerModel item);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView gif_image;
        TextView name_txt, coins_txt;
        ImageView ivSelect;

        public CustomViewHolder(View view) {
            super(view);
            gif_image = view.findViewById(R.id.gif_image);
            name_txt = view.findViewById(R.id.name_txt);
            coins_txt = view.findViewById(R.id.coin_txt);
            ivSelect = view.findViewById(R.id.ivSelect);
        }

        public void bind(final StickerModel item, final StickerAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> listener.onItemClick(item));

        }

    }

}