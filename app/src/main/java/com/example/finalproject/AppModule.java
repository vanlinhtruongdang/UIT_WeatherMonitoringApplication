package com.example.finalproject;

import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.example.finalproject.Utils.MyWebSocketClient;
import com.tencent.mmkv.MMKV;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public static CustomCookieJar provideCustomCookieJar() {
        return new CustomCookieJar();
    }

    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(
        final CustomCookieJar cookieJar,
        final HttpLoggingInterceptor logging
    ) {
        return new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(chain -> {
                    var host = chain.request().url().host();
                    var request = chain.request();

                    if (!host.equals("uiot.ixxc.dev") || request.header("Authorization") != null) {
                        return chain.proceed(request);
                    }

                    final var accessToken = MMKV.defaultMMKV().getString("AccessToken", null);

                    if (accessToken != null) {
                        Timber.d("Authenticator called with accessToken: %s", accessToken);
                        return chain.proceed(
                                request.newBuilder()
                                    .header("Authorization", String.format("Bearer %s", accessToken))
                                    .build()
                        );
                    }

                    return chain.proceed(request);
                })
                .addInterceptor(logging)
                .build();
    }
    @Provides
    @Singleton
    public static Retrofit provideRetrofit(final OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public static ApiService provideApiService(final Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public static MyWebSocketClient provideWebSocket() {
        return new MyWebSocketClient();
    }
}
