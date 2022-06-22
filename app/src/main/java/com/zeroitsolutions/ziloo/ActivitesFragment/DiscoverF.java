package com.zeroitsolutions.ziloo.ActivitesFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rd.PageIndicatorView;
import com.zeroitsolutions.ziloo.ActivitesFragment.Search.SearchMainA;
import com.zeroitsolutions.ziloo.Adapters.DiscoverAdapter;
import com.zeroitsolutions.ziloo.Adapters.SlidingAdapter;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Interfaces.AdapterClickListener;
import com.zeroitsolutions.ziloo.MainMenu.RelateToFragmentOnBack.RootFragment;
import com.zeroitsolutions.ziloo.Models.DiscoverModel;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.Models.SliderModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;
import com.zeroitsolutions.ziloo.activities.TagedVideosA;
import com.zeroitsolutions.ziloo.activities.WatchVideosA;
import com.zeroitsolutions.ziloo.activities.WebviewA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverF extends RootFragment implements View.OnClickListener {

    View view;
    Context context;
    RecyclerView recyclerViewDiscover;
    SwipeRefreshLayout swiperefresh;

    ArrayList<DiscoverModel> datalist = new ArrayList<>();
    DiscoverAdapter adapter;
    PageIndicatorView pageIndicatorView;
    ViewPager viewPager;

    ShimmerFrameLayout shimmerFrameLayout;
    CoordinatorLayout dataContainer;
    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;


    boolean isDiscoverAPiCall = false;
    boolean isSliderApiCall = false;
    ArrayList<SliderModel> slider_list = new ArrayList<>();

    public DiscoverF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        context = getContext();


        datalist = new ArrayList<>();
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        dataContainer = view.findViewById(R.id.dataContainer);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerViewDiscover = view.findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewDiscover.setLayoutManager(linearLayoutManager);
        recyclerViewDiscover.setHasFixedSize(true);


        adapter = new DiscoverAdapter(context, datalist, new DiscoverAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList<HomeModel> home_models, int parent_postion, int child_position) {


                if (view.getId() == R.id.hashtag_layout || home_models.get(child_position).thum == null) {
                    openHashtag(datalist.get(parent_postion).title);
                } else {
                    openWatchVideo(child_position, home_models, datalist.get(parent_postion).title);
                }


            }
        });
        recyclerViewDiscover.setAdapter(adapter);

        recyclerViewDiscover.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems, scrollInItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollInItem = linearLayoutManager.findFirstVisibleItemPosition();
                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

//                if (scrollInItem == 0)
//                {
//                    recyclerViewDiscover.setNestedScrollingEnabled(true);
//                }
//                else
//                {
//                    recyclerViewDiscover.setNestedScrollingEnabled(false);
//                }
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiForAllvideos();
                    }
                }


            }
        });


        viewPager = view.findViewById(R.id.viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (datalist.size() < 1) {
                    dataContainer.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();
                }
                callApiSlider();
                pageCount = 0;
                callApiForAllvideos();
            }
        });

        view.findViewById(R.id.search_layout).setOnClickListener(this);
        view.findViewById(R.id.search_edit).setOnClickListener(this);


        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && datalist.size() < 1) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    shimmerFrameLayout.startShimmer();
                    callApiForAllvideos();
                    callApiSlider();
                }
            }, 200);
        }
    }

    // get the image of the upper slider in the discover screen
    private void callApiSlider() {
        if (isSliderApiCall) {
            return;
        }
        isSliderApiCall = true;

        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showAppSlider, new JSONObject(), Functions.getHeaders(getActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(getActivity(),resp);
                isSliderApiCall=false;
                parseSliderData(resp);
            }

            @Override
            public void onError(String response) {

            }
        });

    }

    public void parseSliderData(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);

            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                slider_list.clear();

                JSONArray msg = jsonObject.optJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject object = msg.optJSONObject(i);
                    JSONObject AppSlider = object.optJSONObject("AppSlider");

                    SliderModel sliderModel = new SliderModel();
                    sliderModel.id = AppSlider.optString("id");
                    sliderModel.image = AppSlider.optString("image");
                    sliderModel.url = AppSlider.optString("url");

                    slider_list.add(sliderModel);
                }

                setSliderAdapter();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setSliderAdapter() {

        pageIndicatorView.setCount(slider_list.size());
        pageIndicatorView.setSelection(0);

        viewPager.setAdapter(new SlidingAdapter(getActivity(), slider_list, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                String slider_url = slider_list.get(pos).url;
                if (slider_url != null && !slider_url.equals("")) {

                    Intent intent = new Intent(view.getContext(), WebviewA.class);
                    intent.putExtra("url", slider_url);
                    intent.putExtra("title", "Link");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            }
        }));

        pageIndicatorView.setViewPager(viewPager);

    }

    // Bottom two function will get the Discover videos
    // from api and parse the json data which is shown in Discover tab

    private void callApiForAllvideos() {
        if (isDiscoverAPiCall) {
            return;
        }
        isDiscoverAPiCall = true;
        if (datalist == null)
            datalist = new ArrayList<>();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("starting_point", "" + pageCount);
            if (Functions.getSharedPreference(view.getContext()).getBoolean(Variable.IS_LOGIN, false)) {
                parameters.put("user_id", Functions.getSharedPreference(view.getContext()).getString(Variable.U_ID, ""));
            }
        } catch (Exception e) {
            Log.d(Constants.tag, "Exception : " + e);
        }

        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showDiscoverySections, parameters, Functions.getHeaders(getActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(getActivity(), response);
                isDiscoverAPiCall = false;
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                dataContainer.setVisibility(View.VISIBLE);
                swiperefresh.setRefreshing(false);
                parseData(response);
            }

            @Override
            public void onError(String response) {
                isDiscoverAPiCall = false;
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                dataContainer.setVisibility(View.VISIBLE);
                swiperefresh.setRefreshing(false);
            }
        });
    }


    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                ArrayList<DiscoverModel> temp_list = new ArrayList<>();

                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int d = 0; d < msgArray.length(); d++) {

                    JSONObject discover_object = msgArray.optJSONObject(d);
                    JSONObject hashtag = discover_object.optJSONObject("Hashtag");

                    DiscoverModel discover_model = new DiscoverModel();
                    discover_model.id = hashtag.optString("id");
                    discover_model.title = hashtag.optString("name");
                    discover_model.views = hashtag.optString("views");
                    discover_model.videos_count = hashtag.optString("videos_count");
                    discover_model.fav = hashtag.optString("favourite", "0");

                    JSONArray video_array = hashtag.optJSONArray("Videos");

                    ArrayList<HomeModel> video_list = new ArrayList<>();
                    for (int i = 0; i < video_array.length(); i++) {
                        JSONObject itemdata = video_array.optJSONObject(i);

                        JSONObject video = itemdata.optJSONObject("Video");
                        JSONObject user = video.optJSONObject("User");
                        JSONObject sound = video.optJSONObject("Sound");
                        JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                        JSONObject userPushNotification = user.optJSONObject("PushNotification");

                        HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);

                        if (item.username != null && !(item.username.equals("null"))) {
                            video_list.add(item);
                        }
                    }

                    if (video_list.size() >= 5)
                        video_list.add(new HomeModel());

                    discover_model.arrayList = video_list;

                    temp_list.add(discover_model);

                }

                if (pageCount == 0) {
                    datalist.clear();
                    datalist.addAll(temp_list);
                } else {
                    datalist.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();
            } else {
                if (datalist.isEmpty()) {
                    view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                }
                loadMoreProgress.setVisibility(View.GONE);
            }

            if (datalist.isEmpty()) {
                view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }


    // When you click on any Video a new activity is open which will play the Clicked video
    private void openWatchVideo(int postion, ArrayList<HomeModel> data_list, String hashtag) {
        if (data_list.size() > 5)
            data_list.remove(data_list.size() - 1);

        Intent intent = new Intent(view.getContext(), WatchVideosA.class);
        intent.putExtra("arraylist", data_list);
        intent.putExtra("position", postion);
        intent.putExtra("pageCount", 0);
        intent.putExtra("hashtag", hashtag);
        intent.putExtra("userId", Functions.getSharedPreference(view.getContext()).getString(Variable.U_ID, ""));
        intent.putExtra("whereFrom", "tagedVideo");
        startActivity(intent);
    }

    public void openSearch() {

        Intent intent = new Intent(context, SearchMainA.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_layout:
                openSearch();
                break;
            case R.id.search_edit:
                openSearch();
                break;
            default:
                return;
        }
    }

    private void openHashtag(String tag) {
        Intent intent = new Intent(view.getContext(), TagedVideosA.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }
}
