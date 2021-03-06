package com.zeroitsolutions.ziloo.ActivitesFragment.SoundLists;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.zeroitsolutions.ziloo.Adapters.SoundsAdapter;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.MainMenu.RelateToFragmentOnBack.RootFragment;
import com.zeroitsolutions.ziloo.Models.SoundCatagoryModel;
import com.zeroitsolutions.ziloo.Models.SoundsModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscoverSoundListF extends RootFragment implements Player.Listener {

    public static String runningSoundId;
    static boolean active = false;
    RecyclerView recyclerView;
    SoundsAdapter adapter;
    ArrayList<SoundCatagoryModel> datalist;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout noDataLayout;
    DownloadRequest prDownloader;
    View view;
    Context context;
    SwipeRefreshLayout swiperefresh;
    ProgressBar pbar;
    ProgressBar loadMoreProgress;
    int pageCount = 0;
    boolean ispostFinsh;
    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Intent output = new Intent();
                        output.putExtra("isSelected", data.getStringExtra("isSelected"));
                        output.putExtra("sound_name", data.getStringExtra("sound_name"));
                        output.putExtra("sound_id", data.getStringExtra("sound_id"));
                        requireActivity().setResult(RESULT_OK, output);
                        requireActivity().finish();
                        requireActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                    }
                }
            });
    // initalize the player for the play the audio file
    View previousView;
    Thread thread;
    SimpleExoPlayer player;
    String previousUrl = "none";
    //run the progress download for showing the downloading state
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_sound_list, container, false);
        context = view.getContext();
        runningSoundId = "none";
        loadMoreProgress = view.findViewById(R.id.load_more_progress);

        PRDownloader.initialize(context);
        pbar = view.findViewById(R.id.pbar);
        noDataLayout = view.findViewById(R.id.no_data_layout);
        datalist = new ArrayList<>();

        recyclerView = view.findViewById(R.id.listview);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);

        setAdapter();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiForGetAllSound();
                    }
                }


            }
        });

        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(() -> {
            previousUrl = "none";
            stopPlaying();
            callApiForGetAllSound();
        });

        callApiForGetAllSound();
        return view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if ((view != null && visible)) {
            callApiForGetAllSound();
        } else {
            stopPlaying();
        }
    }

    public void setAdapter() {

        adapter = new SoundsAdapter(context, datalist, (view, postion, item) -> {

            int id = view.getId();
            if (id == R.id.see_all_btn) {
                openSectionList(postion);
            } else if (id == R.id.done) {
                stopPlaying();
                downLoadMp3(item.id, item.sound_name, item.acc_path);
            } else if (id == R.id.fav_btn) {
                callApiForFavSound(item);
            } else {
                if (thread != null && !thread.isAlive()) {
                    stopPlaying();
                    playaudio(view, item);
                } else if (thread == null) {
                    stopPlaying();
                    playaudio(view, item);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // call the api to get all the section of sound
    private void callApiForGetAllSound() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variable.U_ID, null));
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showSounds, parameters, Functions.getHeaders(getActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                swiperefresh.setRefreshing(false);
                pbar.setVisibility(View.GONE);
                parseData(response);
            }

            @Override
            public void onError(String response) {
                pbar.setVisibility(View.GONE);
            }
        });
    }


    // parse the data of sound list
    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.optJSONArray("msg");

                ArrayList<SoundCatagoryModel> temp_list = new ArrayList<>();
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject object = msg.optJSONObject(i);
                    JSONObject soundSection = object.optJSONObject("SoundSection");
                    JSONArray soundObj = object.optJSONArray("Sound");
                    ArrayList<SoundsModel> sound_list = new ArrayList<>();
                    sound_list.clear();
                    for (int j = 0; j < soundObj.length(); j++) {
                        JSONObject sound = soundObj.optJSONObject(j);
                        SoundsModel item = new SoundsModel();
                        item.id = sound.optString("id");
                        String accPath = sound.optString("audio");
                        String type = sound.getString("type");
                        if (!type.isEmpty()) {
                            if (type.equals("live")) {
                                if (accPath.contains("http"))
                                    item.acc_path = sound.optString("audio");
                                else
                                    item.acc_path = Constants.BASE_LIVE_AUDIO_URL + sound.optString("audio");
                            } else {
                                if (accPath.contains("http"))
                                    item.acc_path = sound.optString("audio");
                                else
                                    item.acc_path = Constants.BASE_MEDIA_URL + sound.optString("audio");
                            }
                        }

                        item.sound_name = sound.optString("name");
                        item.description = sound.optString("description");
                        item.section = sound.optString("section");
                        String thum_image = sound.optString("thum");

                        if (!thum_image.isEmpty() && thum_image.contains("http"))
                            item.thum = sound.optString("thum");
                        else
                            item.thum = Constants.BASE_MEDIA_URL + sound.optString("thum");

                        item.duration = sound.optString("duration");
                        item.date_created = sound.optString("created");
                        item.fav = sound.optString("favourite");

                        sound_list.add(item);
                    }

                    SoundCatagoryModel sound_catagoryModel = new SoundCatagoryModel();
                    sound_catagoryModel.id = soundSection.optString("id");
                    sound_catagoryModel.catagory = soundSection.optString("name");
                    sound_catagoryModel.sound_list = sound_list;

                    temp_list.add(sound_catagoryModel);

                }

                if (pageCount == 0) {
                    datalist.clear();
                }

                datalist.addAll(temp_list);
                adapter.notifyDataSetChanged();

            } else {
                noDataLayout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Functions.printLog(Constants.tag, e.toString());
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }

    // open the video list against the section id
    public void openSectionList(int pos) {
        SoundCatagoryModel item = datalist.get(pos);

        Intent intent = new Intent(view.getContext(), SectionSoundListA.class);
        intent.putExtra("id", item.id);
        intent.putExtra("name", item.catagory);
        resultCallback.launch(intent);
        requireActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    @Override
    public boolean onBackPressed() {
        requireActivity().onBackPressed();
        return super.onBackPressed();
    }

    public void playaudio(View view, final SoundsModel item) {
        previousView = view;

        if (previousUrl.equals(item.acc_path)) {
            previousUrl = "none";
            runningSoundId = "none";
        } else {
            previousUrl = item.acc_path;
            runningSoundId = item.id;

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);

            player = new SimpleExoPlayer.Builder(context).
                    setTrackSelector(trackSelector)
                    .build();

            DataSource.Factory cacheDataSourceFactory = new DefaultDataSourceFactory(view.getContext(), context.getString(R.string.app_name));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(item.acc_path));
            player.addMediaSource(videoSource);
            player.prepare();
            player.addListener(this);
            player.setPlayWhenReady(true);

            try {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE)
                        .build();
                player.setAudioAttributes(audioAttributes, true);
            } catch (Exception e) {
                Log.d(Constants.tag, "Exception audio focus : " + e);
            }
        }
    }

    // stop the player
    public void stopPlaying() {

        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }
        showStopState();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;

        runningSoundId = "null";

        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }
        showStopState();
    }

    // show the state of the player
    public void showRunState() {

        if (previousView != null) {
            previousView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previousView.findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
            View imgDone = previousView.findViewById(R.id.done);
            View imgFav = previousView.findViewById(R.id.fav_btn);
            imgFav.animate().translationX(0).setDuration(400).start();
            imgDone.animate().translationX(0).setDuration(400).start();
        }

    }

    //show the loading state
    public void showLoadingState() {
        previousView.findViewById(R.id.play_btn).setVisibility(View.GONE);
        previousView.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
    }

    public void showStopState() {

        if (previousView != null) {
            previousView.findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
            previousView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previousView.findViewById(R.id.pause_btn).setVisibility(View.GONE);
            View imgDone = previousView.findViewById(R.id.done);
            View imgFav = previousView.findViewById(R.id.fav_btn);
            imgDone.animate().translationX(Float.parseFloat("" + getResources().getDimension(R.dimen._80sdp))).setDuration(400).start();
            imgFav.animate().translationX(Float.parseFloat("" + getResources().getDimension(R.dimen._50sdp))).setDuration(400).start();
        }

        runningSoundId = "none";

    }

    public void downLoadMp3(final String id, final String sound_name, String url) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(view.getContext().getString(R.string.please_wait_));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        prDownloader = PRDownloader.download(url, Functions.getAppFolder(requireActivity()) + Variable.APP_HIDED_FOLDER, Variable.SelectedAudio_AAC)
                .build();

        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                progressDialog.dismiss();

                Intent output = new Intent();
                output.putExtra("isSelected", "yes");
                output.putExtra("sound_name", sound_name);
                output.putExtra("sound_id", id);
                requireActivity().setResult(RESULT_OK, output);
                requireActivity().finish();
                requireActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
            }
        });
    }

    // call the api for favourite the sound
    private void callApiForFavSound(final SoundsModel item) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variable.U_ID, "0"));
            parameters.put("sound_id", item.id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(context, false, false);
        ApiVolleyRequest.JsonPostRequest(getActivity(), ApiLinks.addSoundFavourite, parameters, Functions.getHeaders(getActivity()), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(getActivity(), response);
                Functions.cancelLoader();

                if (item.fav.equals("1"))
                    item.fav = "0";
                else
                    item.fav = "1";

                for (int i = 0; i < datalist.size(); i++) {
                    SoundCatagoryModel catagory_get_set = datalist.get(i);
                    if (catagory_get_set.sound_list.contains(item)) {
                        int index = catagory_get_set.sound_list.indexOf(item);
                        catagory_get_set.sound_list.remove(item);
                        catagory_get_set.sound_list.add(index, item);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    // handle will be call on player state change
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == Player.STATE_BUFFERING) {
            showLoadingState();
        } else if (playbackState == Player.STATE_READY) {
            showRunState();
        } else if (playbackState == Player.STATE_ENDED) {
            showStopState();
        }
    }
}
