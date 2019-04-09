package com.eric.school.fastrecycler.bean;

import cn.bmob.v3.BmobObject;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/8 0008 20:29
 */
public class RecyclerPlace extends BmobObject {
    private FRUser recycler;
    private double latitude;
    private double longitude;
    private String areaCode;
    private String blockCode;

    public FRUser getRecycler() {
        return recycler;
    }

    public void setRecycler(FRUser recycler) {
        this.recycler = recycler;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

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
}
