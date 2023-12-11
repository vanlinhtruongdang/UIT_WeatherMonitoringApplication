package com.example.chart;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({"Accept: application/json"})
    @POST("/api/master/asset/datapoint/5zI6XqkQVSfdgOrZ1MyWEf/attribute/{attributeName}")
    Call<List<Point>> GetChart(@Header("Authorization") String token,
                        @Path("attributeName") String attributeName,
                        @Body RequestBody Message);

    @FormUrlEncoded
    @POST("/auth/realms/master/protocol/openid-connect/token")
    Call<ResponseBody> SignIn(@Field("client_id") String client_id,
                              @Field("username") String username,
                              @Field("password") String password,
                              @Field("grant_type") String grant_type);
}
