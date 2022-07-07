package com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.R;

public class AlertViewHolder extends RecyclerView.ViewHolder {
    public TextView message, dateTxt;
    View view;

    public AlertViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        this.message = view.findViewById(R.id.message);
        this.dateTxt = view.findViewById(R.id.datetxt);
    }

}
