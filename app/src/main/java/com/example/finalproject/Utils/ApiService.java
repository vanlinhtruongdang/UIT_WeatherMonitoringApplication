package com.example.finalproject.Utils;


import com.example.finalproject.Model.Point;
import com.example.finalproject.Model.Token;
import com.example.finalproject.Model.User;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
public interface ApiService {
    @GET("auth/realms/master/protocol/openid-connect/auth")
    Call<ResponseBody> getAuthSession(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("redirect_uri") String redirectUri
    );
    @GET
    Call<ResponseBody> getPage(@Url String url);
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
    Call<Token> getTokenWithCookies(@Field("client_id") String client_id,
                                    @Field("username") String username,
                                    @Field("password") String password,
                                    @Field("grant_type") String grant_type);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> getTokenWithCookies(
        @Url String url,
        @Field("username") String username,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/auth/realms/master/protocol/openid-connect/token")
    Call<Token> getTokenWithKeycloak(
        @Field("client_id") String client_id,
        @Field("username") String username,
        @Field("password") String password,
        @Field("grant_type") String grant_type
    );

    @Headers({"Accept: application/json"})
    @POST("/api/master/asset/datapoint/5zI6XqkQVSfdgOrZ1MyWEf/attribute/{attributeName}")
    Call<List<Point>> GetChart(@Path("attributeName") String attributeName,
                               @Body RequestBody Message);
    @GET("/api/master/user/user")
    Call<User> getUser(@Header("Authorization") String token);

    @POST("/auth/realms/master/account/password")
    @FormUrlEncoded
    Call<ResponseBody> changePassword(
        @Field("stateChecker") String stateChecker,
        @Field("password") String password,
        @Field("password-new") String newPassword,
        @Field("password-confirm") String confirmPassword,
        @Field("login") String loginType // Save
    );
    @GET("/auth/realms/master/account/password")
    Call<ResponseBody> getPasswordPage();


    @POST("/auth/realms/master/account")
    @FormUrlEncoded
    Call<ResponseBody> changeAccount(
            @Field("stateChecker") String stateChecker,
            @Field("email") String password,
            @Field("firstName") String newPassword,
            @Field("lastName") String confirmPassword,
            @Field("submitAction") String submitType // Save
    );
    @GET("/auth/realms/master/account")
    Call<ResponseBody> getAccountPage();
}