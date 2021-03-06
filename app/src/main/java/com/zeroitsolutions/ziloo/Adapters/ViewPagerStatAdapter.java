package com.zeroitsolutions.ziloo.Adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.zeroitsolutions.ziloo.ActivitesFragment.VideosListF;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Interfaces.FragmentCallBack;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import io.paperdb.Paper;

public class ViewPagerStatAdapter extends FragmentStatePagerAdapter {
    private static int PAGE_REFRESH_STATE = PagerAdapter.POSITION_UNCHANGED;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    VerticalViewPager menuPager;

    FragmentCallBack callBack;

    public ViewPagerStatAdapter(@NonNull FragmentManager fm, VerticalViewPager menuPager, boolean isFirstTime, FragmentCallBack callBack) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.menuPager = menuPager;
        this.callBack = callBack;

        if (isFirstTime) {
            Log.d(Constants.tag, "Check : init ");
            if (Paper.book(Variable.PromoAds).contains(Variable.PromoAdsModel)) {
                HomeModel initItem = Paper.book(Variable.PromoAds).read(Variable.PromoAdsModel);
                if (initItem != null)
                    addFragment(new VideosListF(true, initItem, menuPager, callBack, R.id.mainMenuFragment), "");
            }
        }
    }

    public void refreshStateSet(boolean isRefresh) {
        if (isRefresh) {
            PAGE_REFRESH_STATE = PagerAdapter.POSITION_NONE;
        } else {
            PAGE_REFRESH_STATE = PagerAdapter.POSITION_UNCHANGED;
        }
    }


    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void removeFragment(int position) {
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        Log.d(Constants.tag, "Check : Refresh " + PAGE_REFRESH_STATE);

        return PAGE_REFRESH_STATE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}