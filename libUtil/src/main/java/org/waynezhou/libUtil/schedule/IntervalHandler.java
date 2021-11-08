package org.waynezhou.libUtil.schedule.thread;

import java.util.concurrent.ScheduledExecutorService;

public class IntervalHandler {
    private final ScheduledExecutorService scheduledExecutorService;
    
    IntervalHandler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }
    
    public void shutdown() {
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }
}
