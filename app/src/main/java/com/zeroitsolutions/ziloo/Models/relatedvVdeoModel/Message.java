package com.zeroitsolutions.ziloo.Models.relatedvVdeoModel;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("User")
    public User user;
    @SerializedName("PushNotification")
    public PushNotification pushNotification;
    @SerializedName("PrivacySetting")
    public PrivacySetting privacySetting;

}
