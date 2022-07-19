package com.zeroitsolutions.ziloo.Accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.MainMenu.MainMenuActivity;
import com.zeroitsolutions.ziloo.Models.UserModel;
import com.zeroitsolutions.ziloo.Models.UserRegisterModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.DataParsing;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.zeroitsolutions.ziloo.activities.SplashA;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paperdb.Paper;

// This fragment will get the username from the users
public class CreateUsernameF extends Fragment {
    View view;
    EditText usernameEdit;
    Button signUpBtn;
    UserRegisterModel userRegisterModel;
    SharedPreferences sharedPreferences;
    String fromWhere;
    TextView usernameCountTxt;

    public CreateUsernameF(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_name, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        userRegisterModel = (UserRegisterModel) bundle.getSerializable("user_model");

        view.findViewById(R.id.goBack).setOnClickListener(v -> requireActivity().onBackPressed());

        sharedPreferences = Functions.getSharedPreference(view.getContext());
        usernameEdit = view.findViewById(R.id.username_edit);
        signUpBtn = view.findViewById(R.id.btn_sign_up);


        usernameCountTxt = view.findViewById(R.id.username_count_txt);

        InputFilter[] username_filters = new InputFilter[1];
        username_filters[0] = new InputFilter.LengthFilter(Constants.USERNAME_CHAR_LIMIT);
        usernameEdit.setFilters(username_filters);
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                usernameCountTxt.setText(usernameEdit.getText().length() + "/" + Constants.USERNAME_CHAR_LIMIT);
                String txtName = usernameEdit.getText().toString();
                if (txtName.length() > 0) {
                    signUpBtn.setEnabled(true);
                    signUpBtn.setClickable(true);
                } else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signUpBtn.setOnClickListener(v -> {
            if (checkValidation()) {
                call_api_for_sigup();
            }
        });
        return view;
    }

    private void call_api_for_sigup() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("dob", "" + userRegisterModel.dateOfBirth);
            parameters.put("username", "" + usernameEdit.getText().toString());
            parameters.put("device", "android");
            parameters.put("gender", userRegisterModel.gender);

            switch (fromWhere) {
                case "fromEmail":
                    parameters.put("email", "" + userRegisterModel.email);
                    parameters.put("password", userRegisterModel.password);
                    break;
                case "fromPhone":
                    parameters.put("phone", "" + userRegisterModel.phoneNo);
                    break;
                case "social":
                    parameters.put("email", "" + userRegisterModel.email);
                    parameters.put("social_id", "" + userRegisterModel.socailId);
                    parameters.put("profile_pic", "" + userRegisterModel.picture);
                    parameters.put("social", "" + userRegisterModel.socailType);
                    parameters.put("first_name", "" + userRegisterModel.fname);
                    parameters.put("last_name", "" + userRegisterModel.lname);
                    parameters.put("auth_token", "" + userRegisterModel.authTokon);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.registerUser, parameters, Functions.getHeadersWithOutLogin(getActivity()), new InterfaceApiResponse() {
                    @Override
                    public void onResponse(String response) {
                        Functions.checkStatus(getActivity(), response);
                        Functions.cancelLoader();
                        parseSignupData(response);
                    }

                    @Override
                    public void onError(String response) {
                        Log.e("ok", response);
                        Functions.cancelLoader();
                    }
                }
        );
    }

    public void parseSignupData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject jsonObj = jsonObject.getJSONObject("msg");
                UserModel userDetailModel = DataParsing.getUserDataModel(jsonObj.optJSONObject("User"));
                if (fromWhere.equals("social")) {
                    userDetailModel.setAuthToken(userRegisterModel.authTokon);
                }
                Functions.storeUserLoginDataIntoDb(view.getContext(), userDetailModel);


                Functions.setUpMultipleAccount(view.getContext());

                requireActivity().sendBroadcast(new Intent("newVideo"));


                Variable.reloadMyVideos = true;
                Variable.reloadMyVideosInner = true;
                Variable.reloadMyLikesInner = true;
                Variable.reloadMyNotification = true;

                if (Paper.book(Variable.MultiAccountKey).getAllKeys().size() > 1) {
                    Intent intent = new Intent(view.getContext(), SplashA.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                }
            } else {
                Toast.makeText(getActivity(), "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // check the username validation here
    public boolean checkValidation() {

        String uname = usernameEdit.getText().toString();
        if (TextUtils.isEmpty(uname)) {
            usernameEdit.setError(view.getContext().getString(R.string.username_cant_empty));
            usernameEdit.setFocusable(true);
            return false;
        }
        if (uname.length() < 4 || uname.length() > 14) {
            usernameEdit.setError(view.getContext().getString(R.string.username_length_between_valid));
            usernameEdit.setFocusable(true);
            return false;
        }
        if (!(UserNameTwoCaseValidate(uname))) {
            usernameEdit.setError(view.getContext().getString(R.string.username_must_contain_alphabet));
            usernameEdit.setFocusable(true);
            return false;
        }
        return true;
    }

    private boolean UserNameTwoCaseValidate(String name) {

        Pattern let_p = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
        Matcher let_m = let_p.matcher(name);

        return let_m.find();
    }
}