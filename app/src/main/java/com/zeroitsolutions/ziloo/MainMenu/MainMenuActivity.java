package com.zeroitsolutions.ziloo.MainMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.ProfileA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.UserModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.DataParsing;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.zeroitsolutions.ziloo.activities.WatchVideosA;

import org.json.JSONObject;

import java.util.Map;


public class MainMenuActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MainMenuActivity mainMenuActivity;
    public static Intent intent;
    long mBackPressed;
    Context context;
    ActivityResultLauncher<Intent> resultChatCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    if (data.getBooleanExtra("isShow", false)) {

                    }
                }
            });
    private MainMenuFragment mainMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception ignored) {
        }
        Functions.setLocale(Functions.getSharedPreference(MainMenuActivity.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, MainMenuActivity.class, false);
        setContentView(R.layout.activity_main_menu);
        context = MainMenuActivity.this;
        mainMenuActivity = this;

        intent = getIntent();
        chechDeepLink(intent);

        if (Functions.getSharedPreference(this).getBoolean(Variable.IS_LOGIN, false)) {
            getPublicIP();
        }

        if (!Functions.getSharedPreference(this).getBoolean(Variable.IsExtended, false))
            checkLicence();

        if (savedInstanceState == null) {
            initScreen();
        } else {
            Functions.printLog(Constants.tag, "savedInstanceState : null " + getSupportFragmentManager().getFragments().get(0));
            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
        }
        Functions.makeDirectry(Functions.getAppFolder(this) + Variable.APP_HIDED_FOLDER);
        Functions.makeDirectry(Functions.getAppFolder(this) + Variable.DRAFT_APP_FOLDER);
        setIntent(null);
    }

    private void chechDeepLink(Intent intent) {
        try {
            Uri uri = intent.getData();
            String linkUri = "" + uri;
            String userId = "";
            String videoId = "";
            String profileURL = Variable.http + "://" + getString(R.string.share_profile_domain_second) + getString(R.string.share_profile_endpoint_second);
            if (linkUri.contains(profileURL)) {
                String[] parts = linkUri.split(profileURL);
                userId = parts[1];

                OpenProfileScreen(userId);
            } else if (linkUri.contains(Constants.BASE_MEDIA_URL)) {
                String[] parts = linkUri.split(Constants.BASE_MEDIA_URL);
                videoId = parts[1].substring(4, (parts[1].length() - 3));
                openWatchVideo(videoId);
            }
        } catch (Exception e) {
            Log.d(Constants.tag, "Exception Link : " + e);
        }
    }

    private void openWatchVideo(String videoId) {
        Intent intent = new Intent(MainMenuActivity.this, WatchVideosA.class);
        intent.putExtra("video_id", videoId);
        intent.putExtra("position", 0);
        intent.putExtra("pageCount", 0);
        intent.putExtra("userId", Functions.getSharedPreference(MainMenuActivity.this).getString(Variable.U_ID, ""));
        intent.putExtra("whereFrom", "IdVideo");
        startActivity(intent);
    }

    private void OpenProfileScreen(String userId) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(MainMenuActivity.this, ApiLinks.showUserDetail, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(MainMenuActivity.this, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        assert msg != null;
                        UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        moveToProfile(userDetailModel.getId()
                                , userDetailModel.getUsername()
                                , userDetailModel.getProfilePic());
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

    private void moveToProfile(String id, String username, String pic) {
        Intent intent = new Intent(MainMenuActivity.this, ProfileA.class);
        intent.putExtra("user_id", id);
        intent.putExtra("user_name", username);
        intent.putExtra("user_pic", pic);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        chechDeepLink(intent);
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null && type.equalsIgnoreCase("message")) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent1 = new Intent(MainMenuActivity.this, ChatA.class);
                    intent1.putExtra("user_id", intent1.getStringExtra("user_id"));
                    intent1.putExtra("user_name", intent1.getStringExtra("user_name"));
                    intent1.putExtra("user_pic", intent1.getStringExtra("user_pic"));
                    resultChatCallback.launch(intent1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }, 2000);
            }
        }
    }

    private void initScreen() {
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(v -> {
        });
    }

    public void getPublicIP() {
        ApiVolleyRequest.JsonGetRequest(this, "https://api.ipify.org/?format=json", new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responce = new JSONObject(response);
                    String ip = responce.optString("ip");
                    Functions.getSharedPreference(MainMenuActivity.this).edit().putString(Variable.DEVICE_IP, ip).commit();

                    if (Functions.getSharedPreference(MainMenuActivity.this).getString(Variable.DEVICE_TOKEN, "").equalsIgnoreCase("")) {
                        addFirebaseToken();
                    } else {
                        Functions.addDeviceData(MainMenuActivity.this);
                    }
                } catch (Exception e) {
                    Log.d(Constants.tag, "Exception : " + e);
                }
            }

            @Override
            public void onError(String response) {

            }
        });
    }


    public void addFirebaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Functions.getSharedPreference(MainMenuActivity.this).edit().putString(Variable.DEVICE_TOKEN, token).commit();
                    Functions.addDeviceData(MainMenuActivity.this);
                });
    }

    public void checkLicence() {

        ApiVolleyRequest.JsonPostRequest(MainMenuActivity.this, ApiLinks.showLicense, null, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(MainMenuActivity.this, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        Functions.getSharedPreference(MainMenuActivity.this).edit().putBoolean(Variable.IsExtended, true).commit();
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
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    Functions.showToast(getBaseContext(), getString(R.string.tap_to_exist));
                    mBackPressed = System.currentTimeMillis();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
    protected void onDestroy() {
        super.onDestroy();
    }
}