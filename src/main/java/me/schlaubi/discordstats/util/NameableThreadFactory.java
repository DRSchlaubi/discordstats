package me.schlaubi.discordstats.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internally used threadFactory
 */
public class NameableThreadFactory implements java.util.concurrent.ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final transient ThreadGroup group;
    private final transient AtomicInteger threadNumber = new AtomicInteger(1);
    private final transient String namePrefix;

    public NameableThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    /**
     * Spawns a new thread.
     * @param r the task for the thread
     * @see java.util.concurrent.ThreadFactory#newThread(Runnable)
     * @return the thread
     */
    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}