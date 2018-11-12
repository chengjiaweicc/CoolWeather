package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//全国所有所有省市县的数据都是从服务器端获取到的，所以需要和服务器进行交互
public class HttpUtil {//和服务器进行交互
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback) {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}

