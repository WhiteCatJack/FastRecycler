package com.eric.school.fastrecycler;

import cn.bmob.v3.BmobObject;

/**
 * 垃圾桶信息.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:05
 */
public class GarbageCan extends BmobObject {
    private int province;
    private int city;
    private int district;
    private int region;
    private int number;
    private double longitude;
    private double latitude;

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
