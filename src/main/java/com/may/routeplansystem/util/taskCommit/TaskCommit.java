package com.may.routeplansystem.util.taskCommit;

import java.util.concurrent.*;

public class TaskCommit {
    private static int NCPU = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NCPU + 1, NCPU + 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new TackCommitThreadFactory());

    public static void commitTask(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    public static Future sumbmitTask(Runnable runnable) {
        return threadPoolExecutor.submit(runnable);
    }
}
