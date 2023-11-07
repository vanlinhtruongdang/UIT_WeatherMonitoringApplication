package com.example.signup;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import retrofit2.http.FormUrlEncoded;

public interface ApiService {
    @GET("auth/realms/master/protocol/openid-connect/auth")
    Call<ResponseBody> getAuthUrl(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("redirect_uri") String redirectUri
    );
    @GET
    Call<ResponseBody> getRegisterPage(@Url String url);
    @POST
    @FormUrlEncoded
    Call<ResponseBody> signUp(
            @Url String url,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("password-confirm") String passwordConfirm,
            @Field("register") String register
    );
}