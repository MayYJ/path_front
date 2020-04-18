package com.may.routeplansystem.constant;

public interface ProcessState {

    int NEW_QUESTION = 0;
    int LOAD_NODE = 1;
    int LOAD_VEHICLE = 2;
    int CALCULATING_DISTANCE = 3;
    int COMPLETE_DISTANCE_PREPARE = 4;
    int PROCESSING_GENETIC = 5;
    int COMPLETE_GENETIC = 6;
    int PROCESSING_SIMPLE = 7;
    int COMPLETE_SIMPLW = 8;
    int PROCESSING_NEW_GENETIC = 9;
    int COMPLETE_NEW_GENETIC = 10;
}
