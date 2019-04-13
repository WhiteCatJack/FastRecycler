package com.eric.school.fastrecycler.map;

import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 17:24
 */
interface IMapContract {
    interface View {
        void showMarkers(ArrayList<MarkerOptions> markerOptionsList);

        void reactClickGarbageCan(GarbageCan garbageCan);

        void reactClickRecyclerPlace(RecyclerPlace recyclerPlace);

        void startNavigate(ArrayList<LatLonPoint> path);
    }

    interface Presenter {
        void getMarkerData();

        void clickMarker(MarkerOptions markerOptions);

        void clickRouteRequest(String startTimeISO, String endTimeISO);
    }
}
