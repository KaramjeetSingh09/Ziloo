package com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ViewHolders;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatAdapter;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatModel;
import com.zeroitsolutions.ziloo.R;

public class ChatAudioViewHolder extends RecyclerView.ViewHolder {
    public TextView dateTxt, message_seen;
    public ProgressBar pBar;
    public ImageView notSendMessageIcon;
    public ImageView playBtn;
    public SeekBar seekBar;
    public TextView totalTime;
    public LinearLayout audioBubble;

    View view;

    public ChatAudioViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        audioBubble = view.findViewById(R.id.audio_bubble);
        dateTxt = view.findViewById(R.id.datetxt);
        message_seen = view.findViewById(R.id.message_seen);
        notSendMessageIcon = view.findViewById(R.id.not_send_messsage);
        pBar = view.findViewById(R.id.p_bar);
        this.playBtn = view.findViewById(R.id.play_btn);
        this.seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        this.totalTime = (TextView) view.findViewById(R.id.total_time);

    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind(final ChatModel item, int position, final ChatAdapter.OnItemClickListener listener, final ChatAdapter.OnLongClickListener long_listener) {

        audioBubble.setOnClickListener(v -> listener.onItemClick(item, v, position));

        audioBubble.setOnLongClickListener(v -> {
            long_listener.onLongclick(item, v);
            return false;
        });

        seekBar.setOnTouchListener((v, event) -> true);

    }
}