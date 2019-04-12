package com.eric.school.fastrecycler.tools.datasource.route;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/12 0012 23:02
 */
public abstract class RouteDataSource implements IRouteDataSource {
    public static RouteDataSource getImpl() {
        return RouteDataSourceImpl.getInstance();
    }
}
