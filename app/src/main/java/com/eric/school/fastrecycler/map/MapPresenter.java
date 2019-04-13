package com.eric.school.fastrecycler.map;

import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.eric.school.fastrecycler.R;
import com.eric.school.fastrecycler.tools.bean.ClientMailbox;
import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;
import com.eric.school.fastrecycler.tools.bean.ServerMailbox;
import com.eric.school.fastrecycler.tools.bmobsync.SyncBmobQuery;
import com.eric.school.fastrecycler.tools.datasource.garbagecan.GarbageCanDataSource;
import com.eric.school.fastrecycler.tools.user.UserEngine;
import com.eric.school.fastrecycler.tools.util.AMapUtils;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;
import com.eric.school.fastrecycler.tools.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 17:24
 */
public class MapPresenter implements IMapContract.Presenter {

    private MapActivity mView;

    public MapPresenter(MapActivity mView) {
        this.mView = mView;
    }

    private ExecutorService mExecutor = Executors.newFixedThreadPool(1);

    private RecyclerPlace mRecyclerPlace;
    private List<GarbageCan> mGarbageCanList;
    private ArrayList<MarkerOptions> mMarkerOptionsList;

    @Override
    public void getMarkerData() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    RecyclerPlace recyclerPlace = GarbageCanDataSource.getImpl().getRecyclerPlace();
                    List<GarbageCan> garbageCanList = GarbageCanDataSource.getImpl().getGarbageCanList(recyclerPlace);

                    ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();
                    for (GarbageCan garbageCan : garbageCanList) {
                        markerOptionsList.add(convertToMarkerOptions(garbageCan));
                    }
                    // 将回收站图标设置在List最后一个元素
                    markerOptionsList.add(convertToMarkerOptions(recyclerPlace));

                    mRecyclerPlace = recyclerPlace;
                    mGarbageCanList = garbageCanList;
                    mMarkerOptionsList = markerOptionsList;

                    mView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.showMarkers(mMarkerOptionsList);
                        }
                    });
                } catch (final BmobException e) {
                    dealException(e);
                }
            }
        };
        mExecutor.execute(task);
    }

    @Override
    public void clickMarker(MarkerOptions markerOptions) {
        ArrayList<MarkerOptions> markerOptionsList = mMarkerOptionsList;
        // 判断点击的是垃圾桶marker还是回收站marker
        int index = markerOptionsList.indexOf(markerOptions);
        if (index == markerOptionsList.size() - 1) {
            // 最后一个数据，点击的是回收站
            mView.reactClickRecyclerPlace(mRecyclerPlace);
        } else {
            // 点击的是垃圾桶
            mView.reactClickGarbageCan(mGarbageCanList.get(index));
        }
    }

    @Override
    public void clickRouteRequest(String startTimeISO, String endTimeISO) {
        final Date startTime = Utils.fromISO(startTimeISO);
        final Date endTime = Utils.fromISO(endTimeISO);
        if (startTime == null || endTime == null) {
            dealException(new IllegalStateException("Time is not a ISO!"));
            return;
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    ClientMailbox clientMail = new ClientMailbox();
                    clientMail.setUser(UserEngine.getInstance().getCurrentUser());
                    clientMail.setStartTime(new BmobDate(startTime));
                    clientMail.setEndTime(new BmobDate(endTime));
                    String clientMailId = clientMail.syncSave();

                    // 等待后台处理
                    Thread.sleep(15000);

                    SyncBmobQuery<ServerMailbox> serverMBQuery = new SyncBmobQuery<>();
                    serverMBQuery.addWhereEqualTo("user", UserEngine.getInstance().getCurrentUser().getObjectId());
                    serverMBQuery.addWhereEqualTo("mail", clientMailId);
                    serverMBQuery.addWhereEqualTo("valid", true);
                    serverMBQuery.order("-createdAt");
                    List<ServerMailbox> temp = serverMBQuery.syncFindObjects();
                    if (temp.size() < 1) throw new BmobException("Server not replied!");
                    ServerMailbox serverMail = temp.get(0);
                    serverMail.setValid(false);
                    serverMail.syncUpdate(serverMail.getObjectId());
                    List<String> garbageCanIdList = new ArrayList<>(Arrays.asList(serverMail.getGarbageCanList().split(",")));

                    SyncBmobQuery<GarbageCan> query = new SyncBmobQuery<>();
                    query.addWhereContainedIn("objectId", garbageCanIdList);
                    List<GarbageCan> resultList = query.syncFindObjects();

                    List<LatLonPoint> checkPoints = new ArrayList<>();
                    checkPoints.add(new LatLonPoint(mRecyclerPlace.getLatitude(), mRecyclerPlace.getLongitude()));
                    for (GarbageCan can : resultList) {
                        checkPoints.add(new LatLonPoint(can.getLatitude(), can.getLongitude()));
                    }
                    // 计算TSP问题
                    final ArrayList<LatLonPoint> path = AMapUtils.sortWayPointSeriesShortestPath(mView, checkPoints);
                    path.add(new LatLonPoint(mRecyclerPlace.getLatitude(), mRecyclerPlace.getLongitude()));

                    mView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.startNavigate(path);
                        }
                    });
                } catch (BmobException e) {
                    dealException(e);
                } catch (InterruptedException e) {
                    dealException(e);
                }
            }
        };
        mExecutor.execute(task);
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
            return null;
        }
        View markerView = LayoutInflater.from(mView).inflate(layoutId, null, false);
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromBitmap(AndroidUtils.loadBitmapFromView(markerView)));
    }

    private void dealException(final Exception e) {
        mView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showError(e.getMessage());
            }
        });
    }
}
