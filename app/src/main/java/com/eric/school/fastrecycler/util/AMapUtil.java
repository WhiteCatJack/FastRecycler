package com.eric.school.fastrecycler.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/9 0009 14:18
 */
public class AMapUtil {

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把LatLng对象转化为Poi对象
     */
    public static Poi convertToPoi(String name, LatLng latlon) {
        return new Poi(name, latlon, "");
    }
}
