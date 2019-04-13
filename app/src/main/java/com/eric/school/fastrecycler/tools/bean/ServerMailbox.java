package com.eric.school.fastrecycler.tools.bean;

import com.eric.school.fastrecycler.tools.bmobsync.SyncBmobObject;

import cn.bmob.v3.BmobObject;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/10 0010 19:42
 */
public class ServerMailbox extends SyncBmobObject {
    private FRUser user;
    private boolean valid = true;
    private ClientMailbox mail;
    private String garbageCanList;

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

    public ClientMailbox getMail() {
        return mail;
    }

    public void setMail(ClientMailbox mail) {
        this.mail = mail;
    }

    public String getGarbageCanList() {
        return garbageCanList;
    }

    public void setGarbageCanList(String garbageCanList) {
        this.garbageCanList = garbageCanList;
    }
}
