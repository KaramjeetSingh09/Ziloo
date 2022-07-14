package com.zeroitsolutions.ziloo.ApiClasses;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.Models.UploadVideoModel;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploader {

    long filesize = 0l;
    UploadVideoModel uploadModel;
    private FileUploaderCallback mFileUploaderCallback;

    public FileUploader(File file, Context context, UploadVideoModel uploadModel) {
        this.uploadModel = uploadModel;
        filesize = file.length();

        InterfaceFileUpload interfaceFileUpload = ApiClient.getRetrofitInstance(context)
                .create(InterfaceFileUpload.class);

        PRRequestBody videoBody = new PRRequestBody(file);
//        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), file);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("video",
                file.getName(), videoBody);

        RequestBody PrivacyType = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getPrivacyPolicy());

        RequestBody UserId = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getUserId());

        RequestBody SoundId = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getSoundId());

        RequestBody AllowComments = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getAllowComments());

        RequestBody Description = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getDescription());

        RequestBody AllowDuet = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getAllowDuet());

        RequestBody UsersJson = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getUsersJson());

        RequestBody HashtagsJson = RequestBody.create(
                okhttp3.MultipartBody.FORM, uploadModel.getHashtagsJson());


        Call<UploadResponse> fileUpload;
        if (uploadModel.getVideoId().equalsIgnoreCase("0")) {
            RequestBody videoId = RequestBody.create(
                    okhttp3.MultipartBody.FORM, uploadModel.getVideoId());

            fileUpload = interfaceFileUpload.UploadFile(fileToUpload, PrivacyType, UserId,
                    SoundId, AllowComments, Description, AllowDuet, UsersJson, HashtagsJson, videoId);
        } else {
            RequestBody videoId = RequestBody.create(
                    okhttp3.MultipartBody.FORM, uploadModel.getVideoId());
            RequestBody duet = RequestBody.create(
                    okhttp3.MultipartBody.FORM, uploadModel.getDuet());

            fileUpload = interfaceFileUpload.UploadFile(fileToUpload, PrivacyType, UserId,
                    SoundId, AllowComments, Description, AllowDuet, UsersJson, HashtagsJson, videoId, duet);
        }

        Log.d("TestApp_", "************************  before call : " +
                fileUpload.request().url());

        fileUpload.enqueue(new Callback<UploadResponse>() {

            @Override
            public void onResponse(@NonNull Call<UploadResponse> call,
                                   @NonNull Response<UploadResponse> response) {

                if (response != null && response.code() == 200) {
                    if (response.body().getCode().equalsIgnoreCase("200")) {
                        mFileUploaderCallback.onFinish(response.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                mFileUploaderCallback.onError();
            }
        });
    }

    public void SetCallBack(FileUploaderCallback fileUploaderCallback) {
        this.mFileUploaderCallback = fileUploaderCallback;
    }


    public interface FileUploaderCallback {

        void onError();

        void onFinish(String responses);

        void onProgressUpdate(int currentpercent, int totalpercent, String msg);
    }

    public class PRRequestBody extends RequestBody {
        private static final int DEFAULT_BUFFER_SIZE = 1024;
        private File mFile;

        public PRRequestBody(final File file) {
            mFile = file;
        }

        @Override
        public MediaType contentType() {
            // i want to upload only images
//            return MediaType.parse("multipart/form-data");
            return MediaType.parse("video/*");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            Log.d(Constants.tag, "Check progress callback");

            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;
//            Source source = null;

            try {
                int read;
//                source = Okio.source(mFile);
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
//                    Log.d(Constants.tag, String.valueOf(uploaded));
                }
            } catch (Exception e) {
                Log.d(Constants.tag, "Exception : " + e);
            } finally {
                in.close();
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded = 0;
        private long mTotal = 0;

        ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int) (100 * mUploaded / mTotal);
            Log.d("video", " " + current_percent);
            int total_percent = (int) (100 * (mUploaded) / mTotal);
            mFileUploaderCallback.onProgressUpdate(current_percent, total_percent,
                    "File Size: " + Functions.readableFileSize(filesize));
        }
    }
}