package com.eric.school.fastrecycler.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
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
import com.eric.school.fastrecycler.tools.util.AMapUtil;
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

public class MapActivity extends BaseActivity {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x01;
    private static final String TAG = "MapActivity";

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
        if (UserEngine.getInstance().getCurrentUser() != null) {
            getGarbageCansLocations();
        } else {
            AndroidUtils.showUnknownErrorToast();
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
                        ClientMailbox mailbox = new ClientMailbox();
                        mailbox.setUser(UserEngine.getInstance().getCurrentUser());
                        mailbox.setStartTime(new BmobDate(getDate(startTimeInput.getText().toString())));
                        mailbox.setEndTime(new BmobDate(getDate(endTimeInput.getText().toString())));
                        mailbox.save(new SaveListener<String>() {
                            @Override
                            public void done(final String mailId, BmobException e) {
                                if (e == null) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(15000);

                                                BmobQuery<ServerMailbox> serverMBQuery = new BmobQuery<>();
                                                serverMBQuery.addWhereEqualTo("user", UserEngine.getInstance().getCurrentUser().getObjectId());
                                                serverMBQuery.addWhereEqualTo("mail", mailId);
                                                serverMBQuery.addWhereEqualTo("valid", true);
                                                serverMBQuery.order("-createdAt");
                                                serverMBQuery.findObjects(new FindListener<ServerMailbox>() {
                                                    @Override
                                                    public void done(final List<ServerMailbox> list, BmobException e) {
                                                        dialog.dismiss();
                                                        if (e == null && list != null && !list.isEmpty()) {
                                                            ServerMailbox mail = list.get(0);
                                                            ServerMailbox m = new ServerMailbox();
                                                            m.setObjectId(mailId);
                                                            m.setValid(false);
                                                            m.update(new UpdateListener() {
                                                                @Override
                                                                public void done(BmobException e) {
                                                                }
                                                            });
                                                            List<String> idList = new ArrayList<>(Arrays.asList(mail.getGarbageCanList().split(",")));
                                                            getRoute(MapActivity.this, idList);
                                                        } else {
                                                            AndroidUtils.showUnknownErrorToast();
                                                        }

                                                    }
                                                });
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    dialog.dismiss();
                                    AndroidUtils.showUnknownErrorToast();
                                }
                            }
                        });
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
                if (index > mGarbageCanList.size() - 1) {
                    return false;
                }
                final GarbageCan garbageCan = mGarbageCanList.get(index);

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
        recyclerPlaceBmobQuery.addWhereEqualTo("recycler", UserEngine.getInstance().getCurrentUser().getObjectId());
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

    public void getRoute(final Context context, final List<String> garbageCanIdList) {
        if (garbageCanIdList == null || garbageCanIdList.size() < 2) {
            return;
        }

        BmobQuery<GarbageCan> query = new BmobQuery<>();
        query.addWhereContainedIn("objectId", garbageCanIdList);
        query.findObjects(new FindListener<GarbageCan>() {
            @Override
            public void done(final List<GarbageCan> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<LatLonPoint> checkPoints = new ArrayList<>();
                            checkPoints.add(new LatLonPoint(mRecyclerPlace.getLatitude(), mRecyclerPlace.getLongitude()));
                            for (GarbageCan can : list) {
                                checkPoints.add(new LatLonPoint(can.getLatitude(), can.getLongitude()));
                            }
                            final ArrayList<LatLonPoint> path = AMapUtil.sortWayPointSeriesShortestPath(context, checkPoints);
                            path.add(new LatLonPoint(mRecyclerPlace.getLatitude(), mRecyclerPlace.getLongitude()));

                            MapActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MapActivity.this, RouteNaviActivity.class);
                                    intent.putParcelableArrayListExtra("path", path);
                                    startActivity(intent);
                                }
                            });
                        }
                    }).start();
                } else {
                    AndroidUtils.showUnknownErrorToast();
                }
            }
        });
    }

    private Date getDate(String isoTime) {
        String[] str_list = isoTime.replace("-", ":").replace(" ", ":").split(":");
        int year = Integer.valueOf(str_list[0]);
        int month = Integer.valueOf(str_list[1]);
        int day = Integer.valueOf(str_list[2]);
        int hour = Integer.valueOf(str_list[3]);
        int minute = Integer.valueOf(str_list[4]);
        int second = Integer.valueOf(str_list[5]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTime();
    }
}
