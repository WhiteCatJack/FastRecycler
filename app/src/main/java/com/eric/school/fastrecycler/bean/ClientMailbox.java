package com.eric.school.fastrecycler.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/10 0010 19:42
 */
public class ClientMailbox extends BmobObject {
    private FRUser user;
    private boolean valid = true;
    private BmobDate startTime;
    private BmobDate endTime;

    public FRUser getUser() {
        return user;
    }

    public void setUser(FRUser user) {
        this.user = user;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public void setStartTime(BmobDate startTime) {
        this.startTime = startTime;
    }

    public BmobDate getEndTime() {
        return endTime;
    }

    public void setEndTime(BmobDate endTime) {
        this.endTime = endTime;
    }
}
