package com.zeroitsolutions.ziloo.SimpleClasses;

import static com.zeroitsolutions.ziloo.SimpleClasses.ImagePipelineConfigUtils.getDefaultImagePipelineConfig;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.Constants;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.rtc.AgoraEventHandler;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.rtc.EngineConfig;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.rtc.EventHandler;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.stats.StatsManager;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.utils.FileUtil;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.utils.PrefManager;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.activities.CustomErrorActivity;

import java.io.File;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.agora.rtc.RtcEngine;
import io.paperdb.Paper;


/**
 * Created by qboxus on 3/18/2019.
 */

public class Ziloo extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context appLevelContext;
    public static SimpleCache simpleCache = null;
    public static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    public static ExoDatabaseProvider exoDatabaseProvider = null;
    public static Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);
    private final EngineConfig mGlobalConfig = new EngineConfig();
    private final AgoraEventHandler mHandler = new AgoraEventHandler();
    private final StatsManager mStatsManager = new StatsManager();
    private HttpProxyCacheServer proxy;
    private RtcEngine mRtcEngine;

    // below code is for cache the videos in local
    public static HttpProxyCacheServer getProxy(Context context) {
        Ziloo app = (Ziloo) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appLevelContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Fresco.initialize(this, getDefaultImagePipelineConfig(this));
        }
        Paper.init(this);
        FirebaseApp.initializeApp(this);
        addFirebaseToken();

        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                freeMemory();
            }
        }

        initCrashActivity();
        initConfig();
        Functions.createNoMediaFile(getApplicationContext());
    }

    public void addFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(com.zeroitsolutions.ziloo.Constants.tag, "token: " + token);
                    SharedPreferences.Editor editor = Functions.getSharedPreference(getApplicationContext()).edit();
                    editor.putString(Variable.DEVICE_TOKEN, "" + token);
                    editor.apply();
                });
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .cacheDirectory(new File(Functions.getAppFolder(this) + "videoCache"))
                .build();
    }

    // check how much memory is available for cache video
    public void freeMemory() {

        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    // delete the cache if it is full
    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void initConfig() {

        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    public void initCrashActivity() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)
                .showErrorDetails(true)
                .showRestartButton(true)
                .logErrorOnRestart(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                .restartActivity(CustomErrorActivity.class)
                .errorActivity(CustomErrorActivity.class)
                .apply();
    }
}