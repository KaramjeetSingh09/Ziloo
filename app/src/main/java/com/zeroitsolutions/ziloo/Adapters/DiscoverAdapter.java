package com.zeroitsolutions.ziloo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.DiscoverModel;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.ArrayList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.CustomViewHolder> implements Filterable {
    public Context context;
    public DiscoverAdapter.OnItemClickListener listener;
    ArrayList<DiscoverModel> datalist;
    ArrayList<DiscoverModel> datalistFilter;

    public DiscoverAdapter(Context context, ArrayList<DiscoverModel> arrayList, DiscoverAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        datalistFilter = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscoverAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalistFilter.size();
    }

    @Override
    public void onBindViewHolder(final DiscoverAdapter.CustomViewHolder holder, final int i) {

        DiscoverModel item = datalistFilter.get(i);

        holder.title.setText(item.title);
        holder.viewsTxt.setText(Functions.getSuffix(item.videos_count));

        HorizontalAdapter adapter = new HorizontalAdapter(context, i, item.arrayList);
        GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 1);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.horizontal_reycerview.setLayoutManager(layoutManager);
        holder.horizontal_reycerview.setAdapter(adapter);

        holder.bind(i, item.arrayList);
    }

    // that function will filter the result
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    datalistFilter = datalist;
                } else {
                    ArrayList<DiscoverModel> filteredList = new ArrayList<>();
                    for (DiscoverModel row : datalist) {


                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.title.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    datalistFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = datalistFilter;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                datalistFilter = (ArrayList<DiscoverModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ArrayList<HomeModel> video_list, int main_position, int child_position);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        RecyclerView horizontal_reycerview;

        TextView title, viewsTxt;
        RelativeLayout hashtagLayout;

        public CustomViewHolder(View view) {
            super(view);

            horizontal_reycerview = view.findViewById(R.id.horizontal_recylerview);
            title = view.findViewById(R.id.title);
            viewsTxt = view.findViewById(R.id.views_txt);

            hashtagLayout = view.findViewById(R.id.hashtag_layout);
        }

        public void bind(final int pos, final ArrayList<HomeModel> datalist) {
            hashtagLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, datalist, pos, -1);
                }
            });
        }


    }

    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.CustomViewHolder> {
        public Context context;

        ArrayList<HomeModel> datalist;
        int main_position;

        public HorizontalAdapter(Context context, int position, ArrayList<HomeModel> arrayList) {
            this.context = context;
            datalist = arrayList;
            this.main_position = position;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_horizontal_layout, viewGroup, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        @Override
        public void onBindViewHolder(final HorizontalAdapter.CustomViewHolder holder, final int i) {
            holder.setIsRecyclable(false);


            HomeModel item = datalist.get(i);
            holder.bind(i, datalist);

            if (item.thum != null) {


                holder.tab_more_txt.setVisibility(View.GONE);

                try {

                    if (Constants.IS_SHOW_GIF) {

                        holder.video_thumbnail.setController(Functions.frescoImageLoad(item.gif, holder.video_thumbnail, true));

                    } else {
                        if (item.thum != null && !item.thum.equals("")) {
                            holder.video_thumbnail.setController(Functions.frescoImageLoad(item.thum, holder.video_thumbnail, false));
                        }
                    }
                } catch (Exception e) {
                    Functions.printLog(Constants.tag, e.toString());
                }
            } else {
                holder.tab_more_txt.setVisibility(View.VISIBLE);
                holder.video_thumbnail.setVisibility(View.GONE);

            }
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {

            SimpleDraweeView video_thumbnail;
            TextView tab_more_txt;

            public CustomViewHolder(View view) {
                super(view);
                video_thumbnail = view.findViewById(R.id.video_thumbnail);
                tab_more_txt = view.findViewById(R.id.tab_more_txt);

            }

            public void bind(final int pos, final ArrayList<HomeModel> datalist) {
                itemView.setOnClickListener(v -> {
                    if (datalist.get(pos).thum == null)
                        listener.onItemClick(itemView, datalist, main_position, pos);
                    else
                        listener.onItemClick(itemView, datalist, main_position, pos);


                });
            }
        }
    }
}