package com.github.zacgamingpro1234.titaniumrewrite;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadManager {
    private static final ExecutorService GLOBAL_THREAD_POOL = Executors.newFixedThreadPool(8);
    private ThreadManager() {}

    public static void execute(Runnable task) {
        GLOBAL_THREAD_POOL.execute(task);
    }

    public static void shutdown() {
        GLOBAL_THREAD_POOL.shutdown();
    }
}
