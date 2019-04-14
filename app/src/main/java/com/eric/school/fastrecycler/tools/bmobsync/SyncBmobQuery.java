package com.eric.school.fastrecycler.tools.bmobsync;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 14:57
 */
public class SyncBmobQuery<T> extends BmobQuery<T> {

    private Class<T> clazz;

    public SyncBmobQuery(Class<T> clazz) {
        this.clazz = clazz;
    }

    @NonNull
    public List<T> syncFindObjects() throws BmobException {
        final SyncBmobResult<List<T>> syncBmobResult = new SyncBmobResult<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        FindListener<T> findListener = SyncBmobQueryFindListenerFactory.getFindListener(clazz, new FindListener<T>() {
            @Override
            public void done(List<T> list, BmobException e) {
                syncBmobResult.setUp(list, e);
                countDownLatch.countDown();
            }
        });
        if (findListener == null){
            throw new IllegalStateException("Must register method in SyncBmobQueryFindListenerFactory for new BmobBean!");
        }
        findObjects(findListener);
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        if (syncBmobResult.data == null) {
            if (syncBmobResult.exception == null) {
                syncBmobResult.setUp(null, new BmobException("Unknown onError!"));
            }
            throw syncBmobResult.exception;
        }
        return syncBmobResult.data;
    }
}
