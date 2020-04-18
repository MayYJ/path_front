package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.Distance;
import org.junit.After;
import org.junit.Before;
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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
public class DistanceDaoTest {

    @Resource
    private DistanceDao distanceDao;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @Rollback
    public void insertDis() {
//        Distance distance = new Distance();
//        distance.setQuestionId(1);
//        distance.setStartNodeId(1);
//        distance.setEndNodeId(2);
//        distance.setDis(10);
//        distance.setTime(10);
//        boolean flag = distanceDao.insertDis(distance);
//        assertTrue(flag);
    }

    @Test
    @Rollback
    public void insertDistances() {
//        List<Distance> distances = new ArrayList<>();
//        Distance distance = new Distance();
//        distance.setQuestionId(1);
//        distance.setStartNodeId(1);
//        distance.setEndNodeId(2);
//        distance.setDis(10);
//        distance.setTime(10);
//        distances.add(distance);
//        boolean flag = distanceDao.insertDistances(distances);
    }

    @Test
    @Rollback
    public void removeDis() {
        boolean flag = distanceDao.removeDis(1);
        assertTrue(flag);
    }

    @Test
    public void findDistanceByStartIdAndEndId() {
//        Distance distance = distanceDao.findDistanceByStartIdAndEndId(401, 402);
//        assertEquals(1212, distance.getDis());
    }

    @Test
    public void deleteDistance() {
//        Distance distance = new Distance();
//        distance.setStartNodeId(401);
//        distance.setEndNodeId(402);
//        boolean flag = distanceDao.removeDistanceByStartNodeIdAndEndNodeId(distance);
//        assertTrue(flag);

    }
}