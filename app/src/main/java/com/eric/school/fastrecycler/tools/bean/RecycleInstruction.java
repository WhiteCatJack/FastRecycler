package com.eric.school.fastrecycler.tools.bean;

import com.eric.school.fastrecycler.tools.bmobsync.SyncBmobObject;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/8 0008 20:29
 */
public class RecycleInstruction extends SyncBmobObject {
    private FRUser targetUser;
    private GarbageCan garbageCan;
    private BmobDate startTime;

    public FRUser getTargetUser() {
        return targetUser;
    }

    public RecycleInstruction setTargetUser(FRUser targetUser) {
        this.targetUser = targetUser;
        return this;
    }

    public GarbageCan getGarbageCan() {
        return garbageCan;
    }

    public RecycleInstruction setGarbageCan(GarbageCan garbageCan) {
        this.garbageCan = garbageCan;
        return this;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public RecycleInstruction setStartTime(BmobDate startTime) {
        this.startTime = startTime;
        return this;
    }
}
