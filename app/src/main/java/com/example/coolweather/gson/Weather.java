package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {//总的实力类来引用其余各个实体类
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")  //daily_forecast包含的是一个数组，使用集合来引用Forecast类
    public List<Forecast> forecastList;
}

