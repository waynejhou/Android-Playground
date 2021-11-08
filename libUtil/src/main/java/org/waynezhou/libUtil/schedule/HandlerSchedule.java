package org.waynezhou.libUtil.schedule;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public final class HandlerSchedule {
    private HandlerSchedule(){}
    @NonNull
    public static TimeoutHandler timeOut(long millis, @NonNull Runnable runnable){
        Looper looper = Looper.myLooper();
        if(looper==null) looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        handler.postDelayed(runnable, millis);
        return new TimeoutHandler(handler, runnable);
    }
    @NonNull
    public static IntervalHandler interval(long millis, @NonNull Runnable runnable){
        Looper looper = Looper.myLooper();
        if(looper==null) looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        RunnableWrapper wrapper = new RunnableWrapper();
        wrapper.runnable = ()->{
            runnable.run();
            handler.postDelayed(wrapper.runnable, millis);
        };
        handler.postDelayed(wrapper.runnable, millis);
        return new IntervalHandler(handler, wrapper);
    }
    static class RunnableWrapper{
        Runnable runnable;
    }
}
