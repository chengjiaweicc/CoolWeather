package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {//Province类 用户存放省的数据信息
    private int id;
    private String provinceName;
    private int ProvinceCode;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getProvinceName() {//省名称

        return provinceName;
    }

    public void setProvinceName(String provinceName) {

        this.provinceName = provinceName;
    }

    public int getProvinceCode() {//省代号

        return ProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {

        this.ProvinceCode = provinceCode;
    }
}
