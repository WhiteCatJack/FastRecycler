package com.eric.school.fastrecycler;

import android.support.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.eric.school.fastrecycler.datasource.GarbageCanDataSource;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 11:31
 */
public class MapRoutePresenter implements IMapContract.GarbageCanPresenter {

    private IMapContract.View mView;

    public MapRoutePresenter(@NonNull IMapContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void getGarbageCansLocations() {

    }
}
