package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.VehicleMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class VehicleDaoTest {

    @Resource
    private VehicleDao vehicleDao;

    @Test
    @Rollback
    public void insertVehicle() {
        VehicleMessage vehicleMessage = new VehicleMessage();
        vehicleMessage.setCapacity(10);
        vehicleMessage.setDate("2017-10-12");
        vehicleMessage.setOil(10);
        vehicleMessage.setPrice(12);
        vehicleMessage.setQuestionId(1);
        vehicleMessage.setType("fdsf");
        vehicleMessage.setOwnerId(1);
        boolean flag = vehicleDao.insertVehicle(vehicleMessage);
        assertTrue(flag);
    }

    @Test
    public void searchVehicleByOwnId() {
        List<VehicleMessage> messageList = vehicleDao.searchVehicleByOwnId(1);
        assertEquals(1, messageList.size());
    }

    @Test
    public void searchVehicleByVehicleId() {
    }

    @Test
    @Rollback
    public void deleteVehicle() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        boolean flag = vehicleDao.deleteVehicle(list);
        assertTrue(flag);
    }
}