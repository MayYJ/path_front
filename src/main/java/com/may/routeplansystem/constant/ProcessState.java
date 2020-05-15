package com.may.routeplansystem.constant;

public interface ProcessState {

    int NEW_QUESTION = 0;
    int LOAD_NODE = 1;
    int LOAD_VEHICLE = 2;
    int CALCULATING_DISTANCE = 3;
    int COMPLETE_DISTANCE_PREPARE = 4;
    int SOME_ALGORITHM_PROCESSING = 5;

    int ALGORITHM_NOT_EXECUTE = 0;
    int ALGORITHM_IS_PROCESSING = 1;
    int ALGORITHM_COMPLETED = 2;
}
