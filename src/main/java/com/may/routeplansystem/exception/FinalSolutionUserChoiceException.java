package com.may.routeplansystem.exception;

/**
 * 当用户想选择一个方案时，如果已经选择了一个通过抛这个异常来提示用户已经选择了一个方案
 */
public class FinalSolutionUserChoiceException extends RuntimeException {

    public FinalSolutionUserChoiceException(String msg) {
        super(msg);
    }
}
