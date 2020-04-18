package com.may.routeplansystem.service;

import com.may.routeplansystem.algorithm.imp.GeneticAlgorithm;
import com.may.routeplansystem.algorithm.imp.NewGeneticAlgorithm;
import com.may.routeplansystem.controller.AlgorithmController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author :DengSiYuan
 * @date :2019/10/4 10:42
 * @desc :
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GeneticAlgorithmTest {

    @Resource
    private NewGeneticAlgorithm newGeneticAlgorithm;
    @Resource
    private AlgorithmController controller;

    @Test
    public void test(){
        //controller.executeAlgorithm(3,2);
        newGeneticAlgorithm.executeAlgorithm(2);
    }

}
