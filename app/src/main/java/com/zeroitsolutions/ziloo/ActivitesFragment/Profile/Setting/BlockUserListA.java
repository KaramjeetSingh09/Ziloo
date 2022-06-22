package com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.ProfileA;
import com.zeroitsolutions.ziloo.Adapters.BlockUserAdapter;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.FollowingModel;
import com.zeroitsolutions.ziloo.Models.UserModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.DataParsing;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BlockUserListA extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerview;
    BlockUserAdapter adapter;
    Context context;
    String userId;
    RelativeLayout noDataLayout;
    ArrayList<FollowingModel> datalist = new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, getClass(), false);
        setContentView(R.layout.activity_block_user_list);
        context = BlockUserListA.this;
        userId = Functions.getSharedPreference(context).getString(Variable.U_ID, "");
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        recyclerview= findViewById(R.id.recyclerview);
        noDataLayout = findViewById(R.id.no_data_layout);
        findViewById(R.id.ivBack).setOnClickListener(this);


        adapter = new BlockUserAdapter(datalist, (view, pos, object) -> {
            FollowingModel item = (FollowingModel) object;
            switch (view.getId()) {
                case R.id.block_layout:
                {
                    callApiUnBlock(item , pos);
                }
                    break;
                case R.id.mainLayout:
                {
                    openProfile(item);
                }
                    break;
                default:
                    break;
            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);

        callApiForgetBlockList();
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private void openProfile(final FollowingModel item) {

        String userName="";
        if (context != null) {
            userName=item.username;
        }
        else
        {
            userName=item.first_name + " " + item.last_name;
        }
        Intent intent=new Intent(context, ProfileA.class);
        intent.putExtra("user_id", item.fb_id);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_pic", item.profile_pic);
        resultCallback.launch(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void callApiForgetBlockList() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
            Functions.printLog(Constants.tag, "e : " + e.toString());
        }

        ApiVolleyRequest.JsonPostRequest(BlockUserListA.this, ApiLinks.showBlockedUsers, parameters, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(BlockUserListA.this,response);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                parseData(response);
            }

            @Override
            public void onError(String response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    public void parseData(String responce) {
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<FollowingModel> tempList = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {

                    JSONObject object = msgArray.optJSONObject(i);
                    UserModel userDetailModel= DataParsing.getUserDataModel(object.optJSONObject("BlockedUser"));
                    FollowingModel item = new FollowingModel();
                    item.fb_id = userDetailModel.getId();
                    item.first_name =userDetailModel.getFirstName();
                    item.last_name = userDetailModel.getLastName();
                    item.username = userDetailModel.getUsername();
                    item.profile_pic = userDetailModel.getProfilePic();

                    tempList.add(item);
                    datalist.clear();
                    datalist.addAll(tempList);
                }

            }
            if(datalist.isEmpty()){
                noDataLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                noDataLayout.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }


    private void callApiUnBlock(FollowingModel item, int pos) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", userId);
            params.put("block_user_id", item.fb_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(BlockUserListA.this, false, false);
        ApiVolleyRequest.JsonPostRequest(BlockUserListA.this, ApiLinks.blockUser, params, Functions.getHeaders(this), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(BlockUserListA.this,response);
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                    }
                    else
                    {
                        datalist.remove(pos);
                        adapter.notifyDataSetChanged();

                    }

                    if(datalist.isEmpty()){
                        noDataLayout.setVisibility(View.VISIBLE);
                    }else{
                        noDataLayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }

            @Override
            public void onError(String response) {

            }
        });

    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            callApiForgetBlockList();
                        }

                    }
                }
            });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;

            default:
                break;
        }
    }
}