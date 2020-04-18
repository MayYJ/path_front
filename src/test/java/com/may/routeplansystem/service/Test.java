package com.may.routeplansystem.service;

import com.may.routeplansystem.algorithm.imp.GeneticAlgorithm;
import com.may.routeplansystem.algorithm.imp.NewGeneticAlgorithm;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author :DengSiYuan
 * @date :2019/10/27 10:49
 * @desc :
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private NewGeneticAlgorithm algorithm;
    @Autowired
    private GeneticAlgorithm geneticAlgorithm;

    @org.junit.Test
    public void test(){
        long start = System.nanoTime();
        algorithm.executeAlgorithm(1);
        long end = System.nanoTime();
        System.out.println("---"+(start-end)/1000000);
        start = System.nanoTime();
        geneticAlgorithm.executeAlgorithm(1);
        end = System.nanoTime();
        System.out.println("---"+(start-end)/1000000);
    }

}
