package com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ViewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatAdapter;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatModel;
import com.zeroitsolutions.ziloo.R;

public class ChatShareProFileViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFullName, tvUsername, datetxt, messageSeen;
    public RelativeLayout tabShareProfile;
    public SimpleDraweeView userProfile;
    public View view;

    public ChatShareProFileViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        userProfile = view.findViewById(R.id.userProfile);
        this.tvFullName = view.findViewById(R.id.tvFullName);
        this.tvUsername = view.findViewById(R.id.tvUserName);
        this.datetxt = view.findViewById(R.id.datetxt);
        messageSeen = view.findViewById(R.id.message_seen);
        tabShareProfile = view.findViewById(R.id.tabShareProfile);
    }

    public void bind(final ChatModel item, final ChatAdapter.OnItemClickListener listener, int position) {
        tabShareProfile.setOnClickListener(v -> listener.onItemClick(item, v, position));
    }
}
