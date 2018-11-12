package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {//City类 用于存放市的数据信息
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getCityName() {//市名称

        return cityName;
    }

    public void setCityName(String cityName) {

        this.cityName = cityName;
    }

    public int getCityCode() {//市代号

        return cityCode;
    }

    public void setCityCode(int cityCode) {

        this.cityCode = cityCode;
    }

    public int getProvinceId() {//当前市所属省的id值

        return provinceId;
    }

    public void setProvinceId(int proviceId) {

        this.provinceId = proviceId;
    }
}
