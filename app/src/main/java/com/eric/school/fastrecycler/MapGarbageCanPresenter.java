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
public class MapGarbageCanPresenter implements IMapContract.GarbageCanPresenter {

    private IMapContract.View mView;

    public MapGarbageCanPresenter(@NonNull IMapContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void getGarbageCansLocations() {
        GarbageCanDataSource.getInstance().getAll(new FindListener<GarbageCan>() {
            @Override
            public void done(List<GarbageCan> list, BmobException e) {
                if (e == null) {
                    if (list == null){
                        return;
                    }
                    /*
                        do transformation
                     */
                    ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();
                    for (GarbageCan can : list) {
                        markerOptionsList.add(transform(can));
                    }
                    mView.showGarbageCans(markerOptionsList);
                } else {

                }
            }

            private MarkerOptions transform(GarbageCan can) {
                return new MarkerOptions()
                        .position(new LatLng(can.getLatitude(), can.getLongitude()))
                        .draggable(false);
//                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource()));
            }
        });
    }
}
