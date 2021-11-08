package org.waynezhou.libUtil.schedule;

import android.os.Handler;

import androidx.annotation.NonNull;

public class TimeoutHandler {
    @NonNull
    private final Runnable cancelAction;
    TimeoutHandler(@NonNull Thread thread){
        cancelAction = ()->{
            if(!thread.isInterrupted())
                thread.interrupt();
        };
    }
    TimeoutHandler(
      @NonNull Handler handler,
      @NonNull Runnable runnable
    ){
        cancelAction = ()->{
            handler.removeCallbacks(runnable);
        };
    }
    public void cancel(){
        cancelAction.run();
    }
}

