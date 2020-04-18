package com.may.routeplansystem.config;

import com.may.routeplansystem.algorithm.AlgorithmContext;
import com.may.routeplansystem.algorithm.imp.GeneticAlgorithm;
import com.may.routeplansystem.algorithm.imp.NewGeneticAlgorithm;
import com.may.routeplansystem.algorithm.imp.SimpleAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class AlgorithmConfig {


    @Bean
    public AlgorithmContext algorithmContext(SimpleAlgorithm simpleAlgorithm,
                                             GeneticAlgorithm geneticAlgorithm, NewGeneticAlgorithm newGeneticAlgorithm) {
        AlgorithmContext algorithmContext = new AlgorithmContext();
        algorithmContext.addAlgorithm(1, simpleAlgorithm, "随机算法");
        algorithmContext.addAlgorithm(2, geneticAlgorithm, "遗传算法");
        algorithmContext.addAlgorithm(3,newGeneticAlgorithm,"优化遗传算法");
        return algorithmContext;
    }

}
