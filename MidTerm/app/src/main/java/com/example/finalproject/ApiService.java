package com.example.finalproject;

import com.example.finalproject.Model.Token;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @FormUrlEncoded
    @POST("/auth/realms/master/protocol/openid-connect/token")
    Call<Token> getToken(@Field("client_id") String client_id,
                         @Field("username") String username,
                         @Field("password") String password,
                         @Field("grant_type") String grant_type);
}
