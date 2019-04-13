package com.eric.school.fastrecycler.tools.bmobsync;

import android.support.annotation.NonNull;


import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.disposables.Disposable;
/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 14:57
 */
public class SyncBmobQuery<T> extends BmobQuery<T> {

    @NonNull
    public List<T> syncFindObjects() throws BmobException {
        final SyncBmobResult<List<T>> syncBmobResult = new SyncBmobResult<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        findObjects(new FindListener<T>() {
            @Override
            public void done(List<T> list, BmobException e) {
                syncBmobResult.setUp(list, e);
                countDownLatch.countDown();
            }
        });
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
