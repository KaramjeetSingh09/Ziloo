package com.zeroitsolutions.ziloo.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.LiveUserAdapter;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.LiveUserModel;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.activities.LiveActivity;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.PermissionUtils;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.zeroitsolutions.ziloo.SimpleClasses.Ziloo;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;

public class LiveUsersA extends AppCompatActivity implements View.OnClickListener {

    Context context;
    ArrayList<LiveUserModel> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    LiveUserAdapter adapter;
    ImageView btnBack;
    DatabaseReference rootref;
    TextView noDataFound;

    PermissionUtils takePermissionUtils;
    LiveUserModel selectLiveModel;
    // get the list of all live user from the firebase
    ChildEventListener valueEventListener;
    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                boolean allPermissionClear = true;
                List<String> blockPermissionCheck = new ArrayList<>();
                for (String key : result.keySet()) {
                    if (!(result.get(key))) {
                        allPermissionClear = false;
                        blockPermissionCheck.add(Functions.getPermissionStatus(LiveUsersA.this, key));
                    }
                }
                if (blockPermissionCheck.contains("blocked")) {
                    Functions.showPermissionSetting(LiveUsersA.this, getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                } else if (allPermissionClear) {
                    goLive();
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(LiveUsersA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, LiveUsersA.class, false);
        setContentView(R.layout.activity_live_users);
        context = LiveUsersA.this;
        rootref = FirebaseDatabase.getInstance().getReference();
        takePermissionUtils = new PermissionUtils(LiveUsersA.this, mPermissionResult);
        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        recyclerView = findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setHasFixedSize(true);
        adapter = new LiveUserAdapter(context, dataList, (view, pos, object) -> {
            selectLiveModel = (LiveUserModel) object;
            if (Functions.checkLoginUser(LiveUsersA.this)) {

                if (takePermissionUtils.isCameraRecordingPermissionGranted()) {
                    goLive();
                } else {
                    takePermissionUtils.showCameraRecordingPermissionDailog(getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                }

            }
        });

        recyclerView.setAdapter(adapter);
        getData();
        noDataFound = findViewById(R.id.no_data_found);
    }

    private void goLive() {
        openZilooLive(selectLiveModel.getUser_id(),
                selectLiveModel.getUser_name(), selectLiveModel.getUser_picture(), Constants.CLIENT_ROLE_AUDIENCE);
    }

    public void getData() {

        valueEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LiveUserModel model = dataSnapshot.getValue(LiveUserModel.class);
                dataList.add(model);
                adapter.notifyDataSetChanged();
                noDataFound.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                LiveUserModel model = dataSnapshot.getValue(LiveUserModel.class);

                for (int i = 0; i < dataList.size(); i++) {
                    if (model.getUser_id().equals(dataList.get(i).getUser_id())) {
                        dataList.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()) {
                    noDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rootref.child("LiveUsers").addChildEventListener(valueEventListener);
    }

    @Override
    public void onDestroy() {
        mPermissionResult.unregister();
        if (rootref != null && valueEventListener != null)
            rootref.child("LiveUsers").removeEventListener(valueEventListener);
        super.onDestroy();

    }

    // watch the streaming of user which will be live
    public void openZilooLive(String userId, String userName, String userImage, int role) {
        final Intent intent = new Intent();
        intent.putExtra("user_id", userId);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_picture", userImage);
        intent.putExtra("user_role", role);
        intent.putExtra(com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(LiveUsersA.this, LiveActivity.class);
        Ziloo ziloo = (Ziloo) getApplication();
        ziloo.engineConfig().setChannelName(userId);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            LiveUsersA.super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, (requestType, response) -> {
            if (response.equalsIgnoreCase("disconnected")) {
                startActivity(new Intent(getApplicationContext(), NoInternetA.class));
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functions.unRegisterConnectivity(getApplicationContext());
    }
}
