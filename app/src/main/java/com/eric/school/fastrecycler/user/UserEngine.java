package com.eric.school.fastrecycler.user;

import android.support.annotation.Nullable;

import com.eric.school.fastrecycler.bean.FRUser;

import cn.bmob.v3.BmobUser;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/1 0001 16:20
 */
public class UserEngine {
    @Nullable
    public static FRUser getCurrentUser() {
        return BmobUser.getCurrentUser(FRUser.class);
    }
}
