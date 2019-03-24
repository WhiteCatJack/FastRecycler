package com.eric.school.fastrecycler;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements IMapContract.View {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x01;

    private IMapContract.Presenter mPresenter;

    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private AMap mAMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapPresenter(this);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mv_map);
        mMapView.onCreate(savedInstanceState);

        checkPermission();
        initAMap();
        mPresenter.getGarbageCansLocations();
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

        mAMap.setMyLocationStyle(new MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
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
                /*
                    get info from marker
                 */
                return false;
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

    @Override
    public void showGarbageCans(ArrayList<MarkerOptions> markerOptionsList) {
        mAMap.getMapScreenMarkers().clear();
        mAMap.addMarkers(markerOptionsList, false);
    }
}
