package com.example.finalproject.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    @Expose
    public String access_token;

    public String getAccess_token(){
        return access_token;
    }
    @SerializedName("expires_in")
    public int expires_in;
    @SerializedName("refresh_expires_in")
    public int refresh_expires_in;
    @SerializedName("refresh_token")
    public String refresh_token;
    @SerializedName("token_type")
    public String token_type;
    @SerializedName("not-before-policy")
    public int not_before_policy;
    @SerializedName("session_state")
    public String session_state;
    @SerializedName("scope")
    public String scope;
}