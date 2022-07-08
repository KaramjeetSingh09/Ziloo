package com.zeroitsolutions.ziloo.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.Adapters.InboxAdapter;
import com.zeroitsolutions.ziloo.Models.InboxModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.ArrayList;
import java.util.Collections;

public class InboxA extends AppCompatActivity implements InboxAdapter.OnItemClickListener, InboxAdapter.OnLongItemClickListener {

    Context context;
    RecyclerView inboxList;
    ArrayList<InboxModel> inboxArraylist;
    DatabaseReference rootRef;
    InboxAdapter inboxAdapter;
    ProgressBar pbar;
    boolean isviewCreated = false;
    AdView adView;
    ValueEventListener eventListener2;
    Query inboxQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(InboxA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, InboxA.class, false);
        setContentView(R.layout.activity_inbox);
        context = InboxA.this;
        rootRef = FirebaseDatabase.getInstance().getReference();
        pbar = findViewById(R.id.pbar);
        inboxList = findViewById(R.id.inboxlist);
        inboxArraylist = new ArrayList<>();
        inboxList = findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inboxList.setLayoutManager(layout);
        inboxList.setHasFixedSize(false);
        inboxAdapter = new InboxAdapter(context, inboxArraylist, this, this);
        inboxList.setAdapter(inboxAdapter);
        findViewById(R.id.back_btn).setOnClickListener(v -> InboxA.super.onBackPressed());
        isviewCreated = true;
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        adView = findViewById(R.id.bannerad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void getData() {

        pbar.setVisibility(View.VISIBLE);
        inboxQuery = rootRef.child("Inbox").child(Functions.getSharedPreference(InboxA.this).getString(Variable.U_ID, "0")).orderByChild("date");
        eventListener2 = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxArraylist.clear();
                pbar.setVisibility(View.GONE);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    InboxModel model = ds.getValue(InboxModel.class);
                    if (model != null) {
                        model.setId(ds.getKey());
                    }
                    inboxArraylist.add(model);
                }

                if (inboxArraylist.isEmpty()) {
//                    Functions.showToast(context, getString(R.string.no_data));
                    findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                    Collections.reverse(inboxArraylist);
                    inboxAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pbar.setVisibility(View.GONE);
                findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            }
        };
        pbar.setVisibility(View.GONE);
        inboxQuery.addValueEventListener(eventListener2);
    }

    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if (inboxQuery != null)
            inboxQuery.removeEventListener(eventListener2);
    }

    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String receiverid, String name, String picture) {
        Intent intent = new Intent(InboxA.this, ChatA.class);
        intent.putExtra("user_id", receiverid);
        intent.putExtra("user_name", name);
        intent.putExtra("user_pic", picture);
        resultCallback.launch(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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

    @Override
    public void onItemClick(InboxModel item) {
        chatFragment(item.getId(), item.getName(), item.getPic());
    }

    @Override
    public void onLongItemClick(InboxModel item) {

    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getBooleanExtra("isShow", false)) {
                        //
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}