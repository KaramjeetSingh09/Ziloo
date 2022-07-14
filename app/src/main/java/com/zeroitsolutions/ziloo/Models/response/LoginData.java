package com.zeroitsolutions.ziloo.Models.response;

import android.accounts.Account;

import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("phone")
    public String phone;

    @SerializedName("title")
    public String title;

    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public String id;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("image")
    public String image;

    @SerializedName("device_type")
    public String device_type;

    @SerializedName("auth_token")
    public String auth_token;

    @SerializedName("username")
    public String user_name;

    @SerializedName("date_of_birth")
    public String date_of_birth;

    @SerializedName("address")
    public String address;

    @SerializedName("language")
    public String language;

    @SerializedName("profile_image")
    public String profile_image;

    @SerializedName("email_verified_at")
    public Object email_verified_at;

    @SerializedName("country")
    public String country;

    @SerializedName("country_id")
    public String country_id;

    @SerializedName("status")
    public int status;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("mobile_number")
    public String mobile_number;

    @SerializedName("account")
    public Account account;
}