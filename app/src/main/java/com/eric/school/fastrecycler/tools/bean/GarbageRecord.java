package com.eric.school.fastrecycler.tools.bean;

import com.eric.school.fastrecycler.tools.bmobsync.SyncBmobObject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 垃圾桶容量记录.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:05
 */
public class GarbageRecord extends SyncBmobObject {
    private GarbageCan garbageCan;
    private BmobDate time;
    private double volumeChange;

    public GarbageCan getGarbageCan() {
        return garbageCan;
    }

    public void setGarbageCan(GarbageCan garbageCan) {
        this.garbageCan = garbageCan;
    }

    public BmobDate getTime() {
        return time;
    }

    public void setTime(BmobDate time) {
        this.time = time;
    }

    public double getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(double volumeChange) {
        this.volumeChange = volumeChange;
    }
}
