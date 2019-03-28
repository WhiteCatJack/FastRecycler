package com.eric.school.fastrecycler;

import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/3/24 0024 11:29
 */
public interface IMapContract {
    interface View {
        void showGarbageCans(ArrayList<MarkerOptions> markerOptionsList);
    }

    interface GarbageCanPresenter {
        void getGarbageCansLocations();
    }

    interface RoutePresenter {
        void getRoute(ArrayList<GarbageCan> garbageCanList);
    }
}
