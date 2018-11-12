package com.example.coolweather.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {//由于服务器返回的省市县数据都是JSON格式的，Utility类就是用来解析和处理这种数据

    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try{
                //使用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，再调用save()方法将数据存储到数据库中
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));//组装成实体类对象
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//将数据存储到数据库当中
                }
                return  true;
            }catch (JSONException e) {//当try中语句出现异常时，就执行catch中的语句
                e.printStackTrace();//打印异常信息在程序中出错的位置及原因
            }
        }return  false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try{
                //使用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，再调用save()方法将数据存储到数据库中
                JSONArray allCities=new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));//组装成实体类对象
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();//将数据存储到数据库中
                }
                return  true;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }return  false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try{
                //使用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，再调用save()方法将数据存储到数据库中
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));//组装成实体类对象
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return  true;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }return  false;
    }

    /**
     *将返回的JSON数据解析成Weather实体类
     */
    @Nullable
    public static Weather handleWeatherResponse(String response) {
        try {
            //通过JSONObject和JSONArray将天气数据中的主体内容解析出来
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);//调用fromJson()方法就能直接将JSON数据转换成Weather对象了
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

