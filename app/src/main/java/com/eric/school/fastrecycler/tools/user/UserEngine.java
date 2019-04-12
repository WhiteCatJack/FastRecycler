package com.eric.school.fastrecycler.tools.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.tools.base.BaseActivity;
import com.eric.school.fastrecycler.tools.bean.FRUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/1 0001 16:20
 */
public class UserEngine implements UserInterface {

    private static volatile UserEngine sInstance;

    private UserEngine() {
    }

    public static UserEngine getInstance() {
        if (sInstance == null) {
            synchronized (UserEngine.class) {
                if (sInstance == null) {

                    sInstance = new UserEngine();
                }
            }
        }
        return sInstance;
    }

    /**
     * 通过{@link BaseActivity#onResume()}默认检查登录态，如果没有登录会强制跳转到登录页面
     */
    @NonNull
    @Override
    public FRUser getCurrentUser() {
        return BmobUser.getCurrentUser(FRUser.class);
    }

    @Override
    public boolean isSignedIn() {
        return getCurrentUser() != null;
    }

    @Override
    public void signIn(@NonNull FRUser user, SaveListener<FRUser> listener) {
        if (user == null || listener == null) {
            return;
        }
        user.login(listener);
    }

    @Override
    public void signUp(@NonNull FRUser user, SaveListener<FRUser> listener) {
        if (user == null || listener == null) {
            return;
        }
        user.signUp(listener);
    }

    @Override
    public void signOut() {
        if (getCurrentUser() != null) {
            BmobUser.logOut();
        }
    }
}
