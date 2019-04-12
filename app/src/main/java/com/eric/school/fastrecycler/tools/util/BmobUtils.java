package com.eric.school.fastrecycler.tools.util;

import com.eric.school.fastrecycler.tools.base.BiCallback;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/4 0004 20:17
 */
public class BmobUtils {

    public static class BmobSyncObject<T> {
        private boolean dataSetUp;
        private T data;
        private BmobException exception;

        public boolean isSuccess() {
            return dataSetUp && data != null && exception == null;
        }

        public void setUp(boolean dataExpected, T data, BmobException e) {
            // 如果data不合法，但是数据请求成功了，这个特殊case不能让exception被覆写为null
            // 并且要清除不可用的数据
            if (dataExpected) {
                this.data = data;
                this.exception = null;
            } else {
                this.data = null;
                if (e != null) {
                    this.exception = e;
                } else {
                    this.exception = new BmobException(AndroidUtils.getUnknownErrorString());
                }
            }
            dataSetUp = true;
        }

        public T getData() {
            return dataSetUp ? data : null;
        }

        public BmobException getException() {
            return dataSetUp ? exception : new BmobException(AndroidUtils.getUnknownErrorString());
        }
    }

    public static void uploadFile(String filePath, final BiCallback<String> callback) {
        if (callback == null) {
            return;
        }
        final BmobFile bmobFile = new BmobFile(new File(filePath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.done(bmobFile.getFileUrl());
                } else {
                    callback.error(e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer ignore) {
            }
        });
    }
}
