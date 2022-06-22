package com.zeroitsolutions.ziloo.SimpleClasses;


import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.danikula.videocache.BuildConfig;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.googlecode.mp4parser.authoring.Track;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.zeroitsolutions.ziloo.Accounts.LoginA;
import com.zeroitsolutions.ziloo.ActivitesFragment.LiveStreaming.CallBack;
import com.zeroitsolutions.ziloo.ActivitesFragment.SendGift.StickerModel;
import com.zeroitsolutions.ziloo.ApiClasses.ApiLinks;
import com.zeroitsolutions.ziloo.ApiClasses.ApiVolleyRequest;
import com.zeroitsolutions.ziloo.ApiClasses.InterfaceApiResponse;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Interfaces.FragmentCallBack;
import com.zeroitsolutions.ziloo.Interfaces.InternetCheckCallback;
import com.zeroitsolutions.ziloo.MainMenu.MainMenuActivity;
import com.zeroitsolutions.ziloo.Models.CommentModel;
import com.zeroitsolutions.ziloo.Models.HomeModel;
import com.zeroitsolutions.ziloo.Models.MultipleAccountModel;
import com.zeroitsolutions.ziloo.Models.PrivacyPolicySettingModel;
import com.zeroitsolutions.ziloo.Models.PushNotificationSettingModel;
import com.zeroitsolutions.ziloo.Models.UserModel;
import com.zeroitsolutions.ziloo.R;
import com.zeroitsolutions.ziloo.activities.SplashA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

/**
 * Created by qboxus on 2/20/2019.
 */

public class Functions {
    // initialize the loader dialog and show
    public static Dialog dialog;
    public static Dialog indeterminantDialog;
    public static Dialog determinant_dialog;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar determinant_progress;
    public static BroadcastReceiver broadcastReceiver;
    public static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

    // change the color of status bar into black
    public static void blackStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();

        int flags = view.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        view.setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.BLACK);
    }

    public static void PrintHashKey(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures") final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(Constants.tag, "KeyHash : " + hashKey);
            }
        } catch (Exception e) {
            Log.e(Constants.tag, "error:", e);
        }
    }

    // change the color of status bar into white
    public static void whiteStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        int flags = view.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        view.setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.WHITE);
    }

    // close the keybord
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // open the keyboard
    public static void showKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    // retun the sharepref instance
    public static SharedPreferences getSharedPreference(Context context) {
        if (Variable.sharedPreferences != null)
            return Variable.sharedPreferences;
        else {
            Variable.sharedPreferences = context.getSharedPreferences(Variable.PREF_NAME, Context.MODE_PRIVATE);
            return Variable.sharedPreferences;
        }

    }

    // print any kind of log
    public static void printLog(String title, String text) {
        if (!Constants.IS_SECURE_INFO) {
            if (title != null && text != null)
                Log.d(title, text);
        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // change string value to integer
    public static int parseInterger(String value) {
        if (value != null && !value.equals("")) {
            return Integer.parseInt(value);
        } else
            return 0;
    }

    // format the count value
    public static String getSuffix(String value) {
        try {

            if (value != null && (!value.equals("") && !value.equalsIgnoreCase("null"))) {
                long count = Long.parseLong(value);
                if (count < 1000)
                    return "" + count;
                int exp = (int) (Math.log(count) / Math.log(1000));
                return String.format(Locale.ENGLISH, "%.1f %c",
                        count / Math.pow(1000, exp),
                        "kMBTPE".charAt(exp - 1));
            } else {
                return "0";
            }
        } catch (Exception e) {
            return value;
        }

    }

    // return  the rundom string of given length
    public static String getRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String removeSpecialChar(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    // show loader of simple messages
    public static void showAlert(Context context, String title, String Message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss()).show();
    }

    // dialog for show loader for showing dialog with title and descriptions
    public static void showAlert(Context context, String title, String description, final CallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, id) -> {
            if (callBack != null)
                callBack.getResponse("alert", "OK");
        });
        builder.create();
        builder.show();

    }

    // dialog for show any kind of alert
    public static void showAlert(Context context, String title, String Message, String postivebtn, String negitivebtn, final Callback callback) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton(negitivebtn, (dialog, which) -> {
                    dialog.dismiss();
                    callback.onResponce("no");
                })
                .setPositiveButton(postivebtn, (dialog, which) -> {

                    dialog.dismiss();
                    callback.onResponce("yes");

                }).show();
    }

    public static void showDoubleButtonAlert(Context context, String title, String message, String negTitle, String posTitle, boolean isCancelable, FragmentCallBack callBack) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvtitle, tvMessage, tvPositive, tvNegative;
        tvtitle = dialog.findViewById(R.id.tvtitle);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvNegative = dialog.findViewById(R.id.tvNegative);
        tvPositive = dialog.findViewById(R.id.tvPositive);


        tvtitle.setText(title);
        tvMessage.setText(message);
        tvNegative.setText(negTitle);
        tvPositive.setText(posTitle);

        tvNegative.setOnClickListener(view -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isShow", false);
            callBack.onResponce(bundle);
        });
        tvPositive.setOnClickListener(view -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isShow", true);
            callBack.onResponce(bundle);
        });
        dialog.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void showLoader(Context context, boolean outside_touch, boolean cancleable) {
        try {

            if (dialog != null) {
                cancelLoader();
                dialog = null;
            }
            {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dialog_loading_view);
                dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_white_background));

                if (!outside_touch)
                    dialog.setCanceledOnTouchOutside(false);

                if (!cancleable)
                    dialog.setCancelable(false);

                dialog.show();
            }

        } catch (Exception e) {
            Log.d(Constants.tag, "Exception : " + e);
        }
    }

    public static void cancelLoader() {
        try {
            if (dialog != null || dialog.isShowing()) {
                dialog.cancel();
            }
        } catch (Exception e) {
            Log.d(Constants.tag, "Exception : " + e);
        }
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static List<List<StickerModel>> createChunksOfList(List<StickerModel> originalList,
                                                              int chunkSize) {
        List<List<StickerModel>> listOfChunks = new ArrayList<List<StickerModel>>();
        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize
                    + chunkSize));
        }
        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add(originalList.subList(originalList.size()
                    - originalList.size() % chunkSize, originalList.size()));
        }
        return listOfChunks;
    }

    // format the username
    public static String showUsername(String username) {
        if (username != null && username.contains("@"))
            return username;
        else
            return "@" + username;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    public static boolean checkTimeDiffernce(Calendar current_cal, String date) {
        try {


            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;


            Log.d(Constants.tag, "Tag : " + difference);

            if (difference < 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }


    }

    public static String changeDateTodayYesterday(Context context, String date) {
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }


            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            if (difference < 86400) {
                if (current_cal.get(Calendar.DAY_OF_YEAR) - date_cal.get(Calendar.DAY_OF_YEAR) == 0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    return sdf.format(d);
                } else
                    return context.getString(R.string.yesterday);
            } else if (difference < 172800) {
                return context.getString(R.string.yesterday);
            } else
                return (difference / 86400) + context.getString(R.string.day_ago);

        } catch (Exception e) {
            return date;
        }


    }

    public static String bitmapToBase64(Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static Bitmap base64ToBitmap(String base_64) {
        Bitmap decodedByte = null;
        try {

            byte[] decodedString = Base64.decode(base_64, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {

        }
        return decodedByte;
    }

    public static boolean isShowContentPrivacy(Context context, String string_case, boolean isFriend) {
        if (string_case == null)
            return true;
        else {
            string_case = stringParseFromServerRestriction(string_case);

            if (string_case.equalsIgnoreCase("Everyone")) {
                return true;
            } else if (string_case.equalsIgnoreCase("Friends") &&
                    Functions.getSharedPreference(context).getBoolean(Variable.IS_LOGIN, false) && isFriend) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String stringParseFromServerRestriction(String res_string) {
        res_string = res_string.toUpperCase();
        res_string = res_string.replace("_", " ");
        return res_string;
    }

    public static String stringParseIntoServerRestriction(String res_string) {
        res_string = res_string.toLowerCase();
        res_string = res_string.replace(" ", "_");
        return res_string;
    }

    public static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }


    // Bottom is all the Apis which is mostly used in app we have add it
    // just one time and whenever we need it we will call it

    // make the directory on specific path
    public static void makeDirectry(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // return the random string of 10 char
    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static long getFileDuration(Context context, Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            return Functions.parseInterger(durationStr);
        } catch (Exception e) {

        }
        return 0;
    }

    // getCurrent Date
    public static String getCurrentDate(String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Calendar date = Calendar.getInstance();
        return format.format(date.getTime());
    }

    public static String getAppFolder(Context activity) {
        return activity.getExternalFilesDir(null).getPath() + "/";
    }

    public static void callApiForLikeVideo(final Activity activity,
                                           String video_id, String action,
                                           final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, "0"));
            parameters.put("video_id", video_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.likeVideo, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {
                Functions.checkStatus(activity, response);

                if (api_callBack != null)
                    api_callBack.onSuccess(response);
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    // this method will like the comment
    public static void callApiForLikeComment(final Activity activity,
                                             String video_id,
                                             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, "0"));
            parameters.put("comment_id", video_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.likeComment, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    // this method will like the reply comment
    public static void callApiForLikeCommentReply(final Activity activity,
                                                  String comment_reply_id,
                                                  final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, "0"));
            parameters.put("comment_reply_id", comment_reply_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.likeCommentReply, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);

                Functions.printLog(Constants.tag, "resp at like comment reply : " + resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    public static void callApiForSendComment(final Activity activity, String videoId, String comment, final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, "0"));
            parameters.put("video_id", videoId);
            parameters.put("comment", comment);

        } catch (Exception e) {
            e.printStackTrace();
        }


        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.postCommentOnVideo, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);

                ArrayList<CommentModel> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONObject msg = response.optJSONObject("msg");
                        JSONObject videoComment = msg.optJSONObject("VideoComment");

                        UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        CommentModel item = new CommentModel();

                        item.fb_id = userDetailModel.getId();
                        item.user_name = userDetailModel.getUsername();
                        item.first_name = userDetailModel.getFirstName();
                        item.last_name = userDetailModel.getLastName();
                        item.profile_pic = userDetailModel.getProfilePic();

                        item.video_id = videoComment.optString("video_id");
                        item.comments = videoComment.optString("comment");
                        item.created = videoComment.optString("created");

                        arrayList.add(item);

                        api_callBack.arrayData(arrayList);

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {

            }
        });

    }

    // this method will send the reply to the comment of the video
    public static void callApiForSendCommentReply(final Activity activity, String commentId, String comment, final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("comment_id", commentId);
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, "0"));
            parameters.put("comment", comment);

            Functions.printLog(Constants.tag, "parameters at reply : " + parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.postCommentReply, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);
                Functions.printLog(Constants.tag, "resp at reply : " + resp);

                ArrayList<CommentModel> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONObject msg = response.optJSONObject("msg");
                        assert msg != null;
                        JSONObject videoComment = msg.optJSONObject("VideoComment");
                        JSONObject videoCommentReply = msg.optJSONObject("VideoCommentReply");
                        UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        CommentModel item = new CommentModel();

                        item.fb_id = userDetailModel.getId();
                        item.first_name = userDetailModel.getFirstName();
                        item.last_name = userDetailModel.getLastName();
                        item.replay_user_name = userDetailModel.getUsername();
                        item.replay_user_url = userDetailModel.getProfilePic();

                        assert videoComment != null;
                        item.video_id = videoComment.optString("video_id");
                        item.comments = videoComment.optString("comment");
                        item.created = videoComment.optString("created");


                        item.comment_reply_id = videoCommentReply.optString("id");
                        item.comment_reply = videoCommentReply.optString("comment");
                        item.parent_comment_id = videoCommentReply.optString("comment_id");
                        item.reply_create_date = videoCommentReply.optString("created");
                        item.reply_liked_count = "0";
                        item.comment_reply_liked = "0";

                        arrayList.add(item);
                        item.item_count_replies = "1";
                        api_callBack.arrayData(arrayList);

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    public static void callApiForUpdateView(final Activity activity,
                                            String video_id) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("device_id", Functions.getSharedPreference(activity).getString(Variable.DEVICE_ID, "0"));
            parameters.put("video_id", video_id);
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.watchVideo, parameters, Functions.getHeaders(activity), null);

    }

    public static void callApiForFollowUnFollow
            (final Activity activity,
             String fbId,
             String followedFbId,
             final APICallBack api_callBack) {

        Functions.showLoader(activity, false, false);


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("sender_id", fbId);
            parameters.put("receiver_id", followedFbId);


        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.followUser, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.onSuccess(response.toString());

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {

            }
        });

    }

    public static void callApiForGetUserData
            (final Activity activity,
             String fbId,
             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", fbId);
            if (Functions.getSharedPreference(activity).getBoolean(Variable.IS_LOGIN, false) && fbId != null) {
                parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variable.U_ID, ""));
                parameters.put("other_user_id", fbId);
            } else if (fbId != null) {
                parameters.put("user_id", fbId);
            } else {
                parameters.put("username", fbId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.printLog("resp", parameters.toString());

       ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.showUserDetail, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
           @Override
           public void onResponse(String resp) {
               Functions.checkStatus(activity, resp);
               Functions.cancelLoader();
               try {
                   JSONObject response = new JSONObject(resp);
                   String code = response.optString("code");
                   if (code.equals("200")) {
                       api_callBack.onSuccess(response.toString());

                   } else {
                       Functions.showToast(activity, "" + response.optString("msg"));
                   }

               } catch (Exception e) {
                   api_callBack.onFail(e.toString());
                   e.printStackTrace();
               }
           }

           @Override
           public void onError(String response) {
               Functions.cancelLoader();
           }
        });
    }

    public static void callApiForDeleteVideo
            (final Activity activity,
             String videoId,
             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", videoId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(activity, ApiLinks.deleteVideo, parameters, Functions.getHeaders(activity), new InterfaceApiResponse() {
            @Override
            public void onResponse(String resp) {
                Functions.checkStatus(activity, resp);
                Functions.cancelLoader();

                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        if (api_callBack != null)
                            api_callBack.onSuccess(response.toString());

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    if (api_callBack != null)
                        api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String response) {

            }
        });
    }

    public static HomeModel parseVideoData(JSONObject userObj, JSONObject sound, JSONObject video, JSONObject userPrivacy, JSONObject userPushNotification) {
        HomeModel item = new HomeModel();

        UserModel userDetailModel = DataParsing.getUserDataModel(userObj);
        if (!(TextUtils.isEmpty(userDetailModel.getId()))) {
            item.user_id = userDetailModel.getId();
            item.username = userDetailModel.getUsername();
            item.first_name = userDetailModel.getFirstName();
            item.last_name = userDetailModel.getLastName();
            item.profile_pic = userDetailModel.getProfilePic();

            item.verified = userDetailModel.getVerified();
            item.follow_status_button = userDetailModel.getButton();
        }

        if (sound != null) {
            item.sound_id = sound.optString("id");
            item.sound_name = sound.optString("name");
            item.sound_pic = sound.optString("thum");

            item.sound_url_mp3 = sound.optString("audio");
            item.sound_url_acc = sound.optString("audio");

            if (!item.sound_url_mp3.contains(Variable.http)) {
                item.sound_url_mp3 = Constants.BASE_MEDIA_URL + item.sound_url_mp3;
            }
            if (!item.sound_url_acc.contains(Variable.http)) {
                item.sound_url_acc = Constants.BASE_MEDIA_URL + item.sound_url_acc;
            }
        }

        if (video != null) {

            item.like_count = "0" + video.optInt("like_count");
            item.video_comment_count = video.optString("comment_count");


            item.privacy_type = video.optString("privacy_type");
            item.allow_comments = video.optString("allow_comments");
            item.allow_duet = video.optString("allow_duet");
            item.video_id = video.optString("id");
            item.liked = video.optString("like");
            item.favourite = video.optString("favourite");

            item.views = video.optString("view");

            item.video_description = video.optString("description");
            item.favourite = video.optString("favourite");
            item.created_date = video.optString("created");

            item.thum = video.optString("thum");
            item.gif = video.optString("gif");
            item.video_url = video.optString("video", "");

            if (!item.video_url.contains(Variable.http)) {
                item.video_url = Constants.BASE_MEDIA_URL + item.video_url;
            }

            if (Ziloo.appLevelContext != null) {
                HttpProxyCacheServer proxy = Ziloo.getProxy(Ziloo.appLevelContext);
                item.video_url = proxy.getProxyUrl(item.video_url);
            }

            if (!item.thum.contains(Variable.http))
                item.thum = Constants.BASE_MEDIA_URL + item.thum;

            if (!item.gif.contains(Variable.http))
                item.gif = Constants.BASE_MEDIA_URL + item.gif;

            item.allow_duet = video.optString("allow_duet");
            item.duet_video_id = video.optString("duet_video_id");
            if (video.has("duet")) {
                JSONObject duet = video.optJSONObject("duet");
                if (duet != null) {
                    UserModel userDetailModelDuet = DataParsing.getUserDataModel(duet.optJSONObject("User"));
                    if (!(TextUtils.isEmpty(userDetailModelDuet.getId())))
                        item.duet_username = userDetailModelDuet.getUsername();
                }
            }

            item.promote = video.optString("promote");
            item.promotion_id = video.optString("promotion_id");
            JSONObject Promotion = video.optJSONObject("Promotion");
            if (Promotion != null) {
                item.destination = Promotion.optString("destination");
                item.website_url = Promotion.optString("website_url");
            }


            item.promote_button = video.optString("promote_button");


        }


        if (userPrivacy != null) {
            item.apply_privacy_model = new PrivacyPolicySettingModel();
            item.apply_privacy_model.setVideo_comment(userPrivacy.optString("video_comment"));
            item.apply_privacy_model.setLiked_videos(userPrivacy.optString("liked_videos"));
            item.apply_privacy_model.setDuet(userPrivacy.optString("duet"));
            item.apply_privacy_model.setDirect_message(userPrivacy.optString("direct_message"));
            item.apply_privacy_model.setVideos_download(userPrivacy.optString("videos_download"));
        }

        if (userPushNotification != null) {
            item.apply_push_notification_model = new PushNotificationSettingModel();
            item.apply_push_notification_model.setComments(userPushNotification.optString("comments"));
            item.apply_push_notification_model.setDirectmessage(userPushNotification.optString("direct_messages"));
            item.apply_push_notification_model.setLikes(userPushNotification.optString("likes"));
            item.apply_push_notification_model.setMentions(userPushNotification.optString("mentions"));
            item.apply_push_notification_model.setNewfollowers(userPushNotification.optString("new_followers"));
            item.apply_push_notification_model.setVideoupdates(userPushNotification.optString("video_updates"));
        }

        return item;

    }

    public static void showIndeterminentLoader(Context context, boolean outside_touch, boolean cancleable) {

        indeterminantDialog = new Dialog(context);
        indeterminantDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indeterminantDialog.setContentView(R.layout.item_indeterminant_progress_layout);
        indeterminantDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));


        if (!outside_touch)
            indeterminantDialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            indeterminantDialog.setCancelable(false);

        indeterminantDialog.show();

    }

    public static void cancelIndeterminentLoader() {
        if (indeterminantDialog != null) {
            indeterminantDialog.cancel();
        }
    }

    public static void showDeterminentLoader(Context context, boolean outside_touch, boolean cancleable) {

        determinant_dialog = new Dialog(context);
        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
        determinant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));

        determinant_progress = determinant_dialog.findViewById(R.id.pbar);

        if (!outside_touch)
            determinant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            determinant_dialog.setCancelable(false);

        determinant_dialog.show();

    }

    public static void showLoadingProgress(int progress) {
        if (determinant_progress != null) {
            determinant_progress.setProgress(progress);

        }
    }

    public static void cancelDeterminentLoader() {
        if (determinant_dialog != null) {
            determinant_progress = null;
            determinant_dialog.cancel();
        }
    }

    //store single account record
    public static void setUpMultipleAccount(Context context) {
        MultipleAccountModel accountModel = new MultipleAccountModel();
        accountModel.setId(Functions.getSharedPreference(context).getString(Variable.U_ID, "0"));
        accountModel.setfName(Functions.getSharedPreference(context).getString(Variable.F_NAME, ""));
        accountModel.setlName(Functions.getSharedPreference(context).getString(Variable.L_NAME, ""));
        accountModel.setuName(Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
        accountModel.setuBio(Functions.getSharedPreference(context).getString(Variable.U_BIO, ""));
        accountModel.setuLink(Functions.getSharedPreference(context).getString(Variable.U_LINK, ""));
        accountModel.setPhoneNo(Functions.getSharedPreference(context).getString(Variable.U_PHONE_NO, ""));
        accountModel.setEmail(Functions.getSharedPreference(context).getString(Variable.U_EMAIL, ""));
        accountModel.setSocialId(Functions.getSharedPreference(context).getString(Variable.U_SOCIAL_ID, ""));
        accountModel.setGender(Functions.getSharedPreference(context).getString(Variable.GENDER, ""));
        accountModel.setuPic(Functions.getSharedPreference(context).getString(Variable.U_PIC, ""));
        accountModel.setuWallet(Functions.getSharedPreference(context).getString(Variable.U_WALLET, "0"));
        accountModel.setuPayoutId(Functions.getSharedPreference(context).getString(Variable.U_PAYOUT_ID, ""));
        accountModel.setAuthToken(Functions.getSharedPreference(context).getString(Variable.AUTH_TOKEN, ""));
        accountModel.setVerified(Functions.getSharedPreference(context).getString(Variable.IS_VERIFIED, ""));
        accountModel.setApplyVerification(Functions.getSharedPreference(context).getString(Variable.IS_VERIFICATION_APPLY, ""));
        accountModel.setLogin(Functions.getSharedPreference(context).getBoolean(Variable.IS_LOGIN, false));

        Paper.book(Variable.MultiAccountKey).write(accountModel.getId(), accountModel);
    }

    //remove account signout
    public static void removeMultipleAccount(Context context) {
        Paper.book(Variable.MultiAccountKey).delete(Functions.getSharedPreference(context).getString(Variable.U_ID, "0"));
    }

    //store single account record
    public static void setUpNewSelectedAccount(Context context, MultipleAccountModel item) {

        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(Variable.U_ID, item.getId());
        editor.putString(Variable.F_NAME, item.getfName());
        editor.putString(Variable.L_NAME, item.getlName());
        editor.putString(Variable.U_NAME, item.getuName());
        editor.putString(Variable.U_BIO, item.getuBio());
        editor.putString(Variable.U_LINK, item.getuLink());
        editor.putString(Variable.U_PHONE_NO, item.getPhoneNo());
        editor.putString(Variable.U_EMAIL, item.getEmail());
        editor.putString(Variable.U_SOCIAL_ID, item.getSocialId());
        editor.putString(Variable.GENDER, item.getGender());
        editor.putString(Variable.U_PIC, item.getuPic());
        editor.putString(Variable.U_WALLET, item.getuWallet());
        editor.putString(Variable.U_PAYOUT_ID, item.getuPayoutId());
        editor.putString(Variable.AUTH_TOKEN, item.getAuthToken());
        editor.putString(Variable.IS_VERIFIED, item.getVerified());
        editor.putString(Variable.IS_VERIFICATION_APPLY, item.getApplyVerification());
        editor.putBoolean(Variable.IS_LOGIN, true);
        editor.apply();

        Intent intent = new Intent(context, SplashA.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    // use this method for lod muliple account in case one one account logout and other one can logout
    public static void setUpExistingAccountLogin(Context context) {
        if (!(Functions.getSharedPreference(context).getBoolean(Variable.IS_LOGIN, false))) {
            if (Paper.book(Variable.MultiAccountKey).getAllKeys().size() > 0) {
                MultipleAccountModel account = Paper.book(Variable.MultiAccountKey).read(Paper.book(Variable.MultiAccountKey).getAllKeys().get(0));
                setUpNewSelectedAccount(context, account);
            }
        }
    }

    public static void setUpSwitchOtherAccount(Context context, String userId) {
        for (String key : Paper.book(Variable.MultiAccountKey).getAllKeys()) {
            MultipleAccountModel account = Paper.book(Variable.MultiAccountKey).read(key);
            if (userId.equalsIgnoreCase(account.getId())) {
                setUpNewSelectedAccount(context, account);
                return;
            }

        }
    }

    //check login status
    public static boolean checkLoginUser(Activity context) {
        if (Functions.getSharedPreference(context)
                .getBoolean(Variable.IS_LOGIN, false)) {

            return true;
        } else {
            Intent intent = new Intent(context, LoginA.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            return false;
        }
    }

    // these function are remove the cache memory which is very helpfull in memmory managmet
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
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

    public static void copyFile(File sourceFile, File destFile) throws Exception {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    public static void showToast(Context context, String msg) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(String url, SimpleDraweeView simpleDrawee, boolean isGif) {
        if (!url.contains(Variable.http)) {
            url = Constants.BASE_MEDIA_URL + url;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();
        DraweeController controller;
        if (isGif) {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        } else {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }

        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Uri resourceUri, boolean isGif) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(resourceUri)
                .build();
        DraweeController controller;
        if (isGif) {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
        } else {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
        }


        return controller;
    }

    public static String getFollowButtonStatus(String button, Context context) {
        if (button.equalsIgnoreCase("following")) {
            return context.getString(R.string.following);
        } else if (button.equalsIgnoreCase("friends")) {
            return context.getString(R.string.friends_);
        } else if (button.equalsIgnoreCase("follow back")) {
            return context.getString(R.string.follow_back);
        } else {
            return context.getString(R.string.follow);
        }
    }

    public static boolean isNotificaitonShow(String userStatus) {
        if (userStatus.equalsIgnoreCase("following")) {
            return true;
        } else if (userStatus.equalsIgnoreCase("friends")) {
            return true;
        } else
            return userStatus.equalsIgnoreCase("follow back");
    }

    public static void addDeviceData(Activity context) {
        JSONObject headers = new JSONObject();
        try {
            headers.put("user_id", getSharedPreference(context).getString(Variable.U_ID, null));
            headers.put("device", "android");
            headers.put("version", BuildConfig.VERSION_NAME);
            headers.put("ip", getSharedPreference(context).getString(Variable.DEVICE_IP, null));
            headers.put("device_token", getSharedPreference(context).getString(Variable.DEVICE_TOKEN, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolleyRequest.JsonPostRequest(context, ApiLinks.addDeviceData, headers, Functions.getHeaders(context), new InterfaceApiResponse() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onError(String response) {

            }
        });
    }

    //    app language change
    public static void setLocale(String lang, Activity context, Class<?> className, boolean isRefresh) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        context.onConfigurationChanged(conf);
        if (isRefresh) {
            updateActivity(context, className);
        }

    }

    public static void updateActivity(Activity context, Class<?> className) {
        Intent intent = new Intent(context, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    // manage for store user data
    public static void storeUserLoginDataIntoDb(Context context, UserModel userDetailModel) {
        SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
        editor.putString(Variable.U_ID, userDetailModel.getId());
        editor.putString(Variable.F_NAME, userDetailModel.getFirstName());
        editor.putString(Variable.L_NAME, userDetailModel.getLastName());
        editor.putString(Variable.U_NAME, userDetailModel.getUsername());
        editor.putString(Variable.U_BIO, userDetailModel.getBio());
        editor.putString(Variable.U_LINK, userDetailModel.getWebsite());
        editor.putString(Variable.U_PHONE_NO, userDetailModel.getPhone());
        editor.putString(Variable.U_EMAIL, userDetailModel.getEmail());
        editor.putString(Variable.U_SOCIAL_ID, userDetailModel.getSocial_id());
        editor.putString(Variable.GENDER, userDetailModel.getGender());
        editor.putString(Variable.U_PIC, userDetailModel.getProfilePic());
        editor.putString(Variable.U_WALLET, "" + userDetailModel.getWallet());
        editor.putString(Variable.U_PAYOUT_ID, userDetailModel.getPaypal());
        editor.putString(Variable.AUTH_TOKEN, userDetailModel.getAuthToken());
        editor.putString(Variable.IS_VERIFIED, userDetailModel.getVerified());
        editor.putString(Variable.IS_VERIFICATION_APPLY, userDetailModel.getApplyVerification());
        editor.putBoolean(Variable.IS_LOGIN, true);
        editor.apply();
    }

    //use to get Directory Storage Used Capacity
    public static String getDirectorySize(String path) {

        File dir = new File(path);

        if (dir.exists()) {
            long bytes = getFolderSize(dir);
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String pre = ("KMGTPE").charAt(exp - 1) + "";
            return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }
        return "0";
    }

    private static long getFolderSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    result += getFolderSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }

    public static void unRegisterConnectivity(Context mContext) {
        try {
            if (broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void RegisterConnectivity(Context context, final InternetCheckCallback callback) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.GetResponse("alert", "connected");
                } else {
                    callback.GetResponse("alert", "disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static Boolean isConnectedToInternet(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(Constants.tag, "Exception : " + e.getMessage());
            return false;
        }
    }

    //check rational permission status
    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

    //show permission setting screen
    public static void showPermissionSetting(Context context, String message) {
        showDoubleButtonAlert(context, context.getString(R.string.permission_alert), message,
                context.getString(R.string.cancel_), context.getString(R.string.settings), false, bundle -> {
                    if (bundle.getBoolean("isShow", false)) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                });
    }

    //    check app is exist or not
    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return false;
    }

    public static File getBitmapToUri(Context inContext, Bitmap bitmap, String fileName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Functions.getAppFolder(inContext) + fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    // logout to app automatically when the login token expire
    public static void checkStatus(Activity activity, String responce) {
        try {
            JSONObject response = new JSONObject(responce);
            if (response.optString("code", "").equalsIgnoreCase("501")) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, gso);
                googleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                removeMultipleAccount(activity);
                SharedPreferences.Editor editor = getSharedPreference(activity).edit();
                Paper.book(Variable.PrivacySetting).destroy();
                editor.clear();
                editor.apply();
                activity.finish();

                setUpExistingAccountLogin(activity);
                activity.startActivity(new Intent(activity, MainMenuActivity.class));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getHeaders(Context context) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", Constants.API_KEY);
        headers.put("user_id", getSharedPreference(context).getString(Variable.U_ID, null));
        headers.put("auth_token", getSharedPreference(context).getString(Variable.AUTH_TOKEN, null));
        headers.put("device", "android");
        headers.put("version", BuildConfig.VERSION_NAME);
        headers.put("ip", getSharedPreference(context).getString(Variable.DEVICE_IP, null));
        headers.put("device_token", getSharedPreference(context).getString(Variable.DEVICE_TOKEN, null));
        printLog(Constants.tag, headers.toString());
        return headers;
    }

    public static HashMap<String, String> getHeadersWithOutLogin(Context context) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", Constants.API_KEY);
        headers.put("user_id", "");
        headers.put("auth_token", "");
        headers.put("device", "android");
        headers.put("version", BuildConfig.VERSION_NAME);
        headers.put("ip", getSharedPreference(context).getString(Variable.DEVICE_IP, ""));
        headers.put("device_token", getSharedPreference(context).getString(Variable.DEVICE_TOKEN, ""));
        printLog(Constants.tag, headers.toString());
        return headers;
    }

    public static void createNoMediaFile(Context context) {

        InputStream in = null;
        OutputStream out = null;

        try {

            //create output directory if it doesn't exist
            String path = getAppFolder(context) + "videoCache";
            Log.d(Constants.tag, "path: " + path);
            File dir = new File(path);
            Log.d(Constants.tag, "getAbsolutePath: " + dir.getAbsolutePath());
            File newFile = new File(dir, ".nomedia");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!newFile.exists()) {
                newFile.createNewFile();

                MediaScannerConnection.scanFile(context,
                        new String[]{path}, null,
                        (path1, uri) -> {
                            Log.i("ExternalStorage", "Scanned " + path1 + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });
            }
        } catch (Exception e) {
            Log.e(Constants.tag, "" + e);
        }
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


}