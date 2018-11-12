package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {//当前的天气信息
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}

