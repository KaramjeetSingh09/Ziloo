package com.zeroitsolutions.ziloo.Accounts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zeroitsolutions.ziloo.Adapters.SwitchAccountAdapter;
import com.zeroitsolutions.ziloo.Interfaces.FragmentCallBack;
import com.zeroitsolutions.ziloo.Models.MultipleAccountModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.ArrayList;

import io.paperdb.Paper;


public class ManageAccountsF extends BottomSheetDialogFragment implements View.OnClickListener {

    private final ArrayList<MultipleAccountModel> list = new ArrayList<>();
    FragmentCallBack callback;
    ImageView ivClose;
    LinearLayout tabAddAccount;
    private View view;
    private SwitchAccountAdapter adapter;

    public ManageAccountsF() {
    }

    public ManageAccountsF(FragmentCallBack callback) {
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_accounts, container, false);
        return init();
    }

    private View init() {
        ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);

        tabAddAccount = view.findViewById(R.id.tabAddAccount);
        tabAddAccount.setOnClickListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SwitchAccountAdapter(list, (view, pos, object) -> {
            MultipleAccountModel item = (MultipleAccountModel) object;
            if (item.isCheck()) {
                Log.d("tag", ""+item.isCheck());
                // nothing to do because we are already login
            } else {
                Functions.setUpNewSelectedAccount(view.getContext(), item);
            }
        });
        recyclerView.setAdapter(adapter);

        Functions.setUpMultipleAccount(view.getContext());

        new Handler(Looper.getMainLooper()).postDelayed(this::getAccountList, 300);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAccountList() {
        list.clear();
        {
            for (String key : Paper.book(Variable.MultiAccountKey).getAllKeys()) {
                MultipleAccountModel item = Paper.book(Variable.MultiAccountKey).read(key);
                item.setCheck(item.getId().equalsIgnoreCase(Functions.getSharedPreference(view.getContext()).getString(Variable.U_ID, "")));
                list.add(item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivClose) {
            dismiss();
        } else if (id == R.id.tabAddAccount) {
            openAddNewAccount();
        }
    }

    private void openAddNewAccount() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isShow", true);
        callback.onResponce(bundle);
        dismiss();
    }
}