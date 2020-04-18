package com.may.routeplansystem.util.taskCommit;

import com.may.routeplansystem.exception.ServerException;

public class TaskUncatchedExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        throw new ServerException("算法或距离执行错误");
    }

}
