package com.sgandham.mycommuter.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgandham on 5/31/15.
 */
public class Route {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RouteDirection> getRouteDirectionList() {
        return routeDirectionList;
    }

    public void setRouteDirectionList(List<RouteDirection> routeDirectionList) {
        this.routeDirectionList = routeDirectionList;
    }

    private String name;
    private String code;
    List<RouteDirection> routeDirectionList = new ArrayList<RouteDirection>();


}
