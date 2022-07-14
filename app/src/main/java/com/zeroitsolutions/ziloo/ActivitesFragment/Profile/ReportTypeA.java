package com.zeroitsolutions.ziloo.ActivitesFragment.Profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zeroitsolutions.ziloo.Adapters.ReportTypeAdapter;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.volley.plus.interfaces.Callback;
import com.zeroitsolutions.ziloo.Models.ReportTypeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportTypeA extends AppCompatActivity implements View.OnClickListener {


    RecyclerView recyclerview;
    ReportTypeAdapter adapter;
    Boolean isFromRegister;
    String videoId, userId;
    ArrayList<ReportTypeModel> dataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(ReportTypeA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, ReportTypeA.class,false);
        setContentView(R.layout.activity_report_type);
        findViewById(R.id.back_btn).setOnClickListener(this);
        recyclerview = findViewById(R.id.recylerview);

        isFromRegister=getIntent().getBooleanExtra("isFrom",false);
        videoId = getIntent().getStringExtra("video_id");
        userId = getIntent().getStringExtra("user_id");

        callApiForGetReportType();
    }


    // get the types of reports
    private void callApiForGetReportType() {

        Functions.showLoader(ReportTypeA.this, false, false);
        JSONObject parameters = new JSONObject();
        ApiVolleyRequest.JsonPostRequest(ReportTypeA.this, ApiLinks.showReportReasons, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(ReportTypeA.this,resp);
                Functions.printLog(Constants.tag, "resp at report : " + resp);
                Functions.cancelLoader();
                parseData(resp);
            }

            @Override
            public void onError(String response) {
                Functions.cancelLoader();
            }
        });

    }

    private void parseData(String resp) {
        dataList.clear();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    JSONObject reportreason = itemdata.optJSONObject("ReportReason");

                    ReportTypeModel item = new ReportTypeModel();
                    item.id = reportreason.optString("id");
                    item.title = reportreason.optString("title");
                    dataList.add(item);
                }
                setAdapter();
                Functions.cancelLoader();

            } else {
                Functions.cancelLoader();
                Functions.showToast(ReportTypeA.this, jsonObject.optString("msg"));
            }

        } catch (Exception e) {
            Functions.cancelLoader();
            e.printStackTrace();
        }

    }


    private void setAdapter() {
        adapter = new ReportTypeAdapter(ReportTypeA.this, dataList, new ReportTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int positon, ReportTypeModel item, View view) {
                switch (view.getId()) {
                    case R.id.rlt_report:
                        if (isFromRegister) {
                            Functions.printLog(Constants.tag, item.title);
                            sendDataBack(item.title);
                        } else {
                            Intent intent=new Intent(ReportTypeA.this,SubmitReportA.class);
                            intent.putExtra("report_id", item.id);
                            intent.putExtra("report_type", item.title);
                            intent.putExtra("video_id", videoId);
                            intent.putExtra("user_id", userId);
                            resultCallback.launch(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }

                        break;

                }
            }
        });

        adapter.setHasStableIds(true);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(ReportTypeA.this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            ReportTypeA.super.onBackPressed();
                        }
                    }
                }
            });

    private void sendDataBack(String reason) {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        intent.putExtra("reason", reason);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_btn:
                ReportTypeA.super.onBackPressed();
                break;
        }

    }

}