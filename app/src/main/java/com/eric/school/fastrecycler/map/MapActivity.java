package com.eric.school.fastrecycler.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
import com.amap.api.services.core.LatLonPoint;
import com.eric.school.fastrecycler.R;
import com.eric.school.fastrecycler.map.navi.RouteNaviActivity;
import com.eric.school.fastrecycler.tools.base.BaseActivity;
import com.eric.school.fastrecycler.tools.bean.ClientMailbox;
import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.GarbageRecord;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.bean.ServerMailbox;
import com.eric.school.fastrecycler.tools.user.UserEngine;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MapActivity extends BaseActivity implements IMapContract.View {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x01;
    private static final String TAG = "MapActivity";

    private IMapContract.Presenter mPresenter;

    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private AMap mAMap;
    private List<GarbageCan> mGarbageCanList;
    private RecyclerPlace mRecyclerPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapPresenter(this);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mv_map);
        mMapView.onCreate(savedInstanceState);

        checkPermission();
        initAMap();
        if (checkIfLogin()) {
            mPresenter.getMarkerData();
        }
        findViewById(R.id.fab_get_target_garbage_can_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.dialog_predict_time_input, null, false);
                final EditText startTimeInput = layout.findViewById(R.id.et_start_time);
                final EditText endTimeInput = layout.findViewById(R.id.et_end_time);
                View submitButton = layout.findViewById(R.id.bt_submit);
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setView(layout);
                final Dialog dialog = builder.create();
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.clickRouteRequest(startTimeInput.getText().toString(), endTimeInput.getText().toString());
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 确认所需权限
     */
    private void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
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
                mPresenter.clickMarker(marker.getOptions());
                return true;
            }
        });
        mAMap.getUiSettings().setZoomControlsEnabled(false);
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
    public void showMarkers(ArrayList<MarkerOptions> markerOptionsList) {
        mAMap.addMarkers(markerOptionsList, true);
    }

    @Override
    public void reactClickGarbageCan(final GarbageCan garbageCan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.dialog_input_recycled_garbage_volume, null);
        final EditText editText = layout.findViewById(R.id.et_recycled_volume);
        View submitButton = layout.findViewById(R.id.bt_submit);
        builder.setView(layout);
        final Dialog dialog = builder.create();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GarbageRecord garbageRecord = new GarbageRecord();
                garbageRecord.setGarbageCan(garbageCan);
                garbageRecord.setTime(new BmobDate(Calendar.getInstance().getTime()));
                garbageRecord.setVolumeChange(Double.valueOf(editText.getText().toString()));
                garbageRecord.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        AndroidUtils.showToast("你成功上传了回收记录！");
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void reactClickRecyclerPlace(RecyclerPlace recyclerPlace) {
        AndroidUtils.showToast("此处是回收站。");
    }

    @Override
    public void startNavigate(ArrayList<LatLonPoint> path) {
        Intent intent = new Intent(MapActivity.this, RouteNaviActivity.class);
        intent.putParcelableArrayListExtra("path", path);
        startActivity(intent);
    }
}
