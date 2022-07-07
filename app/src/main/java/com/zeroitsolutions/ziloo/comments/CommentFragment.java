package com.zeroitsolutions.ziloo.comments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeroitsolutions.ziloo.ActivitesFragment.CommentF;
import com.zeroitsolutions.ziloo.Adapters.CommentsAdapter;
import com.zeroitsolutions.ziloo.Interfaces.FragmentDataSend;
import com.zeroitsolutions.ziloo.MainMenu.RelateToFragmentOnBack.RootFragment;
import com.zeroitsolutions.ziloo.Models.CommentModel;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.SoftKeyboardStateHelper;

import java.util.ArrayList;

public class CommentFragment extends RootFragment {
    private View view;
    Context context;
    RecyclerView recyclerView;
    CommentsAdapter adapter;
    ArrayList<CommentModel> dataList;
    HomeModel item;
    String videoId;
    String userId;
    EditText messageEdit;
    ImageView sendBtn;
    ProgressBar sendProgress, noDataLoader;
    TextView commentCountTxt, noDataLayout;
    FrameLayout commentScreen;
    String commentId, parentCommentId, replyUserName;
    String replyStatus = null;
    int pageCount = 0;
    boolean isPostFinished;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;
    FragmentDataSend fragmentDataSend;
    RelativeLayout write_layout;

    public CommentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        write_layout = view.findViewById(R.id.write_layout);
        commentScreen = view.findViewById(R.id.comment_screen);
        noDataLayout = view.findViewById(R.id.no_data_layout);
        commentScreen.setOnClickListener(v -> CommentFragment.this.requireActivity().onBackPressed());
        view.findViewById(R.id.goBack).setOnClickListener(v -> CommentFragment.this.requireActivity().onBackPressed());
        Bundle bundle = getArguments();
        if (bundle != null) {
            videoId = bundle.getString("video_id");
            userId = bundle.getString("user_id");
            item = (HomeModel) bundle.getSerializable("data");
        }

        SoftKeyboardStateHelper.SoftKeyboardStateListener softKeyboardStateListener = new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {

            }

            @Override
            public void onSoftKeyboardClosed() {
                if ((parentCommentId != null && !parentCommentId.equals("")) || replyStatus != null) {
                    messageEdit.setHint(requireActivity().getString(R.string.leave_a_comment));
                    replyStatus = null;
                    parentCommentId = null;
                }
            }
        };

        final SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(requireActivity(), view.findViewById(R.id.comment_screen));
        softKeyboardStateHelper.addSoftKeyboardStateListener(softKeyboardStateListener);
        commentCountTxt = view.findViewById(R.id.comment_count);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

    }
}
