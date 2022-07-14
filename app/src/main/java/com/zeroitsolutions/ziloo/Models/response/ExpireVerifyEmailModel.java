package com.zeroitsolutions.ziloo.Models.response;

import com.google.gson.annotations.SerializedName;

public class ExpireVerifyEmailModel {

    @SerializedName("code")
    public String code;
    @SerializedName("msg")
    public String msg;

}
