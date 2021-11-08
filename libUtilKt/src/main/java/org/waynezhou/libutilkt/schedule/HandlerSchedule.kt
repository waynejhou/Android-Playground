package org.waynezhou.libutilkt.schedule

import android.os.Handler
import android.os.Looper

object HandlerSchedule {

    @JvmStatic
    fun timeOut(millis: Long, runnable: ()->Unit): TimeoutHandler {
        val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
        handler.postDelayed(runnable, millis)
        return TimeoutHandler(handler, runnable)
    }

    @JvmStatic
    fun interval(millis: Long, runnable: ()->Unit): IntervalHandler {
        val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
        val wrapper = RunnableWrapper()
        wrapper.runnable = Runnable {
            runnable()
            handler.postDelayed(wrapper.runnable, millis)
        }
        handler.postDelayed(wrapper.runnable, millis)
        return IntervalHandler(handler, wrapper)
    }

    internal class RunnableWrapper {
        lateinit var runnable: Runnable
    }
}
