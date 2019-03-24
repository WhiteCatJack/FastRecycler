package com.eric.school.fastrecycler.datasource;

import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.GarbageCan;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 17:23
 */
class GarbageCanDataSourceImpl extends GarbageCanDataSource {

    @Override
    void getAll(@NonNull FindListener<GarbageCan> listener) {
        if (listener == null) {
            return;
        }
        BmobQuery<GarbageCan> query = new BmobQuery<>();
        // ...
        query.findObjects(listener);
    }
}
