package com.eric.school.fastrecycler;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.eric.school.fastrecycler.base.BaseActivity;
import com.eric.school.fastrecycler.bean.FRUser;
import com.eric.school.fastrecycler.bean.GarbageCan;
import com.eric.school.fastrecycler.bean.RecyclerPlace;
import com.eric.school.fastrecycler.user.UserEngine;
import com.eric.school.fastrecycler.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MapActivity extends BaseActivity {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x01;

    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private AMap mAMap;
    private List<GarbageCan> mGarbageCanList;
    private RecyclerPlace mRecyclerPlace;
    private ArrayList<MarkerOptions> mMarkerOptionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mv_map);
        mMapView.onCreate(savedInstanceState);

        checkPermission();
        initAMap();
        if (UserEngine.getCurrentUser() != null) {
            getGarbageCansLocations();
        } else {
            AndroidUtils.showUnknownErrorToast();
        }
    }

    /**
     * 确认所需权限
     */
    private void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }

    /**
     * 权限回调之后开始定位
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    /**
     * 初始化AMap，设置参数，注册监听
     */
    private void initAMap() {
        mAMap = mMapView.getMap();

        mAMap.setMyLocationStyle(new MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
        mAMap.setLocationSource(new LocationSource() {
            private OnLocationChangedListener mListener;

            @Override
            public void activate(OnLocationChangedListener listener) {
                mListener = listener;
                if (mLocationClient == null) {
                    //初始化定位
                    mLocationClient = new AMapLocationClient(MapActivity.this);
                    //设置定位回调监听
                    mLocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {
                            if (mListener != null && aMapLocation != null) {
                                if (aMapLocation.getErrorCode() == 0) {
                                    // 显示系统小蓝点
                                    mListener.onLocationChanged(aMapLocation);
                                } else {
                                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                                    Log.e("AmapErr", errText);
                                }
                            }
                        }
                    });
                    //设置定位参数
                    mLocationClient.setLocationOption(new AMapLocationClientOption()
                            .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy));
                    //启动定位
                    mLocationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                mListener = null;
                if (mLocationClient != null) {
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                }
                mLocationClient = null;
            }
        });
        mAMap.setMyLocationEnabled(true);
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int index = mMarkerOptionsList.indexOf(marker.getOptions());
                GarbageCan garbageCan = mGarbageCanList.get(index);
                AndroidUtils.showToast(garbageCan.getObjectId());
                return true;
            }
        });
    }

    /**
     * 生命周期告知AMap
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 生命周期告知AMap
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 生命周期告知AMap
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 生命周期告知AMap
     */
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        super.onDestroy();
    }

    public void showMarkers(List<GarbageCan> garbageCanList, RecyclerPlace recyclerPlace) {
        ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();
        for (GarbageCan can : garbageCanList) {
            markerOptionsList.add(convertToMarkerOptions(can));
        }
        markerOptionsList.add(convertToMarkerOptions(recyclerPlace));
        this.mMarkerOptionsList = markerOptionsList;
        mAMap.addMarkers(markerOptionsList, true);
    }

    public void getGarbageCansLocations() {
        BmobQuery<RecyclerPlace> recyclerPlaceBmobQuery = new BmobQuery<>();
        recyclerPlaceBmobQuery.addWhereEqualTo("recycler", UserEngine.getCurrentUser().getObjectId());
        recyclerPlaceBmobQuery.findObjects(new FindListener<RecyclerPlace>() {
            @Override
            public void done(List<RecyclerPlace> list, BmobException e) {
                if (e == null && !list.isEmpty()) {
                    RecyclerPlace recyclerPlace = list.get(0);
                    BmobQuery<GarbageCan> garbageCanBmobQuery = new BmobQuery<>();
                    garbageCanBmobQuery.addWhereEqualTo("areaCode", recyclerPlace.getAreaCode());
                    garbageCanBmobQuery.addWhereEqualTo("blockCode", recyclerPlace.getBlockCode());
                    garbageCanBmobQuery.findObjects(new FindListener<GarbageCan>() {
                        @Override
                        public void done(List<GarbageCan> list, BmobException e) {
                            if (e == null) {
                                mGarbageCanList = list;

                                showMarkers(mGarbageCanList, mRecyclerPlace);
                            } else {
                            }
                        }
                    });
                    mRecyclerPlace = recyclerPlace;
                } else {
                }
            }
        });
    }

    private <T> MarkerOptions convertToMarkerOptions(T data) {
        int layoutId = 0;
        double latitude = 0, longitude = 0;
        if (data instanceof GarbageCan) {
            layoutId = R.layout.view_garbage_can_marker;
            latitude = ((GarbageCan) data).getLatitude();
            longitude = ((GarbageCan) data).getLongitude();
        } else if (data instanceof RecyclerPlace) {
            layoutId = R.layout.view_recycler_place_marker;
            latitude = ((RecyclerPlace) data).getLatitude();
            longitude = ((RecyclerPlace) data).getLongitude();
        } else {
            AndroidUtils.showUnknownErrorToast();
        }
        View markerView = LayoutInflater.from(MapActivity.this).inflate(layoutId, mMapView, false);
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromView(markerView));
    }

    public void getRoute(Context context, List<LatLonPoint> checkPoints) {
        if (checkPoints == null || checkPoints.size() < 2) {
            return;
        }
        LatLonPoint start = null, end = null;
        List<LatLonPoint> passedByPoints = new ArrayList<>();
        for (int i = 0; i < checkPoints.size(); i++) {
            LatLonPoint point = checkPoints.get(i);
            if (i == 0) {
                start = point;
            } else if (i == checkPoints.size() - 1) {
                end = point;
            } else {
                passedByPoints.add(point);
            }
        }
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, passedByPoints,
                null, "");
    }
}
