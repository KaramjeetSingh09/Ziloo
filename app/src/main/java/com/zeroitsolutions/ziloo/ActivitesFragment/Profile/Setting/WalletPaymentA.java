package com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

public class WalletPaymentA extends AppCompatActivity implements View.OnClickListener {

    ImageView btnBack;
    TextView tvEmail, tvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(WalletPaymentA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, WalletPaymentA.class, false);
        setContentView(R.layout.activity_wallet_payment);
        METHOD_initViews();
    }

    private void METHOD_initViews() {
        tvAdd = findViewById(R.id.tvAdd);
        tvAdd.setOnClickListener(this);
        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setOnClickListener(this);
        SetupScreenData();
    }

    @Override
    public void onClick(View view) {
        if (view == tvAdd) {
            METHOD_openAddCard_F(false);
        } else if (view == btnBack) {
            WalletPaymentA.super.onBackPressed();
        } else if (view == tvEmail) {
            METHOD_openAddCard_F(true);
        }
    }

    private void METHOD_openAddCard_F(boolean isEdit) {
        if (!(Functions.isValidEmail(tvEmail.getText().toString()))) {
            isEdit = false;
        }
        Intent intent = new Intent(WalletPaymentA.this, AddPayoutMethodA.class);
        intent.putExtra("email", tvEmail.getText().toString());
        intent.putExtra("isEdit", isEdit);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void SetupScreenData() {
        tvEmail.setText(Functions.getSharedPreference(WalletPaymentA.this).getString(Variable.U_PAYOUT_ID, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetupScreenData();
    }
}