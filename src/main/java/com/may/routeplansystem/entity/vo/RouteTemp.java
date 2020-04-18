package com.may.routeplansystem.entity.vo;

import com.may.routeplansystem.entity.po.NodePojo;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class RouteTemp {

    private List<List<NodePojo>> route;

    public RouteTemp(List<List<NodePojo>> route) {
        this.route = route;
    }

    public RouteTemp(int count) {
        route = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            route.add(new LinkedList<>());
        }
    }
}
