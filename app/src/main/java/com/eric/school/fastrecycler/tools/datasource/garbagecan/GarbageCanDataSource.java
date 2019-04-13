package com.eric.school.fastrecycler.tools.datasource.garbagecan;

import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.tools.base.RpcCallback;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:02
 */
public abstract class GarbageCanDataSource implements IGarbageCanDataSource {
    public static GarbageCanDataSource getImpl() {
        return GarbageCanDataSourceImpl.getInstance();
    }
}
