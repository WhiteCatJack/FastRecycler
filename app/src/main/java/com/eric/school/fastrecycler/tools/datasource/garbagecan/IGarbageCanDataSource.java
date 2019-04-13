package com.eric.school.fastrecycler.tools.datasource.garbagecan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * 垃圾桶数据池.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:00
 */
interface IGarbageCanDataSource {

    @Nullable
    @WorkerThread
    RecyclerPlace getRecyclerPlace() throws BmobException;

    @NonNull
    @WorkerThread
    List<GarbageCan> getGarbageCanList(@Nullable RecyclerPlace recyclerPlace) throws BmobException;
}
