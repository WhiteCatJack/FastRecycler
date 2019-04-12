package com.eric.school.fastrecycler.tools.bean;

import cn.bmob.v3.BmobObject;

/**
 * 垃圾桶信息.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:05
 */
public class GarbageCan extends BmobObject {
    private String areaCode;
    private String blockCode;
    private int number;
    private double longitude;
    private double latitude;
    private double maxVolume;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
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

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }
}
