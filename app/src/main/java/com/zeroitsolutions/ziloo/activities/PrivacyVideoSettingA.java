package com.zeroitsolutions.ziloo.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.volley.plus.interfaces.Callback;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONObject;

public class PrivacyVideoSettingA extends AppCompatActivity implements View.OnClickListener{

    TextView viewVideo;
    Switch allowCommentSwitch, allowDuetSwitch;
    String videoId, commentValue, duetValue, privacyValue, duetVideoId;
    RelativeLayout allowDuetLayout;
    Boolean callApi = false;
    String viewVideoType="Private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(PrivacyVideoSettingA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, PrivacyVideoSettingA.class,false);
        setContentView(R.layout.activity_privacy_video_setting);
        viewVideo =findViewById(R.id.view_video);
        allowDuetLayout =findViewById(R.id.allow_duet_layout);

        allowCommentSwitch =findViewById(R.id.allow_comment_switch);
        allowCommentSwitch.setOnClickListener(this);

        allowDuetSwitch =findViewById(R.id.allow_duet_switch);
        allowDuetSwitch.setOnClickListener(this);

       findViewById(R.id.view_video_layout).setOnClickListener(this);
       findViewById(R.id.back_btn).setOnClickListener(this);

        videoId = getIntent().getStringExtra("video_id");
        privacyValue = getIntent().getStringExtra("privacy_value");
        duetValue = getIntent().getStringExtra("duet_value");
        commentValue = getIntent().getStringExtra("comment_value");
        duetVideoId = getIntent().getStringExtra("duet_video_id");

        viewVideo.setText(privacyValue);
        viewVideoType=privacyValue;

        allowCommentSwitch.setChecked(commentValue(commentValue));

        allowDuetSwitch.setChecked(getTrueFalseCondition(duetValue));

        if (Functions.getSharedPreference(PrivacyVideoSettingA.this).getBoolean(Variable.IsExtended,false) && (duetVideoId != null && duetVideoId.equalsIgnoreCase("0"))) {
            allowDuetLayout.setVisibility(View.VISIBLE);
        }
    }


    private boolean getTrueFalseCondition(String str) {
        if (str.equalsIgnoreCase("1"))
            return true;
        else
            return false;
    }


    private boolean commentValue(String str) {
        if (str.equalsIgnoreCase("true"))
            return true;
        else
            return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_video_layout) {
            openDialogForPrivacy(PrivacyVideoSettingA.this);
        } else if (id == R.id.back_btn) {
            onBackPressed();
        } else if (id == R.id.allow_duet_switch) {
            if (allowDuetSwitch.isChecked()) {
                duetValue = "1";
            } else {
                duetValue = "0";
            }
            callApi();
        } else if (id == R.id.allow_comment_switch) {
            if (allowCommentSwitch.isChecked()) {
                commentValue = "true";
            } else {
                commentValue = "false";
            }
            callApi();
        }
    }


    // call api for change the privacy setting of profile
    public void callApi() {

        JSONObject params = new JSONObject();
        try {
            params.put("video_id", videoId);
            params.put("allow_comments", commentValue);
            params.put("allow_duet", duetValue);
            params.put("privacy_type", viewVideoType);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Functions.printLog(Constants.tag, "params at video_setting: " + params);

        ApiVolleyRequest.JsonPostRequest(PrivacyVideoSettingA.this, ApiLinks.updateVideoDetail, params, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(PrivacyVideoSettingA.this,response);
                parseDate(response);
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    public void parseDate(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");

            if (code.equals("200")) {
                Functions.showToast(PrivacyVideoSettingA.this,  getString(R.string.setting_updated_successfully));
                callApi = true;
            } else {
                Functions.showToast(PrivacyVideoSettingA.this, jsonObject.optString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // open the dialog for privacy public or private options
    private void openDialogForPrivacy(Context context) {
        final CharSequence[] options = {getString(R.string.public_), getString(R.string.private_)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                viewVideo.setText(options[item]);
                if (item==0)
                {
                    viewVideoType="Public";
                }
                else
                {
                    viewVideoType="Private";
                }
                callApi();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isShow", callApi);
        intent.putExtra("video_id", videoId);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}