package com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONObject;

public class UpdateEmailPhoneA extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitle;
    RelativeLayout tabPhoneNo, tabEmail;
    EditText edtEmail, edtPhoneNo;
    CountryCodePicker ccp;
    TextView tvCountryCode;
    Button btnEmailNext;
    String countryDialingCode = "", phoneNo;
    // start trimming activity
    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data.getBooleanExtra("isShow", false)) {
                        moveBack();
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, UpdateEmailPhoneA.class, false);
        setContentView(R.layout.activity_update_email_phone);

        initControl();
    }

    private void initControl() {
        tvTitle = findViewById(R.id.tvTitle);
        tabPhoneNo = findViewById(R.id.tabPhoneNo);
        tabEmail = findViewById(R.id.tabEmail);
        edtEmail = findViewById(R.id.email_edit);
        edtPhoneNo = findViewById(R.id.phone_edit);
        tvCountryCode = findViewById(R.id.country_code);
        tvCountryCode.setOnClickListener(this);
        findViewById(R.id.goBack).setOnClickListener(this);
        ccp = new CountryCodePicker(UpdateEmailPhoneA.this);
        ccp.registerPhoneNumberTextView(edtPhoneNo);
        tvCountryCode.setText(ccp.getDefaultCountryNameCode() + " " + ccp.getDefaultCountryCodeWithPlus());
        btnEmailNext = findViewById(R.id.btnSendCodeEmail);
        btnEmailNext.setOnClickListener(this);
        findViewById(R.id.btnSendCodePhone).setOnClickListener(this);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                // check the email validation during user typing
                String txtName = edtEmail.getText().toString();
                if (txtName.length() > 0) {
                    btnEmailNext.setEnabled(true);
                    btnEmailNext.setClickable(true);
                } else {
                    btnEmailNext.setEnabled(false);
                    btnEmailNext.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setUpScreenData();
    }

    private void setUpScreenData() {
        if (getIntent().getStringExtra("type").equalsIgnoreCase("email")) {
            tvTitle.setText(getString(R.string.update_email));
            tabPhoneNo.setVisibility(View.GONE);
            tabEmail.setVisibility(View.VISIBLE);
        } else if (getIntent().getStringExtra("type").equalsIgnoreCase("phone")) {
            tvTitle.setText(getString(R.string.update_phone));
            tabPhoneNo.setVisibility(View.VISIBLE);
            tabEmail.setVisibility(View.GONE);
        }
    }

    // this will open the county picker screen
    @SuppressLint("WrongConstant")
    public void opencountry() {
        final CountryPicker picker = CountryPicker.newInstance(getString(R.string.select_country_));
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {
            countryDialingCode = dialCode;
            ccp.setCountryForNameCode(code);
            tvCountryCode.setText(code + " " + dialCode);
            picker.dismiss();

        });
        picker.show(getSupportFragmentManager(), getString(R.string.select_country_));
    }

    public boolean checkPhoneValidation() {

        final String st_phone = edtPhoneNo.getText().toString();

        if (TextUtils.isEmpty(st_phone)) {
            edtPhoneNo.setError(getString(R.string.enter_valid_phone_no));
            edtPhoneNo.setFocusable(true);
            return false;
        }


        if (!ccp.isValid()) {
            edtPhoneNo.setError(getString(R.string.enter_valid_phone_no));
            edtPhoneNo.setFocusable(true);
            return false;
        }

        phoneNo = edtPhoneNo.getText().toString();
        if (phoneNo.charAt(0) == '0') {
            phoneNo = phoneNo.substring(1);
        }
        phoneNo = phoneNo.replace("+", "");
        phoneNo = phoneNo.replace(ccp.getSelectedCountryCode(), "");
        phoneNo = ccp.getSelectedCountryCodeWithPlus() + phoneNo;
        phoneNo = phoneNo.replace(" ", "");
        phoneNo = phoneNo.replace("(", "");
        phoneNo = phoneNo.replace(")", "");
        phoneNo = phoneNo.replace("-", "");

        return true;
    }

    private void callApiOtpForEmail() {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("user_id", Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variable.U_ID, ""));
            parameters.put("email", edtEmail.getText().toString());
            parameters.put("verify", "0");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(UpdateEmailPhoneA.this, false, false);
        ApiVolleyRequest.JsonPostRequest(UpdateEmailPhoneA.this, ApiLinks.changeEmailAddress, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(UpdateEmailPhoneA.this, resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        moveToVerificationScreen(edtEmail.getText().toString());
                    } else {
                        Functions.showToast(UpdateEmailPhoneA.this, jsonObject.optString("msg"));
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

    private void callApiOtpForPhone() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variable.U_ID, ""));
            parameters.put("phone", phoneNo);
            parameters.put("verify", "0");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(UpdateEmailPhoneA.this, false, false);
        ApiVolleyRequest.JsonPostRequest(UpdateEmailPhoneA.this, ApiLinks.changePhoneNo, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(UpdateEmailPhoneA.this, resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        moveToVerificationScreen(phoneNo);
                    } else {
                        Functions.showToast(UpdateEmailPhoneA.this, jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {
                Functions.cancelLoader();
            }
        });
    }

    // this will check the validations like none of the field can be the empty
    public boolean checkEmailValidation() {

        final String st_email = edtEmail.getText().toString();

        if (TextUtils.isEmpty(st_email)) {
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.setFocusable(true);
            return false;
        } else if (!Functions.isValidEmail(st_email)) {
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.setFocusable(true);
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.country_code:
                opencountry();
                break;
            case R.id.btnSendCodePhone:
                if (checkPhoneValidation()) {

                    Log.d(Constants.tag, "Phone : " + phoneNo);
                    callApiOtpForPhone();
                }
                break;
            case R.id.btnSendCodeEmail: {
                if (checkEmailValidation()) {
                    Log.d(Constants.tag, "Email : " + edtEmail.getText().toString());
                    callApiOtpForEmail();
                }
            }
            break;
            case R.id.goBack: {
                UpdateEmailPhoneA.super.onBackPressed();
            }
            break;
        }
    }

    private void moveToVerificationScreen(String data) {
        Intent intent = new Intent(UpdateEmailPhoneA.this, UpdateEmailPhoneNoVerification.class);
        intent.putExtra("type", getIntent().getStringExtra("type"));
        intent.putExtra("data", data);
        resultCallback.launch(intent);

    }

    private void moveBack() {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}