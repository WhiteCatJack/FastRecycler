package com.eric.school.fastrecycler.datasource;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.eric.school.fastrecycler.GarbageCan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 泽乾
 * createAt 2019/3/27 0027 20:10
 */
public abstract class RouteDataSource {
    private static volatile RouteDataSource sInstance;

    protected RouteDataSource() {
    }

    public static RouteDataSource getInstance() {
        if (sInstance == null) {
            synchronized (RouteDataSource.class) {
                if (sInstance == null) {
                    sInstance = new RouteDataSourceImpl();
                }
            }
        }
        return sInstance;
    }

    public void getRoute(Context context, List<>) {
        AMapUtil.convertToLatLng
        Marker marker
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(), new LatLonPoint());
        List<LatLonPoint> passedByPoints = new ArrayList<>();
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null,
                null, "");
    }
}
