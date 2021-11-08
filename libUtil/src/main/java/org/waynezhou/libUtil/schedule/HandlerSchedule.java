package org.waynezhou.libUtil.schedule;

import android.os.Handler;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.schedule.thread.TimeoutHandler;

public final class HandlerUtils {
    private HandlerUtils(){}
    @NonNull
    public static TimeoutHandler timeOut(long millis, @NonNull Runnable runnable){
        Handler handler = new Handler();
        handler.postDelayed(runnable, millis);
        return new TimeoutHandler(handler, runnable);
    }
}
