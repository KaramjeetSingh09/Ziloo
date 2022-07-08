package com.zeroitsolutions.ziloo;

public class Constants {

    public static final String HOST_URL = "http://ziloo.live/webapp/";
    public static final String BASE_URL_DEVELOPER = "http://ziloo.live/api/";

    public static final String BASE_URL = HOST_URL+"api/";

    public static final String API_KEY = "53aee794-c376-49f8-8df9-5df6e9668bee";
    public static final String BASE_MEDIA_URL =HOST_URL;
    public static final String BASE_AUDIO_URL = HOST_URL+"app/webroot/uploads";
    public static final String BASE_LIVE_AUDIO_URL = "https://ziloobucket.s3.ap-south-1.amazonaws.com";

    public static final String privacy_policy = "https://www.freeprivacypolicy.com/live/63ea2c65-c86a-408a-b12a-35afeec613dd";
    public static final String terms_conditions = "https://www.freeprivacypolicy.com/live/63ea2c65-c86a-408a-b12a-35afeec613dd";

    // if you want a user can't share a video from your app then you have to set this value to true
    public static final boolean IS_SECURE_INFO = true;

    // if you want a ads did not show into your app then set the value to true.
    public static final boolean IS_REMOVE_ADS = true;

    // if you show the ad on after every specific video count
    public static final int SHOW_AD_ON_EVERY = 8;

    // if you want a video thumnail image show rather then a video gif then set the below value to false.
    public static final boolean IS_SHOW_GIF = true;

    // if you want to disbale all the toasts in the app
    public static final boolean IS_TOAST_ENABLE = false;

    // if you want to add a limit that a user can watch only 6 video then change the below value to true
    // if you want to change the demo videos limit count then set the count as you want
    public static final boolean IS_DEMO_APP = false;
    public static final int DEMO_APP_VIDEOS_COUNT = 6;
    //video description char limit during posting the video
    public final static int VIDEO_DESCRIPTION_CHAR_LIMIT = 150;
    // Username char limit during signup and edit the account
    public static final int USERNAME_CHAR_LIMIT = 30;
    // user profile bio char limit during edit the profile
    public static final int BIO_CHAR_LIMIT = 150;
    // if you want to add a time limit in live streaming then make this true
    public static final boolean STREAMING_LIMIT = false;
    // if you want to change the streaming time then change it for now it is 60 sec
    public static final int MAX_STREMING_TIME = 60000;
    // set the profile image max size for now it is 400 * 400
    public static final int PROFILE_IMAGE_SQUARE_SIZE = 300;
    // Make product ids of different prices on google play console and place that ids in it.
    public static final String COINS0 = "100";
    public static final String PRICE0 = "$1";
    public static final String Product_ID0 = "android.test.purchased";
    public static final String COINS1 = "500";
    public static final String PRICE1 = "$5";
    public static final String Product_ID1 = "com.qboxus.musictoktwo";
    public static final String COINS2 = "2000";
    public static final String PRICE2 = "$20";
    public static final String Product_ID2 = "com.qboxus.musictokthree";
    public static final String COINS3 = "5000";
    public static final String PRICE3 = "$50";
    public static final String Product_ID3 = "com.qboxus.musictokfour";
    public static final String COINS4 = "10000";
    public static final String PRICE4 = "$100";
    public static final String Product_ID4 = "com.qboxus.musictokfive";
    // maximum time to record the video for now it is 30 sec
    public static int MAX_RECORDING_DURATION = 30000;
    public static int RECORDING_DURATION = 30000;
    // minimum time of recode a video for now it is 5 sec
    public static int MIN_TIME_RECORDING = 5000;
    // minimum trim chunk time span of a video for now it is 5 sec
    public static int MIN_TRIM_TIME = 5;
    // maximum trim chunk time span of a video for now it is 30 sec
    public static int MAX_TRIM_TIME = 30;
    // The tag name you want to print all the log
    public static String tag = "ziloo_";

}
