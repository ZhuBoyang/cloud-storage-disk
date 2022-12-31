package online.yangcloud.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取当前系统时间的时间戳
 * 注：如果Java自带的currentTimeMillis()不影响整体的效率时，不需要使用本类中的方法
 *
 * @author zhuby
 * @since 2020/11/20 5:16 下午
 */

public class CurrentTimeGetter {

    private volatile long now;

    private CurrentTimeGetter() {
        this.now = System.currentTimeMillis();
        scheduleTick();
    }

    private void scheduleTick() {
        new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable, "current-time-millis");
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> now = System.currentTimeMillis(), 1, 1, TimeUnit.MILLISECONDS);
    }

    public static long now() {
        return SingletonHolder.INSTANCE.now;
    }

    private static CurrentTimeGetter getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CurrentTimeGetter INSTANCE = new CurrentTimeGetter();
    }

}
