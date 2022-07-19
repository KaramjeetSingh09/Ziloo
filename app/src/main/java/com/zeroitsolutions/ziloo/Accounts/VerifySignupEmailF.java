package com.zeroitsolutions.ziloo.Accounts;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
import com.zeroitsolutions.ziloo.ApiClasses.ApiClient;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceFileUpload;
import com.zeroitsolutions.ziloo.Models.UserRegisterModel;
import com.zeroitsolutions.ziloo.Models.response.ExpireVerifyEmailModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifySignupEmailF extends Fragment implements View.OnClickListener {

    View view;
    TextView tv1, resendCode, edtEmail;
    ImageView back;
    RelativeLayout rl1;
    SharedPreferences sharedPreferences;
    UserRegisterModel userRegisterModel;
    Button sendOtpBtn;
    PinView etCode;

    public VerifySignupEmailF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_signup_email, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        userRegisterModel = (UserRegisterModel) bundle.getSerializable("user_model");
        sharedPreferences = Functions.getSharedPreference(getContext());
        initViews();
        addClickListner();
        callApiCodeVerification(false);
        etCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = etCode.getText().toString().trim();
                if (txtName.length() == 4) {
                    sendOtpBtn.setEnabled(true);
                    sendOtpBtn.setClickable(true);
                } else {
                    sendOtpBtn.setEnabled(false);
                    sendOtpBtn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void initViews() {
        tv1 = view.findViewById(R.id.tv1_id);
        resendCode = view.findViewById(R.id.resend_code);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtEmail.setText(userRegisterModel.email);

        back = view.findViewById(R.id.goBack);
        rl1 = view.findViewById(R.id.rl1_id);
        sendOtpBtn = view.findViewById(R.id.send_otp_btn);
        etCode = view.findViewById(R.id.et_code);
    }

    private void addClickListner() {
        back.setOnClickListener(this);
        resendCode.setOnClickListener(this);
        edtEmail.setOnClickListener(this);
        sendOtpBtn.setOnClickListener(this);
    }

    // run the one minute countdown timer
    private void oneMinuteTimer() {
        rl1.setVisibility(View.VISIBLE);

        new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tv1.setText((view.getContext().getString(R.string.resend_code) + f.format(min) + ":" + f.format(sec)));
            }

            @Override
            public void onFinish() {
                rl1.setVisibility(View.GONE);
                resendCode.setVisibility(View.VISIBLE);
                expireVerifyEmailCode();
            }
        }.start();
    }

    private void callApiCodeVerification(boolean isVerify) {
        Functions.showLoader(getActivity(), false, false);
        JSONObject parameters = new JSONObject();
        try {
            if (isVerify) {
                parameters.put("email", userRegisterModel.email);
                parameters.put("code", etCode.getText().toString());
            } else {
                parameters.put("email", userRegisterModel.email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(requireActivity(), ApiLinks.verifyRegisterEmailCode, parameters, Functions.getHeaders(requireActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(VerifySignupEmailF.this.getActivity(), response);
                Functions.cancelLoader();
                parseOptData(response, isVerify);
            }

            @Override
            public void onError(String response) {
                Functions.cancelLoader();
            }
        });
    }

    public void parseOptData(String loginData, boolean isVerify) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                if (isVerify) {
                    openCreatePasswordF();
                } else {
//                    oneMinuteTimer();
                }
            } else {
                Toast.makeText(getContext(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCreatePasswordF() {
        CreatePasswordF create_password_f = new CreatePasswordF("fromEmail");
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_model", userRegisterModel);
        create_password_f.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.replace(R.id.email_verify_container, create_password_f).commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resend_code:
                resendCode.setVisibility(View.GONE);
                etCode.setText("");
                callApiCodeVerification(false);
                break;

            case R.id.edit_num_id:
            case R.id.goBack:
                requireActivity().onBackPressed();
                break;

            case R.id.send_otp_btn:
                callApiCodeVerification(true);
                break;
        }
    }

    private void expireVerifyEmailCode() {
        InterfaceFileUpload interfaceFileUpload = ApiClient.getRetrofitInstance(requireActivity())
                .create(InterfaceFileUpload.class);

        interfaceFileUpload.expireVerifyEmailCode(userRegisterModel.email).enqueue(new Callback<ExpireVerifyEmailModel>() {
            @Override
            public void onResponse(@NonNull Call<ExpireVerifyEmailModel> call, @NonNull Response<ExpireVerifyEmailModel> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ExpireVerifyEmailModel> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}