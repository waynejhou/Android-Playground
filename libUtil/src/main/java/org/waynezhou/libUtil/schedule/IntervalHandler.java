package org.waynezhou.libUtil.schedule;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ScheduledExecutorService;

public class IntervalHandler {
    @NonNull
    private final Runnable stopAction;
    
    IntervalHandler(@NonNull ScheduledExecutorService scheduledExecutorService) {
        stopAction = () -> {
            if (!scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdown();
            }
        };
    }
    
    IntervalHandler(
      @NonNull Handler handler,
      @NonNull HandlerSchedule.RunnableWrapper wrapper
    ) {
        stopAction = () -> {
            handler.removeCallbacks(wrapper.runnable);
        };
    }
    
    public void shutdown() {
        stopAction.run();
    }
}
