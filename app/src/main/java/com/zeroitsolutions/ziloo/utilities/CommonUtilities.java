package com.zeroitsolutions.ziloo.utilities;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CommonUtilities {

    private static ProgressDialog progressDialog;

    public static void alertDialog(final Context context, final String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setCancelable(false).setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void alertDialogOpenSetting(final Context context, final String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setCancelable(false).setNegativeButton("OK", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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

    public static void colorstatusbar(Activity context) {
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            context.getWindow().setStatusBarColor(context.getResources().getColor(R.color.taskbar));
    }

    public static void hidestatusbar(Activity context) {
        context.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static CircularProgressDrawable getCirculardrawable(Context context) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }

    public static String getUtcFormattedDate(String dateString) {
        String date = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date value = formatter.parse(dateString);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy");
            date = dateFormatter.format(value);
            Log.v("Date-->", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getUtcFormattedDateEuro(String dateString, String outputFormat) {
//        2021-06-11T20:00:00+01:00
        String date = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date value = formatter.parse(dateString);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(outputFormat);
            date = dateFormatter.format(value);
            Log.v("Date-->", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String getDateTimeFormattedDate(String dateString) {
        String date = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date value = formatter.parse(dateString);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm");
            date = dateFormatter.format(value);
            Log.v("Date-->", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getUtcFormattedTime(String dateString) {
        String time = "";
        try {
/*
            2020-10-17T12:30:00+01:00
*/
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            Date value = formatter.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
            assert value != null;
            time = dateFormatter.format(Objects.requireNonNull(value));
            Log.v("Time-->", time);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getFormattedDefaultDate(String dateString) {
        String date = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date value = formatter.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy");
            date = dateFormatter.format(value);
            Log.v("Date-->", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getFormattedDefaultDateTime(String dateString) {
        String date = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date value = formatter.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
            date = dateFormatter.format(Objects.requireNonNull(value));
            Log.v("Date-->", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate(String dateString) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = formatter.parse(dateString);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getYears(String dateString) {
        String year = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date value = formatter.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
            year = dateFormatter.format(value);
            Log.v("Date-->", year);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return year;
    }

    public static String getFormattedTime(long time) {
        String currentTime = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTimeInMillis(time);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            currentTime = sdf.format(time);

            Log.v("Time-->", currentTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    public static String getLocalDate() {
        String date = "";
        try {
            Date myDate = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat inFormat = new SimpleDateFormat("EEEE , dd MMM yyyy");
            date = inFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;

    }

    public static void downloadFile(String URL) {
        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory().toString() + "/ProjectName";

            File file = new File(PATH);
            file.mkdirs();

            File outputFile = new File(file, "ProjectName.mp4");
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();

            byte[] buffer = new byte[4096];
            int len1 = 0;

            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }

            fos.close();
            is.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void checkForUpdates(final Context context) {
//
//        AppUpdateManager mAppUpdateManager = AppUpdateManagerFactory.create(context);
//        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    // For a flexible update, use AppUpdateType.FLEXIBLE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
//            ) {
//                showUpdateDialog(context);
//            }
//        });
//    }


//
//    public static void loadImageGlide(Context context, String url, ImageView imageView) {
//        if (url.contains(URLConfig.IMAGE_URL))
//            Glide.with(context).load(url).placeholder(R.drawable.gallery_place_holder).into(imageView);
//        else
//            Glide.with(context).load(URLConfig.IMAGE_URL + url).placeholder(R.drawable.gallery_place_holder).into(imageView);
//    }
//
//    public static void loadImageProfileGlide(Context context, String url, ImageView imageView) {
//        if (url.contains(URLConfig.PROFILE_URL))
//            Glide.with(context).load(url).placeholder(R.drawable.ic_person).into(imageView);
//        else
//            Glide.with(context).load(URLConfig.PROFILE_URL + url).placeholder(R.drawable.ic_person).into(imageView);
//    }

    public static void loadWebUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            context.startActivity(intent);
        }
    }
}