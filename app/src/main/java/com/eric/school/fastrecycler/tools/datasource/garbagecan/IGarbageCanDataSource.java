package com.eric.school.fastrecycler.tools.datasource.garbagecan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.util.BmobUtils;

import java.util.List;

/**
 * 垃圾桶数据池.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:00
 */
interface IGarbageCanDataSource {

    /**
     * 同步方式调用BmobApi获取回收站数据
     */
    @NonNull
    @WorkerThread
    BmobUtils.BmobSyncObject<RecyclerPlace> getRecyclerPlace();

    /**
     * 同步方式调用BmobApi获取所有垃圾桶数据
     */
    @NonNull
    @WorkerThread
    BmobUtils.BmobSyncObject<List<GarbageCan>> getGarbageCanList(@Nullable RecyclerPlace recyclerPlace);
}
