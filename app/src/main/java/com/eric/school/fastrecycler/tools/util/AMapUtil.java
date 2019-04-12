package com.eric.school.fastrecycler.tools.util;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;

import java.util.ArrayList;
import java.util.List;

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

    public static LatLng convertToLatLng(LatLonPoint latlon) {
        return new LatLng(latlon.getLatitude(), latlon.getLongitude());
    }

    public static NaviLatLng convertToNaviLatLng(LatLonPoint latLonPoint) {
        return new NaviLatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    public static Poi convertToPoi(LatLonPoint latLonPoint) {
        return new Poi("", convertToLatLng(latLonPoint), null);
    }

    @WorkerThread
    public static ArrayList<LatLonPoint> sortWayPointSeriesShortestPath(Context context, List<LatLonPoint> wayPointList) {
        if (wayPointList == null) {
            return null;
        }
        DistanceSearch distanceSearch = new DistanceSearch(context);
        DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        distanceQuery.setType(DistanceSearch.TYPE_DISTANCE);

        double[][] matrix = new double[wayPointList.size()][wayPointList.size()];
        List<LatLonPoint> starts = new ArrayList<>();
        for (int i = 0; i < wayPointList.size() - 1; i++) {
            starts.clear();
            LatLonPoint end = wayPointList.get(i);
            for (int j = i + 1; j < wayPointList.size(); j++) {
                if (j == i) {
                    continue;
                }
                LatLonPoint start = wayPointList.get(j);
                starts.add(start);
            }
            distanceQuery.setOrigins(starts);
            distanceQuery.setDestination(end);
            try {
                DistanceResult distanceResult = distanceSearch.calculateRouteDistance(distanceQuery);
                List<DistanceItem> distanceItemList = distanceResult.getDistanceResults();
                for (int k = 0; k < distanceItemList.size(); k++) {
                    // 第i个LLP到第j个LLP的距离
                    double distance = distanceItemList.get(k).getDistance();
                    matrix[i][wayPointList.indexOf(starts.get(k))] = distance;
                }
            } catch (AMapException e) {
                e.printStackTrace();
                return null;
            }
        }
        TSPGeneticAlgorithm ga = TSPGeneticAlgorithm.getInstance();
        ga.setMaxGeneration(1000);
        ga.setAutoNextGeneration(true);
        int[] best = ga.tsp(matrix);
        ArrayList<LatLonPoint> bestPath = new ArrayList<>();
        for (int i : best) {
            bestPath.add(wayPointList.get(i));
        }

        return bestPath;
    }
}
