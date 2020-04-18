package com.may.routeplansystem.util.taskCommit;

import java.util.concurrent.ThreadFactory;

public class TackCommitThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        Thread.UncaughtExceptionHandler handler = new TaskUncatchedExceptionHandler();
        thread.setUncaughtExceptionHandler(handler);
        return thread;
    }

}
