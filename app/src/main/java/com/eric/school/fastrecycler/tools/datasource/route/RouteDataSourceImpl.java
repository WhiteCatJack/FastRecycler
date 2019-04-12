package com.eric.school.fastrecycler.tools.datasource.route;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:01
 */
class RouteDataSourceImpl extends RouteDataSource {

    private static volatile RouteDataSource sInstance;

    private RouteDataSourceImpl() {
    }

    static RouteDataSource getInstance() {
        if (sInstance == null) {
            synchronized (RouteDataSourceImpl.class) {
                if (sInstance == null) {
                    sInstance = new RouteDataSourceImpl();
                }
            }
        }
        return sInstance;
    }
}
