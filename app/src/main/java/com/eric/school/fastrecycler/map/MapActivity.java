package com.eric.school.fastrecycler.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

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
import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecycleInstruction;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;
import com.eric.school.fastrecycler.tools.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends BaseActivity implements IMapContract.View {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x01;
    private static final String TAG = "MapActivity";

    private IMapContract.Presenter mPresenter;

    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private AMap mAMap;
    private List<GarbageCan> mGarbageCanList;
    private RecyclerPlace mRecyclerPlace;
    private boolean mPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapPresenter(this);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mv_map);
        mMapView.onCreate(savedInstanceState);

        checkPermission();
    }

    private void init() {
        initAMap();
        if (checkIfLogin()) {
            mPresenter.getMarkerData();
        }
        findViewById(R.id.fab_get_target_garbage_can_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.bt_work_arrangement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                mPresenter.getRecycleArrangement();
            }
        });
    }

    /**
     * 确认所需权限
     */
    private void checkPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                },
                REQUEST_LOCATION_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permission3 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean permission4 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean permission5 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        mPermissionsGranted = permission1 && permission2 && permission3 && permission4 && permission5;
        if (mPermissionsGranted) {
            init();
        } else {
            AndroidUtils.showToast("Permission not granted!");
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

    @Override
    public void showRecycleInstructions(List<RecycleInstruction> instructions) {
        dismissLoadingDialog();

        List<List<RecycleInstruction>> arrangement = getArrangement(instructions);

        StringBuilder stringBuilder = new StringBuilder();
        for (List<RecycleInstruction> instructionList : arrangement) {
            String time = instructionList.get(0).getStartTime().getDate();
            stringBuilder.append("时间:")
                    .append(time)
                    .append("\n");
            for (RecycleInstruction instruction : instructionList) {
                stringBuilder.append("回收")
                        .append(instruction.getGarbageCan().getNumber())
                        .append("号垃圾桶.\n");
            }
            stringBuilder.append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(stringBuilder.toString());
        builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private List<List<RecycleInstruction>> getArrangement(List<RecycleInstruction> instructions) {
        List<List<RecycleInstruction>> ret = new ArrayList<>();

        int i = 0;
        while (i < instructions.size()) {
            List<RecycleInstruction> groupInstruction = new ArrayList<>();
            RecycleInstruction instruction = instructions.get(i);
            Date time = Utils.fromISO(instruction.getStartTime().getDate());
            groupInstruction.add(instruction);
            while (true) {
                i++;
                if (i >= instructions.size()) {
                    break;
                }
                RecycleInstruction instructionOther = instructions.get(i);
                Date timeA = Utils.fromISO(instructionOther.getStartTime().getDate());
                if (Math.abs(timeA.getTime() - time.getTime()) < 1000 * 60 * 10) {
                    groupInstruction.add(instructionOther);
                } else {
                    break;
                }
            }
            if (groupInstruction.size() > 0) {
                ret.add(groupInstruction);
            }
        }
        return ret;
    }
}
