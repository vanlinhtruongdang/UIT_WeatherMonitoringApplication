package com.example.finalproject.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import timber.log.Timber;

public class CustomCookieJar implements CookieJar {
    private final Map<String, Map<String, Cookie>> cookieMap = new HashMap<>();

    public void clear() {
        cookieMap.clear();
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        Timber.d(cookies.toString());

        String host = url.host();
        var currentCookies = cookieMap.getOrDefault(host, new HashMap<>());
        cookies.forEach(c -> currentCookies.put(c.name(), c));
        cookieMap.put(host, currentCookies);
        Timber.d(cookieMap.toString());
    }
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String host = url.host();
        var cookies = cookieMap.getOrDefault(host, new HashMap<>());
        Timber.d(cookieMap.toString());
        return cookies.values().stream().collect(Collectors.toList());
    }
}