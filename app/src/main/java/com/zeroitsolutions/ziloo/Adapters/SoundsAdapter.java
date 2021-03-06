package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.SoundCatagoryModel;
import com.zeroitsolutions.ziloo.Models.SoundsModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.CustomViewHolder> implements Filterable {

    public Context context;
    ArrayList<SoundCatagoryModel> datalist;
    ArrayList<SoundCatagoryModel> datalist_filter;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    public SoundsAdapter.OnItemClickListener listener;

    public SoundsAdapter(Context context, ArrayList<SoundCatagoryModel> arrayList, SoundsAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        datalist_filter = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SoundsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_sound_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist_filter.size();
    }


    @Override
    public void onBindViewHolder(final SoundsAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);


        SoundCatagoryModel item = datalist_filter.get(i);
        holder.title.setText(item.catagory);

        holder.bind(i, new SoundsModel(), listener);

        SoundItemsAdapter adapter = new SoundItemsAdapter(context, item.sound_list, (view, postion, item1) -> {
            listener.onItemClick(view, postion, item1);
        });

        GridLayoutManager gridLayoutManager;
        if (item.sound_list.size() == 1)
            gridLayoutManager = new GridLayoutManager(context, 1);

        else if (item.sound_list.size() == 2)
            gridLayoutManager = new GridLayoutManager(context, 2);

        else
            gridLayoutManager = new GridLayoutManager(context, 3);

        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.findSnapView(gridLayoutManager);
        snapHelper.attachToRecyclerView(holder.recyclerView);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title, see_all_btn;
        RecyclerView recyclerView;

        public CustomViewHolder(View view) {
            super(view);
            see_all_btn = view.findViewById(R.id.see_all_btn);
            title = view.findViewById(R.id.title);
            recyclerView = view.findViewById(R.id.horizontal_recylerview);
        }

        public void bind(final int pos, final SoundsModel item, final SoundsAdapter.OnItemClickListener listener) {
            see_all_btn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    datalist_filter = datalist;
                } else {
                    ArrayList<SoundCatagoryModel> filteredList = new ArrayList<>();
                    for (SoundCatagoryModel row : datalist) {

                        ArrayList<SoundsModel> soundList = new ArrayList<>();
                        for (SoundsModel sounds_model : row.sound_list) {
                            if (sounds_model.sound_name.toLowerCase().contains(charString.toLowerCase())) {
                                soundList.add(sounds_model);
                            }
                        }

                        if (!soundList.isEmpty()) {
                            row.sound_list = soundList;
                            filteredList.add(row);
                        }
                    }
                    datalist_filter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = datalist_filter;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                datalist_filter = (ArrayList<SoundCatagoryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

class SoundItemsAdapter extends RecyclerView.Adapter<SoundItemsAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<SoundsModel> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    public SoundItemsAdapter.OnItemClickListener listener;


    public SoundItemsAdapter(Context context, ArrayList<SoundsModel> arrayList, SoundItemsAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SoundItemsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final SoundItemsAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        SoundsModel item = datalist.get(i);
        try {

            holder.bind(i, datalist.get(i), listener);

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);
            holder.duration_time_txt.setText(item.duration);

            if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_un_favourite));


            if (item.thum.equals("")) {
                item.thum = "Null";
            }

            if (item.thum != null && !item.thum.equals("")) {
                Functions.printLog(Constants.tag, item.thum);
                holder.sound_image.setController(Functions.frescoImageLoad(item.thum,holder.sound_image,false));
            }

        } catch (Exception e) {
            Functions.printLog(Constants.tag,"Exception : "+e);
        }
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done, fav_btn;
        TextView sound_name, description_txt, duration_time_txt;
        SimpleDraweeView sound_image;

        public CustomViewHolder(View view) {
            super(view);

            done = view.findViewById(R.id.done);
            fav_btn = view.findViewById(R.id.fav_btn);


            sound_name = view.findViewById(R.id.sound_name);
            description_txt = view.findViewById(R.id.description_txt);
            sound_image = view.findViewById(R.id.sound_image);

            duration_time_txt = view.findViewById(R.id.duration_time_txt);

        }

        public void bind(final int pos, final SoundsModel item, final SoundItemsAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            done.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            fav_btn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

        }


    }


}

