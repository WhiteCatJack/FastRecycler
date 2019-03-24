package com.eric.school.fastrecycler;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * @author 泽乾
 * createAt 2019/3/22 0022 16:21
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initBmob();
    }

    private void initBmob() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId(Constants.BMOB_KEY)
                .setConnectTimeout(Constants.BMOB_NETWORK_LAG_TIME_LIMIT_IN_SECONDS)
                .build();
        Bmob.initialize(config);
    }
}
