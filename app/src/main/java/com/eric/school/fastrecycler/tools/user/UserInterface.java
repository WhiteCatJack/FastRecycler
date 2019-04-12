package com.eric.school.fastrecycler.tools.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.tools.bean.FRUser;

import cn.bmob.v3.listener.SaveListener;

/**
 * 用户操作.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 10:41
 */
interface UserInterface {

    /**
     * 检查登录态
     * 如果登陆态不正确，直接跳转至登录页面
     */
    boolean isSignedIn(@NonNull Context context);

    /**
     * 获取当前登录用户
     */
    FRUser getCurrentUser();

    /**
     * 登录
     */
    void signIn(@NonNull FRUser user, SaveListener<FRUser> listener);

    /**
     * 注册
     */
    void signUp(@NonNull FRUser user, SaveListener<FRUser> listener);

    /**
     * 登出
     */
    void signOut();
}
