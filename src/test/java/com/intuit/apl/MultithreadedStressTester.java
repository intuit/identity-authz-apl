package com.intuit.apl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * A class that "blitzes" an object by calling it many times, from
 * multiple threads.  Used for stress-testing synchronisation.
 *
 */

class MultithreadedStressTester {
    /**
     * The default number of threads to run concurrently.
     */
    private static final int DEFAULT_THREAD_COUNT = 2;

    private final ExecutorService executor;
    private final int threadCount;
    private final int iterationCount;


    MultithreadedStressTester(int iterationCount) {
        this(DEFAULT_THREAD_COUNT, iterationCount);
    }

    private MultithreadedStressTester(int threadCount, int iterationCount) {
        this.threadCount = threadCount;
        this.iterationCount = iterationCount;
        /*class YourThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Your name");
            }
        };*/
        this.executor = Executors.newCachedThreadPool();
    }

    void stress(final Runnable action) throws InterruptedException {
        spawnThreads(action).await();
    }

    private CountDownLatch spawnThreads(final Runnable action) {
        final CountDownLatch finished = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        repeat(action);
                    }
                    finally {
                        finished.countDown();
                    }
                }
            });
        }
        return finished;
    }

    private void repeat(Runnable action) {
        for (int i = 0; i < iterationCount; i++) {
            action.run();
        }
    }
}