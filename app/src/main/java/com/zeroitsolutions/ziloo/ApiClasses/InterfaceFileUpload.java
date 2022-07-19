package com.zeroitsolutions.ziloo.ApiClasses;

import com.zeroitsolutions.ziloo.Models.relatedvVdeoModel.UploadImageResponse;
import com.zeroitsolutions.ziloo.Models.response.ExpireVerifyEmailModel;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface InterfaceFileUpload {

    @Multipart
    @POST(ApiLinks.postVideo)
    Call<UploadResponse> UploadFile(@Part MultipartBody.Part file,
                                    @Part("privacy_type") RequestBody PrivacyType,
                                    @Part("user_id") RequestBody UserId,
                                    @Part("sound_id") RequestBody SoundId,
                                    @Part("allow_comments") RequestBody AllowComments,
                                    @Part("description") RequestBody Description,
                                    @Part("allow_duet") RequestBody AllowDuet,
                                    @Part("users_json") RequestBody UsersJson,
                                    @Part("hashtags_json") RequestBody HashtagsJson,
                                    @Part("video_id") RequestBody videoId);

    @Multipart
    @POST(ApiLinks.postVideo)
    Call<UploadResponse> UploadFile(@Part MultipartBody.Part file,
                                    @Part("privacy_type") RequestBody PrivacyType,
                                    @Part("user_id") RequestBody UserId,
                                    @Part("sound_id") RequestBody SoundId,
                                    @Part("allow_comments") RequestBody AllowComments,
                                    @Part("description") RequestBody Description,
                                    @Part("allow_duet") RequestBody AllowDuet,
                                    @Part("users_json") RequestBody UsersJson,
                                    @Part("hashtags_json") RequestBody HashtagsJson,
                                    @Part("video_id") RequestBody videoId,
                                    @Part("duet") RequestBody duet);

    @POST(ApiLinks.addUserImage)
    @Multipart
    Call<UploadImageResponse> postUpdateProfile(@PartMap HashMap<String, RequestBody> hashMap);

    @POST(ApiLinks.expireVerifyEmailCode)
    Call<ExpireVerifyEmailModel> expireVerifyEmailCode(@Query("email") String email);

    @POST(ApiLinks.expirePhoneVerifyCode)
    Call<ExpireVerifyEmailModel> expirePhoneVerifyCode(@Query("phone") String phone);

}
