package com.zeroitsolutions.ziloo.ActivitesFragment.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.zeroitsolutions.ziloo.Accounts.LoginA;
import com.zeroitsolutions.ziloo.Accounts.ManageAccountsF;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.BlockUserListA;
import com.zeroitsolutions.ziloo.ActivitesFragment.WalletAndWithdraw.MyWallet;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.AppLanguageChangeA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.AppSpaceClearA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.ManageProfileA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.PrivacyPolicySettingA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.ProfileVarificationA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.PushNotificationSettingA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.QrCodeProfileA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.WalletPaymentA;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.activities.WebviewA;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.zeroitsolutions.ziloo.Constants;
import com.volley.plus.interfaces.Callback;
import com.zeroitsolutions.ziloo.Interfaces.FragmentCallBack;
import com.zeroitsolutions.ziloo.MainMenu.MainMenuActivity;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONObject;

import io.paperdb.Paper;

public class SettingAndPrivacyA extends AppCompatActivity implements View.OnClickListener{

    TextView tvLanguage;
    LinearLayout tabVerifyProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, SettingAndPrivacyA.class,false);
        setContentView(R.layout.activity_setting_and_privacy);

        InitControl();
    }

    private void InitControl() {
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.tabManageAccount).setOnClickListener(this);
        findViewById(R.id.tabPrivacy).setOnClickListener(this);
        findViewById(R.id.tabBalance).setOnClickListener(this);
        findViewById(R.id.tabQr).setOnClickListener(this);
        findViewById(R.id.tabShareProfile).setOnClickListener(this);
        findViewById(R.id.tabPushNotificaiton).setOnClickListener(this);
        findViewById(R.id.tabApplanguage).setOnClickListener(this);
        findViewById(R.id.tabFreeSpace).setOnClickListener(this);
        findViewById(R.id.tabTermsOfService).setOnClickListener(this);
        findViewById(R.id.tabPrivacyPolicy).setOnClickListener(this);
        findViewById(R.id.tabSwitchAccount).setOnClickListener(this);
        findViewById(R.id.tabLogout).setOnClickListener(this);
        findViewById(R.id.tabPayoutSetting).setOnClickListener(this);
        findViewById(R.id.tabBlockUser).setOnClickListener(this);
        tabVerifyProfile=findViewById(R.id.tabVerifyProfile);
        tabVerifyProfile.setOnClickListener(this);
        tvLanguage=findViewById(R.id.tvLanguage);

        setUpScreenData();
    }

    private void setUpScreenData() {
        if (Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.IS_VERIFIED,"0").equals("1"))
        {
            tabVerifyProfile.setVisibility(View.GONE);
        }
        else
        {
            tabVerifyProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back_btn:
            {
                SettingAndPrivacyA.super.onBackPressed();
            }
            break;
            case R.id.tabVerifyProfile:
            {
                openRequestVerification();
            }
            break;
            case R.id.tabManageAccount:
            {
                startActivity(new Intent(SettingAndPrivacyA.this, ManageProfileA.class));
            }
            break;
            case R.id.tabPrivacy:
            {
                openPrivacySetting();
            }
            break;
            case R.id.tabPayoutSetting:
            {
                openPayoutSetting();
            }
            break;
            case R.id.tabBlockUser:
            {
                openBlockUserList();
            }
            break;
            case R.id.tabBalance:
            {
                startActivity(new Intent(SettingAndPrivacyA.this, MyWallet.class));
            }
            break;
            case R.id.tabQr:
            {
                startActivity(new Intent(SettingAndPrivacyA.this, QrCodeProfileA.class));
            }
            break;
            case R.id.tabShareProfile:
            {
                shareProfile();
            }
            break;
            case R.id.tabPushNotificaiton:
            {
                openPushNotificationSetting();
            }
            break;
            case R.id.tabApplanguage:
            {
                startActivity(new Intent(SettingAndPrivacyA.this, AppLanguageChangeA.class));
            }
            break;
            case R.id.tabFreeSpace:
            {
                startActivity(new Intent(SettingAndPrivacyA.this, AppSpaceClearA.class));
            }
            break;
            case R.id.tabTermsOfService:
            {
                openWebUrl(getString(R.string.terms_amp_conditions), Constants.terms_conditions);
            }
            break;
            case R.id.tabPrivacyPolicy:
            {
                openWebUrl(getString(R.string.privacy_policy), Constants.privacy_policy);
            }
            break;
            case R.id.tabSwitchAccount:
            {
                openManageMultipleAccounts();
            }
            break;
            case R.id.tabLogout:
            {
                logoutProceed();
            }
            break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        tvLanguage.setText(Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.APP_LANGUAGE, Variable.DEFAULT_LANGUAGE));
    }

    private void openPayoutSetting() {

        Intent intent=new Intent(SettingAndPrivacyA.this, WalletPaymentA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openBlockUserList() {

        Intent intent=new Intent(SettingAndPrivacyA.this, BlockUserListA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void openRequestVerification() {
        Intent intent=new Intent(SettingAndPrivacyA.this, ProfileVarificationA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }


    private void shareProfile() {

        String userId=Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.U_ID,"");
        String userName=Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.U_NAME,"");
        String fullName=Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.F_NAME,"")+" "+Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.L_NAME,"");
        String userPic=Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.U_PIC,"");

        final ShareUserProfileF fragment = new ShareUserProfileF(userId,userName,fullName,userPic,"",false,true, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {

                }
            }
        });
        fragment.show(getSupportFragmentManager(), "");

    }


    private void openPrivacySetting() {
        Intent intent=new Intent(SettingAndPrivacyA.this, PrivacyPolicySettingA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    private void openPushNotificationSetting() {
        Intent intent=new Intent(SettingAndPrivacyA.this, PushNotificationSettingA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    private void logoutProceed() {
        if (Paper.book(Variable.MultiAccountKey).getAllKeys().size()>1)
        {
            Functions.showDoubleButtonAlert(SettingAndPrivacyA.this,
                    getString(R.string.are_you_sure_to_logout),
                    "",
                    getString(R.string.logout),
                    getString(R.string.switch_account),true,
                    new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if (bundle.getBoolean("isShow",false))
                            {
                                openManageMultipleAccounts();
                            }
                            else
                            {
                                logout();
                            }
                        }
                    }
            );
        }
        else
        {
            logout();
        }
    }


    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(SettingAndPrivacyA.this, WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    public void logout() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Functions.getSharedPreference(SettingAndPrivacyA.this).getString(Variable.U_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(SettingAndPrivacyA.this,false,false);
        ApiVolleyRequest.JsonPostRequest(SettingAndPrivacyA.this, ApiLinks.logout, params, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(SettingAndPrivacyA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equalsIgnoreCase("200")) {
                        removePreferenceData();
                    }
                    else
                    {
                        removePreferenceData();
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }

            @Override
            public void onError(String response) {

            }

        });

    }

    private void removePreferenceData() {
        Paper.book(Variable.PrivacySetting).destroy();

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SettingAndPrivacyA.this, gso);
        googleSignInClient.signOut();

        LoginManager.getInstance().logOut();

        Functions.removeMultipleAccount(SettingAndPrivacyA.this);

        SharedPreferences.Editor editor = Functions.getSharedPreference(SettingAndPrivacyA.this).edit();
        editor.clear();
        editor.commit();

        Functions.setUpExistingAccountLogin(SettingAndPrivacyA.this);

        Intent intent=new Intent(SettingAndPrivacyA.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void openManageMultipleAccounts() {
        ManageAccountsF f = new ManageAccountsF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    Functions.hideSoftKeyboard(SettingAndPrivacyA.this);
                    Intent intent = new Intent(SettingAndPrivacyA.this, LoginA.class);
                    startActivity(intent);
                   overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
            }
        });
        f.show(getSupportFragmentManager(), "");
    }
}