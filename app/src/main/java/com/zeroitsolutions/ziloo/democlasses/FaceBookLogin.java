package com.zeroitsolutions.ziloo.democlasses;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.zeroitsolutions.ziloo.Models.UserRegisterModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.util.Arrays;

public class FaceBookLogin extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    Button loginButton;
    private CallbackManager mCallbackManager;
    UserRegisterModel userRegisterModel = new UserRegisterModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_facebooklogin);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            LoginWithFB();
        }
    }

    public void LoginWithFB() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends", "picture"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                Toast.makeText(FaceBookLogin.this, "facebook:onSuccess", Toast.LENGTH_SHORT).show();
//                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
                Toast.makeText(FaceBookLogin.this, "facebook:onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
                Toast.makeText(FaceBookLogin.this, "facebook:onError", Toast.LENGTH_SHORT).show();
            }
        });


//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Functions.showToast(FaceBookLogin.this, getString(R.string.facebook));
//                handleFacebookAccessToken(loginResult.getAccessToken());
//                Functions.printLog("resp_token", loginResult.getAccessToken() + "");
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Functions.showToast(FaceBookLogin.this, getString(R.string.login_cancel));
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Functions.printLog("resp", "" + error.toString());
//                Functions.showToast(FaceBookLogin.this, getString(R.string.login_error) + error);
//            }
//        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Functions.printLog("resp_token", token.getToken() + "");
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Functions.showLoader(FaceBookLogin.this, false, false);
                            final String id = Profile.getCurrentProfile().getId();
                            GraphRequest request = GraphRequest.newMeRequest(token, (user, graphResponse) -> {

                                Functions.cancelLoader();
                                Functions.printLog("resp", user.toString());
                                //after get the info of user we will pass to function which will store the info in our server

                                String fname = "" + user.optString("first_name");
                                String lname = "" + user.optString("last_name");
                                String email = "" + user.optString("email");
                                String auth_token = token.getToken();
                                String image = "https://graph.facebook.com/" + id + "/picture?width=500";

                                userRegisterModel = new UserRegisterModel();

                                userRegisterModel.fname = Functions.removeSpecialChar(fname);
                                userRegisterModel.email = email;
                                userRegisterModel.lname = Functions.removeSpecialChar(lname);
                                userRegisterModel.socailId = id;
                                userRegisterModel.picture = image;
                                userRegisterModel.socailType = "facebook";
                                userRegisterModel.authTokon = auth_token;


//                                FaceBookLogin.this.callApiForLogin("" + id,
//                                        "facebook",
//                                        auth_token);

                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Functions.cancelLoader();
                            Functions.showToast(FaceBookLogin.this, getString(R.string.authentication_failed));
                        }
                    }
                });
    }

}
