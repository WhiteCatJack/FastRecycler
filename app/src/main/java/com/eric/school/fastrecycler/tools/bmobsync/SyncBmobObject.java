package com.eric.school.fastrecycler.tools.bmobsync;

import java.util.concurrent.CountDownLatch;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 14:57
 */
public class SyncBmobObject extends BmobObject {

    public String syncSave() throws BmobException {
        final SyncBmobResult<String> syncBmobResult = new SyncBmobResult<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                syncBmobResult.setUp(objectId, e);
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

    public void syncUpdate(String objectId) throws BmobException {
        final SyncBmobResult<String> syncBmobResult = new SyncBmobResult<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                syncBmobResult.setUp(null, e);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        if (syncBmobResult.exception != null) {
            throw syncBmobResult.exception;
        }
    }

    public void syncDelete() throws BmobException {
        final SyncBmobResult<String> syncBmobResult = new SyncBmobResult<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                syncBmobResult.setUp(null, e);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        if (syncBmobResult.exception != null) {
            throw syncBmobResult.exception;
        }
    }
}
