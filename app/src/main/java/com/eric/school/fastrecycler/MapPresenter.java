package com.eric.school.fastrecycler;

import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.datasource.GarbageCanDataSource;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 11:31
 */
public class MapPresenter implements IMapContract.Presenter {

    private IMapContract.View mView;

    public MapPresenter(@NonNull IMapContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void getGarbageCansLocations() {
        GarbageCanDataSource.getInstance().getAll(new FindListener<GarbageCan>() {
            @Override
            public void done(List<GarbageCan> list, BmobException e) {
                if (e == null) {
                    /*
                        do transformation
                     */
//                    mView.showGarbageCans();
                } else {

                }
            }
        });
    }
}
