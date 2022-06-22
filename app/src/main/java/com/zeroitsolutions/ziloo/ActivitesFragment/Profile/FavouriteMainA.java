package com.zeroitsolutions.ziloo.ActivitesFragment.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Favourite.FavouriteVideosF;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.ActivitesFragment.Search.SearchHashTagsF;
import com.zeroitsolutions.ziloo.ActivitesFragment.SoundLists.FavouriteSoundF;
import com.zeroitsolutions.ziloo.Adapters.ViewPagerAdapter;
import com.zeroitsolutions.ziloo.Interfaces.InternetCheckCallback;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

public class FavouriteMainA extends AppCompatActivity {

    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(FavouriteMainA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, FavouriteMainA.class,false);
        setContentView(R.layout.activity_favourite_main_);
        context = FavouriteMainA.this;

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteMainA.super.onBackPressed();
            }
        });

        Set_tabs();
    }


    // set up the favourite video sound and hastage fragment
    protected TabLayout tabLayout;
    protected ViewPager menuPager;
    ViewPagerAdapter adapter;

    public void Set_tabs() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        menuPager = (ViewPager) findViewById(R.id.viewpager);
        menuPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        adapter.addFrag(new FavouriteVideosF(), getString(R.string.videos));
        adapter.addFrag(new FavouriteSoundF(), getString(R.string.sounds));
        adapter.addFrag(new SearchHashTagsF("favourite"), getString(R.string.hashtag));


        menuPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(menuPager);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
            @Override
            public void GetResponse(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(getApplicationContext(), NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functions.unRegisterConnectivity(getApplicationContext());
    }


}
