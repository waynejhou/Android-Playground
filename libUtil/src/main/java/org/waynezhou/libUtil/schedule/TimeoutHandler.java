package org.waynezhou.libUtil.schedule.thread;

import android.os.Handler;

public class TimeoutHandler {
    private final Runnable interruptAction;
    TimeoutHandler(Thread thread){
        interruptAction = ()->{
            if(!thread.isInterrupted())
                thread.interrupt();
        };
    }
    public TimeoutHandler(
      Handler handler,
      Runnable runnable
    ){
        interruptAction = ()->{
            handler.removeCallbacks(runnable);
        };
    }
    public void interrupt(){
        interruptAction.run();
    }
}

