package com.zeroitsolutions.ziloo.ActivitesFragment.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.zeroitsolutions.ziloo.Accounts.LoginA;
import com.zeroitsolutions.ziloo.Accounts.ManageAccountsF;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.activities.StreamingMain_A;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.LikedVideos.LikedVideoF;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.PrivateVideos.PrivateVideoF;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.UserVideos.UserVideoF;
import com.zeroitsolutions.ziloo.ActivitesFragment.VideoRecording.DraftVideosA;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.MainMenu.RelateToFragmentOnBack.RootFragment;
import com.zeroitsolutions.ziloo.Models.PrivacyPolicySettingModel;
import com.zeroitsolutions.ziloo.Models.PushNotificationSettingModel;
import com.zeroitsolutions.ziloo.Models.UserModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.DataParsing;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.PermissionUtils;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.zeroitsolutions.ziloo.activities.WebviewA;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class ProfileTabF extends RootFragment implements View.OnClickListener {
    protected TabLayout tabLayout;
    protected ViewPager pager;
    View view;
    Context context;
    String totalLikes = "";
    ImageView settingBtn, favBtn, ivDraftVideo;
    PushNotificationSettingModel pushNotificationSettingModel;
    PrivacyPolicySettingModel privacyPolicySettingModel;
    PermissionUtils takePermissionUtils;
    private TextView username, username2Txt, tvBio, tvLink, tvEditProfile;
    private SimpleDraweeView imageView;
    private TextView followCountTxt, fansCountTxt, heartCountTxt;
    private LinearLayout tabAccount, tabLink;
    private ViewPagerAdapter adapter;
    private String picUrl, followerCount, followingCount;
    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    if (data.getBooleanExtra("isShow", false)) {
                        updateProfile();
                    }
                }
            });

    private LinearLayout createPopupLayout, tabPrivacyLikes;
    private int myvideoCount = 0;
    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(), key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(view.getContext(), getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                    } else if (allPermissionClear) {
                        goLive();
                    }

                }
            });

    public ProfileTabF() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_tab, container, false);
        context = getContext();
        return init();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_btn: {
                if (takePermissionUtils.isCameraRecordingPermissionGranted()) {
                    goLive();
                } else {
                    takePermissionUtils.showCameraRecordingPermissionDailog(getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                }
            }
            break;

            case R.id.user_image:
                openProfileShareTab();
                break;

            case R.id.edit_profile_btn:
                openEditProfile();
                break;
            case R.id.message_btn: {
                Intent intent = new Intent(view.getContext(), SettingAndPrivacyA.class);
                startActivity(intent);
            }
            break;
            case R.id.tabLink:
                openWebUrl(view.getContext().getString(R.string.web_browser), "" + tvLink.getText().toString());
                break;

            case R.id.tabAccount:
                openManageMultipleAccounts();
                break;

            case R.id.following_layout:
                openFollowing();
                break;

            case R.id.fans_layout:
                openFollowers();
                break;
            case R.id.invite_btn:
                openInviteFriends();
                break;
            case R.id.favBtn:
                openFavouriteVideos();
                break;

            case R.id.tabPrivacyLikes:
                showMyLikesCounts();
                break;

//            case R.id.draft_btn:
//                Intent upload_intent = new Intent(getActivity(), DraftVideos_A.class);
//                startActivity(upload_intent);
//                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
//                break;

//            case R.id.ivDraftVideo:
//                Intent upload_intent = new Intent(getActivity(), DraftVideosA.class);
//                startActivity(upload_intent);
//                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
//                break;
        }
    }

    private void goLive() {
        String user_id = Functions.getSharedPreference(context).getString(Variable.U_ID, "");
        String user_name = Functions.getSharedPreference(context).getString(Variable.F_NAME, "") + " " +
                Functions.getSharedPreference(context).getString(Variable.L_NAME, "");
        String user_image = Functions.getSharedPreference(context).getString(Variable.U_PIC, "");
        openZilooLive(user_id, user_name, user_image, io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
    }

    private void openInviteFriends() {
        Intent intent = new Intent(view.getContext(), InviteFriendsA.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openProfileShareTab() {

        final ShareAndViewProfileF fragment = new ShareAndViewProfileF(Functions.getSharedPreference(view.getContext()).getString(Variable.U_ID, "")
                , true, picUrl, bundle -> {
            if (bundle.getString("action").equals("profileShareMessage")) {
                if (Functions.checkLoginUser(getActivity())) {
                    // firebase sharing
                }
            }
        });
        fragment.show(getChildFragmentManager(), "");

    }

    @SuppressLint("SetTextI18n")
    private void showMyLikesCounts() {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.show_likes_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextView tvMessage, tvDone;
        tvDone = dialog.findViewById(R.id.tvDone);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(username.getText() + " " + view.getContext().getString(R.string.received_a_total_of) + " " + totalLikes + " " + view.getContext().getString(R.string.likes_across_all_video));
        tvDone.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void openWebUrl(String title, String url) {
        Intent intent = new Intent(view.getContext(), WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openManageMultipleAccounts() {
        ManageAccountsF f = new ManageAccountsF(bundle -> {
            if (bundle.getBoolean("isShow", false)) {
                Functions.hideSoftKeyboard(requireActivity());
                Intent intent = new Intent(requireActivity(), LoginA.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        });
        f.show(getChildFragmentManager(), "");
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (Functions.getSharedPreference(context).getBoolean(Variable.IS_LOGIN, false)) {
                    updateProfile();
                    callApiForGetAllvideos();
                }
            }, 200);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showDraftCount();

    }

    private View init() {

        takePermissionUtils = new PermissionUtils(getActivity(), mPermissionResult);
        view.findViewById(R.id.live_btn).setOnClickListener(this);

        username = view.findViewById(R.id.username);
        username2Txt = view.findViewById(R.id.username2_txt);
        tvLink = view.findViewById(R.id.tvLink);
        tvBio = view.findViewById(R.id.tvBio);
        imageView = view.findViewById(R.id.user_image);
        imageView.setOnClickListener(this);

        followCountTxt = view.findViewById(R.id.follow_count_txt);
        fansCountTxt = view.findViewById(R.id.fan_count_txt);
        heartCountTxt = view.findViewById(R.id.heart_count_txt);

        showDraftCount();

        tvEditProfile = view.findViewById(R.id.edit_profile_btn);
        tvEditProfile.setOnClickListener(this);

        tabAccount = view.findViewById(R.id.tabAccount);
        tabAccount.setOnClickListener(this);

        tabLink = view.findViewById(R.id.tabLink);
        tabLink.setOnClickListener(this);

        settingBtn = view.findViewById(R.id.message_btn);
        settingBtn.setOnClickListener(this);

        favBtn = view.findViewById(R.id.favBtn);
        favBtn.setOnClickListener(this);

        tabPrivacyLikes = view.findViewById(R.id.tabPrivacyLikes);
        tabPrivacyLikes.setOnClickListener(this);

        tabLayout = view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        setupTabIcons();

        createPopupLayout = view.findViewById(R.id.create_popup_layout);

        view.findViewById(R.id.following_layout).setOnClickListener(this);
        view.findViewById(R.id.fans_layout).setOnClickListener(this);
        view.findViewById(R.id.invite_btn).setOnClickListener(this);
        view.findViewById(R.id.ivDraftVideo).setOnClickListener(this);
        return view;
    }

    public void showDraftCount() {
        try {
            String path = Functions.getAppFolder(requireActivity()) + Variable.DRAFT_APP_FOLDER;
            File directory = new File(path);
            File[] files = directory.listFiles();
//            if (files != null) {
//                if (files.length <= 0) {
//                    //draf gone
//                } else {
//                    //draf visible
//                }
//            }
        } catch (Exception ignored) {

        }
    }

    // place the profile data
    @SuppressLint("SetTextI18n")
    private void updateProfile() {
        username2Txt.setText(Functions.showUsername(Functions.getSharedPreference(context).getString(Variable.U_NAME, "")));
        String firstName = Functions.getSharedPreference(context).getString(Variable.F_NAME, "");
        String lastName = Functions.getSharedPreference(context).getString(Variable.L_NAME, "");
        if (firstName.trim().equalsIgnoreCase("") && lastName.trim().equalsIgnoreCase("")) {
            username.setText(Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
        } else {
            username.setText(firstName.trim() + " " + lastName.trim());
        }

        if (TextUtils.isEmpty(Functions.getSharedPreference(context).getString(Variable.U_BIO, ""))) {
            tvBio.setVisibility(View.GONE);
        } else {
            tvBio.setVisibility(View.GONE);
            if (Functions.getSharedPreference(context).getString(Variable.U_BIO, "") != "null")
                tvBio.setText(Functions.getSharedPreference(context).getString(Variable.U_BIO, ""));
            else
                tvBio.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(Functions.getSharedPreference(context).getString(Variable.U_LINK, ""))) {
            tabLink.setVisibility(View.GONE);
        } else {
            tabLink.setVisibility(View.GONE);
            tvLink.setText(Functions.getSharedPreference(context).getString(Variable.U_LINK, ""));
        }

        picUrl = Functions.getSharedPreference(context).getString(Variable.U_PIC, "null");

        imageView.setController(Functions.frescoImageLoad(picUrl, imageView, false));

    }

    // change the icons of the tab
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupTabIcons() {

        View view1 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
        ImageView imageView1 = view1.findViewById(R.id.image);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_color));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
        ImageView imageView2 = view2.findViewById(R.id.image);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_gray));
        tabLayout.getTabAt(1).setCustomView(view2);

        View view3 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
        ImageView imageView3 = view3.findViewById(R.id.image);
        imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_gray));
        tabLayout.getTabAt(2).setCustomView(view3);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                assert v != null;
                ImageView image = v.findViewById(R.id.image);

                switch (tab.getPosition()) {
                    case 0:

                        if (myvideoCount > 0) {
                            createPopupLayout.setVisibility(View.GONE);
                        } else {
                            createPopupLayout.setVisibility(View.VISIBLE);
                            Animation aniRotate = AnimationUtils.loadAnimation(context, R.anim.up_and_down_animation);
                            createPopupLayout.startAnimation(aniRotate);
                        }
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_color));
                        break;

                    case 1:
                        createPopupLayout.clearAnimation();
                        createPopupLayout.setVisibility(View.GONE);
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_color));
                        break;

                    case 2:
                        createPopupLayout.clearAnimation();
                        createPopupLayout.setVisibility(View.GONE);
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_black));
                        break;
                }
                tab.setCustomView(v);
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                assert v != null;
                ImageView image = v.findViewById(R.id.image);

                switch (tab.getPosition()) {
                    case 0:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_gray));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_gray));
                        break;

                    case 2:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_gray));
                        break;
                }

                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }

    //this will get the all videos data of user and then parse the data
    private void callApiForGetAllvideos() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variable.U_ID, ""));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showUserDetail, parameters, Functions.getHeaders(getActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(getActivity(), resp);
                parseData(resp);
                Functions.cancelLoader();
            }

            @Override
            public void onError(String response) {
                Functions.cancelLoader();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void parseData(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONObject msg = jsonObject.optJSONObject("msg");
                assert msg != null;
                JSONObject pushNotificationSetting = msg.optJSONObject("PushNotification");
                JSONObject privacyPolicySetting = msg.optJSONObject("PrivacySetting");

                UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));
                JSONObject user = msg.optJSONObject("User");
                String profile_pic = user.optString("profile_pic");

                pushNotificationSettingModel = new PushNotificationSettingModel();
                if (pushNotificationSetting != null) {
                    pushNotificationSettingModel.setComments("" + pushNotificationSetting.optString("comments"));
                    pushNotificationSettingModel.setLikes("" + pushNotificationSetting.optString("likes"));
                    pushNotificationSettingModel.setNewfollowers("" + pushNotificationSetting.optString("new_followers"));
                    pushNotificationSettingModel.setMentions("" + pushNotificationSetting.optString("mentions"));
                    pushNotificationSettingModel.setDirectmessage("" + pushNotificationSetting.optString("direct_messages"));
                    pushNotificationSettingModel.setVideoupdates("" + pushNotificationSetting.optString("video_updates"));
                }

                privacyPolicySettingModel = new PrivacyPolicySettingModel();
                if (privacyPolicySetting != null) {
                    privacyPolicySettingModel.setVideos_download("" + privacyPolicySetting.optString("videos_download"));
                    privacyPolicySettingModel.setDirect_message("" + privacyPolicySetting.optString("direct_message"));
                    privacyPolicySettingModel.setDuet("" + privacyPolicySetting.optString("duet"));
                    privacyPolicySettingModel.setLiked_videos("" + privacyPolicySetting.optString("liked_videos"));
                    privacyPolicySettingModel.setVideo_comment("" + privacyPolicySetting.optString("video_comment"));
                }

                Paper.book(Variable.PrivacySetting).write(Variable.PushSettingModel, pushNotificationSettingModel);
                Paper.book(Variable.PrivacySetting).write(Variable.PrivacySettingModel, privacyPolicySettingModel);

                username2Txt.setText(Functions.showUsername(userDetailModel.getUsername()));


                if (TextUtils.isEmpty(userDetailModel.getBio())) {
                    tvBio.setVisibility(View.GONE);
                } else {
                    tvBio.setVisibility(View.GONE);
                    tvBio.setText(userDetailModel.getBio());
                }

                if (TextUtils.isEmpty(userDetailModel.getWebsite())) {
                    tabLink.setVisibility(View.GONE);
                } else {
                    tabLink.setVisibility(View.GONE);
                    tvLink.setText(userDetailModel.getWebsite());
                }

                String firstName = userDetailModel.getFirstName();
                String lastName = userDetailModel.getLastName();

                if (firstName.equalsIgnoreCase("") && lastName.equalsIgnoreCase("")) {
                    username.setText(userDetailModel.getUsername());
                } else {
                    username.setText(firstName + " " + lastName);
                }

                picUrl = userDetailModel.getProfilePic();

                imageView.setController(Functions.frescoImageLoad(profile_pic, imageView, false));

                SharedPreferences.Editor editor = Functions.getSharedPreference(view.getContext()).edit();
                editor.putString(Variable.U_PIC, picUrl);
                editor.putString(Variable.U_WALLET, "" + userDetailModel.getWallet());
                editor.apply();

                followingCount = userDetailModel.getFollowingCount();
                followerCount = userDetailModel.getFollowersCount();
                totalLikes = userDetailModel.getLikesCount();
                followCountTxt.setText(Functions.getSuffix(followingCount));
                fansCountTxt.setText(Functions.getSuffix(followerCount));
                heartCountTxt.setText(Functions.getSuffix(totalLikes));

                myvideoCount = Functions.parseInterger(userDetailModel.getVideoCount());

                if (myvideoCount != 0) {
                    createPopupLayout.setVisibility(View.GONE);
                    createPopupLayout.clearAnimation();
                } else {

                    createPopupLayout.setVisibility(View.VISIBLE);
                    Animation aniRotate = AnimationUtils.loadAnimation(context, R.anim.up_and_down_animation);
                    createPopupLayout.startAnimation(aniRotate);

                }

                String verified = userDetailModel.getVerified();
                if (verified != null && verified.equalsIgnoreCase("1")) {
                    view.findViewById(R.id.varified_btn).setVisibility(View.VISIBLE);
                }


            } else {
                Functions.showToast(requireActivity(), jsonObject.optString("msg"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openEditProfile() {
        Intent intent = new Intent(view.getContext(), EditProfileA.class);
        resultCallback.launch(intent);
        requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    // open the favourite videos fragment
    private void openFavouriteVideos() {
        Intent intent = new Intent(view.getContext(), FavouriteMainA.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // open the following fragment
    private void openFollowing() {
        Intent intent = new Intent(view.getContext(), FollowsMainTabA.class);
        intent.putExtra("id", Functions.getSharedPreference(context).getString(Variable.U_ID, ""));
        intent.putExtra("from_where", "following");
        intent.putExtra("userName", Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
        intent.putExtra("followingCount", "" + followingCount);
        intent.putExtra("followerCount", "" + followerCount);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }


    // open the followers fragment
    private void openFollowers() {
        Intent intent = new Intent(view.getContext(), FollowsMainTabA.class);
        intent.putExtra("id", Functions.getSharedPreference(context).getString(Variable.U_ID, ""));
        intent.putExtra("from_where", "fan");
        intent.putExtra("userName", Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
        intent.putExtra("followingCount", "" + followingCount);
        intent.putExtra("followerCount", "" + followerCount);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // this will erase all the user info store in locally and logout the user


    // open the live streaming
    public void openZilooLive(String user_id, String user_name, String user_image, int role) {
        Intent intent = new Intent(getActivity(), StreamingMain_A.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("user_name", user_name);
        intent.putExtra("user_picture", user_image);
        intent.putExtra("user_role", role);
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPermissionResult.unregister();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new UserVideoF(true, Functions.getSharedPreference(context).getString(Variable.U_ID, ""), Functions.getSharedPreference(context).getString(Variable.U_NAME, ""), "");
                    break;
                case 1:
                    result = new LikedVideoF(true, Functions.getSharedPreference(context).getString(Variable.U_ID, ""), Functions.getSharedPreference(context).getString(Variable.U_NAME, ""), true, "");
                    break;

                case 2:
                    result = new PrivateVideoF();
                    break;

                default:
                    result = null;
                    break;
            }

            assert result != null;
            return result;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return null;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
