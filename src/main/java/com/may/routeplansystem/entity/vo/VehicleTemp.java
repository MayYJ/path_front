package com.may.routeplansystem.entity.vo;

import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.entity.po.VehicleMessage;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @author :DengSiYuan
 * @date :2019/10/5 19:32
 * @desc :
 */

@Data
@ToString
public class VehicleTemp{
    private VehicleMessage vehicleMessage;

    private Integer serviceNodeCount;

    private List<NodePojo> vehicleServiceNodes;

    private Long routeTime;

    private Long routeDistance;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        VehicleTemp that = (VehicleTemp) o;
        return Objects.equals(vehicleServiceNodes, that.vehicleServiceNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleServiceNodes);
    }
}
