package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.VehicleMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:dengsiyuan
 * @Date:2018/9/23 20:49
 */
@Repository("vehicleDao")
public interface VehicleDao {

    /**
     * 用户车辆导入
     *
     * @param vehicleMessage
     * @return boolean
     */
    boolean insertVehicle(VehicleMessage vehicleMessage);

    /**
     * 用户信息查询
     *
     * @param userId
     * @return vehicleMessage:返回车辆信息
     */
    List<VehicleMessage> searchVehicleByOwnId(int userId);

    /**
     * 查询车辆信息
     *
     * @param vehicleId
     * @return vehicleMessage
     */
    VehicleMessage searchVehicleByVehicleId(int vehicleId);

    /**
     * 通过车辆ID删除车辆
     * @param vehicleId
     * @return
     */
    boolean deleteVehicleByVehicleId(int vehicleId);

    /**
     * 删除车辆信息
     *
     * @param deleteList
     * @return true/false
     */
    boolean deleteVehicle(ArrayList deleteList);

    /**
     * 通过问题Id删除车辆
     * @param questionId
     * @return
     */
    boolean deleteVehicleByQuestionId(int questionId);

    /**
     * 通过问题ID查找车辆
     * @param questionId
     * @return
     */
    List<VehicleMessage> searchVehicleByQuestionId(int questionId);

    /**
     * 批量添加车辆数据
     * @param vehicleMessages
     * @return
     */
    boolean insertVehiclesBatch(@Param("vehicleMessages") List<VehicleMessage> vehicleMessages);
}
