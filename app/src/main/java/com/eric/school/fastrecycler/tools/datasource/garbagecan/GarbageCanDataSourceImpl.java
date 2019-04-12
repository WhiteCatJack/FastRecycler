package com.eric.school.fastrecycler.tools.datasource.garbagecan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.user.UserEngine;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;
import com.eric.school.fastrecycler.tools.util.BmobUtils;

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

    @NonNull
    @Override
    public BmobUtils.BmobSyncObject<RecyclerPlace> getRecyclerPlace() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final BmobUtils.BmobSyncObject<RecyclerPlace> syncObject = new BmobUtils.BmobSyncObject<>();
        BmobQuery<RecyclerPlace> recyclerPlaceBmobQuery = new BmobQuery<>();
        recyclerPlaceBmobQuery.addWhereEqualTo("recycler", UserEngine.getInstance().getCurrentUser().getObjectId());
        recyclerPlaceBmobQuery.findObjects(new FindListener<RecyclerPlace>() {
            @Override
            public void done(List<RecyclerPlace> list, BmobException e) {
                RecyclerPlace data = null;
                if (e == null && list != null && list.size() > 0) {
                    data = list.get(0);
                }
                syncObject.setUp(data != null, data, e);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        return syncObject;
    }

    @NonNull
    @Override
    public BmobUtils.BmobSyncObject<List<GarbageCan>> getGarbageCanList(@Nullable RecyclerPlace recyclerPlace) {
        final BmobUtils.BmobSyncObject<List<GarbageCan>> syncObject = new BmobUtils.BmobSyncObject<>();

        BmobUtils.BmobSyncObject<RecyclerPlace> recyclerPlaceBmobSyncObject = null;
        // 如果没有上级传递数据，则尝试远程获取
        if (recyclerPlace == null) {
            recyclerPlaceBmobSyncObject = getRecyclerPlace();
            // 如果执行了远程获取，并且失败
            if (!recyclerPlaceBmobSyncObject.isSuccess()){
                syncObject.setUp(false, null, recyclerPlaceBmobSyncObject.getException());
                return syncObject;
            } else {
                recyclerPlace = recyclerPlaceBmobSyncObject.getData();
            }
        }
        // 此时recyclerPlace对象一定非空
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        BmobQuery<GarbageCan> garbageCanBmobQuery = new BmobQuery<>();
        garbageCanBmobQuery.addWhereEqualTo("areaCode", recyclerPlace.getAreaCode());
        garbageCanBmobQuery.addWhereEqualTo("blockCode", recyclerPlace.getBlockCode());
        garbageCanBmobQuery.findObjects(new FindListener<GarbageCan>() {
            @Override
            public void done(List<GarbageCan> list, BmobException e) {
                syncObject.setUp(true, list, e);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        return syncObject;
    }
}
