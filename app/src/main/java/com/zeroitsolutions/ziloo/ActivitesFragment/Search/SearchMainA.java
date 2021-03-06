package com.zeroitsolutions.ziloo.ActivitesFragment.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zeroitsolutions.ziloo.ActivitesFragment.Profile.Setting.NoInternetA;
import com.zeroitsolutions.ziloo.Adapters.RecentSearchAdapter;
import com.zeroitsolutions.ziloo.Adapters.ViewPagerAdapter;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.Interfaces.InternetCheckCallback;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;

import io.paperdb.Paper;

public class SearchMainA extends AppCompatActivity implements View.OnClickListener {


    Context context;
    public static EditText searchEdit;
    TextView search_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(SearchMainA.this).getString(Variable.APP_LANGUAGE_CODE, Variable.DEFAULT_LANGUAGE_CODE)
                , this, SearchMainA.class,false);
        setContentView(R.layout.activity_search_main);
        context =SearchMainA.this;

        searchEdit = findViewById(R.id.search_edit);

        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this::onClick);


        showRecentSearch();

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchEdit.getText().toString().length() > 0) {
                    search_btn.setVisibility(View.VISIBLE);

                } else {
                    search_btn.setVisibility(View.GONE);
                }

                showRecentSearch();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEdit.setFocusable(true);
        UIUtil.showKeyboard(context, searchEdit);


        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();

                    findViewById(R.id.recent_layout).setVisibility(View.GONE);
                    addSearchKey(searchEdit.getText().toString());

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.clear_all_txt).setOnClickListener(this::onClick);
    }


    public void performSearch() {
        if (menuPager != null) {
            menuPager.removeAllViews();
        }
        setTabs();
    }


    protected TabLayout tabLayout;
    protected ViewPager menuPager;
    ViewPagerAdapter adapter;

    public void setTabs() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        menuPager = findViewById(R.id.viewpager);
        menuPager.setOffscreenPageLimit(3);
        tabLayout = findViewById(R.id.tabs);

        adapter.addFrag(new SearchUserF("user"), "Users");
        adapter.addFrag(new SearchVideoF("video"), "Videos");
        adapter.addFrag(new SearchSoundF("sound"), "Sounds");
        adapter.addFrag(new SearchHashTagsF("hashtag"), "HashTags");

        menuPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(menuPager);

    }

    public void addSearchKey(String search_key) {
        ArrayList<String> search_list = Paper.book().read("recent_search", new ArrayList<>());
        search_list.add(search_key);
        Paper.book().write("recent_search", search_list);

    }


    // this method will get the recent searched list from local db

    RecyclerView recyclerView;
    RecentSearchAdapter recentsearchAdapter;
    ArrayList<String> searchQueryList = new ArrayList<>();

    public void showRecentSearch() {
        ArrayList<String> search_list = Paper.book().read("recent_search", new ArrayList<>());

        searchQueryList.clear();
        searchQueryList.addAll(search_list);

        if (searchQueryList.isEmpty()) {
            findViewById(R.id.recent_layout).setVisibility(View.GONE);
            return;
        } else {
            findViewById(R.id.recent_layout).setVisibility(View.VISIBLE);
        }

        if (recentsearchAdapter != null) {
            recentsearchAdapter.getFilter().filter(searchEdit.getText().toString());
            recentsearchAdapter.notifyDataSetChanged();
            return;
        }

        findViewById(R.id.recent_layout).setVisibility(View.VISIBLE);
        recentsearchAdapter = new RecentSearchAdapter(context, searchQueryList, new AdapterClickListener() {
            @Override
            public void onItemClick(View v, int pos, Object object) {

                if (v.getId() == R.id.delete_btn) {
                    searchQueryList.remove(object);
                    recentsearchAdapter.notifyDataSetChanged();

                    search_list.remove(object);
                    Paper.book().write("recent_search", search_list);
                } else {

                    String search = (String) object;
                    searchEdit.setText(search);
                    performSearch();
                    findViewById(R.id.recent_layout).setVisibility(View.GONE);
                }

            }
        });
        recyclerView = findViewById(R.id.recylerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recentsearchAdapter);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_btn) {
            Functions.hideSoftKeyboard(SearchMainA.this);
            performSearch();
            findViewById(R.id.recent_layout).setVisibility(View.GONE);
            addSearchKey(searchEdit.getText().toString());
        } else if (id == R.id.clear_all_txt) {
            Paper.book().delete("recent_search");
            showRecentSearch();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, (requestType, response) -> {
            if(response.equalsIgnoreCase("disconnected")) {
                startActivity(new Intent(getApplicationContext(), NoInternetA.class));
                overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functions.unRegisterConnectivity(getApplicationContext());
    }
}
