package com.eric.school.fastrecycler.tools.datasource.garbagecan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.bmobsync.SyncBmobQuery;
import com.eric.school.fastrecycler.tools.user.UserEngine;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;
import com.eric.school.fastrecycler.tools.util.BmobUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:01
 */
class GarbageCanDataSourceImpl extends GarbageCanDataSource {

    private static volatile GarbageCanDataSource sInstance;

    private GarbageCanDataSourceImpl() {
    }

    static GarbageCanDataSource getInstance() {
        if (sInstance == null) {
            synchronized (GarbageCanDataSourceImpl.class) {
                if (sInstance == null) {
                    sInstance = new GarbageCanDataSourceImpl();
                }
            }
        }
        return sInstance;
    }

    @Nullable
    @Override
    public RecyclerPlace getRecyclerPlace() throws BmobException {
        RecyclerPlace result = null;
        SyncBmobQuery<RecyclerPlace> recyclerPlaceBmobQuery = new SyncBmobQuery<>();
        recyclerPlaceBmobQuery.addWhereEqualTo("recycler", UserEngine.getInstance().getCurrentUser().getObjectId());
        List<RecyclerPlace> temp = recyclerPlaceBmobQuery.syncFindObjects();
        if (temp.size() != 1) throw new BmobException("Recycler place count illegal!");
        return temp.get(0);
    }

    @NonNull
    @Override
    public List<GarbageCan> getGarbageCanList(@Nullable RecyclerPlace recyclerPlace) throws BmobException {
        List<GarbageCan> result = new ArrayList<>();
        // 如果没有上级传递数据，则尝试远程获取
        if (recyclerPlace == null) {
            recyclerPlace = getRecyclerPlace();
            // 如果执行了远程获取，并且失败
            if (recyclerPlace == null) {
                // 此时返回的结果size为0
                return result;
            }
        }
        // 此时recyclerPlace对象一定非空
        SyncBmobQuery<GarbageCan> garbageCanBmobQuery = new SyncBmobQuery<>();
        garbageCanBmobQuery.addWhereEqualTo("areaCode", recyclerPlace.getAreaCode())
                .addWhereEqualTo("blockCode", recyclerPlace.getBlockCode());
        return garbageCanBmobQuery.syncFindObjects();
    }
}
