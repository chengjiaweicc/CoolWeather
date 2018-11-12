package com.example.coolweather.gson;

public class AQI {//当前空气质量的情况

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
