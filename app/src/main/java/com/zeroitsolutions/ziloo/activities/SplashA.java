package com.zeroitsolutions.ziloo.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.MainMenu.MainMenuActivity;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONObject;

import io.paperdb.Paper;

public class SplashA extends AppCompatActivity {

    CountDownTimer countDownTimer;
    ActivityResultLauncher<Intent> connectionCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    if (data.getBooleanExtra("isShow", false)) {
                        apiCallHit();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Functions.setLocale(Functions.getSharedPreference(SplashA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, SplashA.class, false);
        setContentView(R.layout.activity_splash);
        apiCallHit();
    }

    private void apiCallHit() {
        callApiForGetad();
        if (Functions.getSharedPreference(this).getString(Variable.DEVICE_ID, "0").equals("0")) {
            callApiRegisterDevice();
        } else
            setTimer();
    }

    private void callApiForGetad() {
        JSONObject parameters = new JSONObject();

        /**
         *      FOR SHOW VIDEO DETAILS ADD
         */
        ApiVolleyRequest.JsonPostRequest(SplashA.this, ApiLinks.showVideoDetailAd, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(SplashA.this, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        assert msg != null;
                        JSONObject video = msg.optJSONObject("Video");
                        JSONObject user = msg.optJSONObject("User");
                        JSONObject sound = msg.optJSONObject("Sound");
                        assert user != null;
                        JSONObject pushNotification = user.optJSONObject("PushNotification");
                        JSONObject privacySetting = user.optJSONObject("PrivacySetting");
                        HomeModel item = Functions.parseVideoData(user, sound, video, privacySetting, pushNotification);
                        item.promote = "1";
                        Paper.book(Variable.PromoAds).write(Variable.PromoAdsModel, item);
                    } else {
                        Paper.book(Variable.PromoAds).destroy();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {
                Paper.book(Variable.PromoAds).destroy();
            }
        });
    }

    // show the splash for 3 sec
    public void setTimer() {
        countDownTimer = new CountDownTimer(2500, 500) {

            public void onTick(long millisUntilFinished) {
                // this will call on every 500 ms
            }

            public void onFinish() {
                Intent intent = new Intent(SplashA.this, MainMenuActivity.class);

                if (getIntent().getExtras() != null) {
                    try {
                        // its for multiple account notification handling
                        String userId = getIntent().getStringExtra("receiver_id");
                        Functions.setUpSwitchOtherAccount(SplashA.this, userId);
                    } catch (Exception ignored) {

                    }
                    intent.putExtras(getIntent().getExtras());
                    setIntent(null);
                }

                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    // register the device on server on application open
    public void callApiRegisterDevice() {

        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        JSONObject param = new JSONObject();
        try {
            param.put("key", androidId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *  FOR REGISTER DEVICE
         */

        ApiVolleyRequest.JsonPostRequest(this, ApiLinks.registerDevice, param, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(SplashA.this, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        setTimer();
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        assert msg != null;
                        JSONObject Device = msg.optJSONObject("Device");
                        SharedPreferences.Editor editor2 = Functions.getSharedPreference(SplashA.this).edit();
                        assert Device != null;
                        editor2.putString(Variable.DEVICE_ID, Device.optString("id")).apply();
                    } else {
                        setTimer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, (requestType, response) -> {
            if (response.equalsIgnoreCase("disconnected")) {
                connectionCallback.launch(new Intent(getApplicationContext(), NoInternetA.class));
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