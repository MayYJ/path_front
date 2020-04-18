package com.may.routeplansystem.entity.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @author :DengSiYuan
 * @date :2019/10/5 21:52
 * @desc :
 */
@Data
@ToString
public class Plan {

    private List<VehicleTemp> vehicleTempList;

    private Double fitness;

    private Long totalDistance;

    private Long totalTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Plan plan = (Plan) o;
        return Objects.equals(vehicleTempList, plan.vehicleTempList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleTempList);
    }
}
