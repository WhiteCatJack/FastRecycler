package com.eric.school.fastrecycler.datasource;

import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.GarbageCan;

import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:23
 */
public abstract class GarbageCanDataSource {

    private static volatile GarbageCanDataSource sInstance;

    private GarbageCanDataSource() {
    }

    public static GarbageCanDataSource getInstance() {
        if (sInstance == null) {
            synchronized (GarbageCanDataSource.class) {
                if (sInstance == null) {
                    sInstance = new GarbageCanDataSourceImpl();
                }
            }
        }
        return sInstance;
    }

    public void getAll(@NonNull FindListener<GarbageCan> listener) {
    }
}
