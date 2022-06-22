package com.zeroitsolutions.ziloo.Models.relatedvVdeoModel;

import com.google.gson.annotations.SerializedName;

public class Msg {

    @SerializedName("video")
    public Video video;
    @SerializedName("user")
    public User user;
    @SerializedName("sound")
    public Sound sound;

}
