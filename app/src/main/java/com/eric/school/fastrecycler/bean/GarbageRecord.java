package com.eric.school.fastrecycler.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 垃圾桶容量记录.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:05
 */
public class GarbageRecord extends BmobObject {
    private GarbageCan garbageCan;
    private BmobDate date;
    private double volumeChange;

    public GarbageCan getGarbageCan() {
        return garbageCan;
    }

    public void setGarbageCan(GarbageCan garbageCan) {
        this.garbageCan = garbageCan;
    }

    public BmobDate getDate() {
        return date;
    }

    public void setDate(BmobDate date) {
        this.date = date;
    }

    public double getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(double volumeChange) {
        this.volumeChange = volumeChange;
    }
}
