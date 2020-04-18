package com.may.routeplansystem.algorithm;

import com.may.routeplansystem.exception.ParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlgorithmContext {

    private Map<Integer, AlgorithmExecutor> map = new HashMap<>();

    private Map<Integer, String> nameMap = new HashMap<>();

    public void addAlgorithm(int key, AlgorithmExecutor algorithmExecutor, String name) {
        map.put(key, algorithmExecutor);
        nameMap.put(key, name);
    }

    public void execute(Integer key, Integer questionId) {
        System.out.println("判断存不存在该算法！");
        Objects.requireNonNull(key);
        Objects.requireNonNull(questionId);
        AlgorithmExecutor algorithmExecutor = map.get(key);
        if (algorithmExecutor == null) {
            throw new ParameterException("没有指定的算法");
        }
        algorithmExecutor.execute(questionId);
    }

    public void stop(Integer questionId, Integer key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(questionId);
        AlgorithmExecutor algorithmExecutor = map.get(key);
        if (algorithmExecutor == null) {
            throw new ParameterException("没有指定的算法");
        }
        algorithmExecutor.stop(questionId);
    }

    public Map<Integer, AlgorithmExecutor> getMap() {
        return map;
    }

    public Map<Integer, String> getNameMap() {
        return nameMap;
    }

}
