package com.zeroitsolutions.ziloo.Models.relatedvVdeoModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RelatedVideoModel {

    @SerializedName("code")
    public String code;

    @SerializedName("msg")
    public ArrayList<Msg> msg;

}
