package org.waynezhou.libUtil.schedule.thread;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.LogHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ThreadUtils {
    private ThreadUtils(){}
    
    @NonNull
    public static Thread run(@NonNull Runnable runnable){
        Thread thd = new Thread(runnable);
        thd.start();
        return thd;
    }

    @NonNull
    public static TimeoutHandler timeOut(long millis, @NonNull Runnable runnable){
        return new TimeoutHandler(run(()->{
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                LogHelper.e("Timeout Thread Interrupted", e);
            }
            runnable.run();
        }));
    }
    
    @NonNull
    public static IntervalHandler interval(long millis, @NonNull Runnable runnable){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(runnable, 0, millis, TimeUnit.MILLISECONDS);
        return new IntervalHandler(executor);
    }
}
