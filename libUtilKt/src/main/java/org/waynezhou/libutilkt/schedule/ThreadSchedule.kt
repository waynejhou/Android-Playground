package org.waynezhou.libutilkt.schedule

import org.waynezhou.libutilkt.log.LogHelper
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ThreadSchedule {

    @JvmStatic
    fun run(runnable: () -> Unit): Thread {
        val thd = Thread(runnable)
        thd.start()
        return thd
    }

    @JvmStatic
    fun timeOut(millis: Long, runnable: () -> Unit): TimeoutHandler {
        return TimeoutHandler(run {
            try {
                Thread.sleep(millis)
            } catch (e: InterruptedException) {
                LogHelper.e("Timeout Thread Interrupted", e)
            }
            runnable()
        })
    }

    @JvmStatic
    fun interval(millis: Long, runnable: Runnable): IntervalHandler {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate(runnable, 0, millis, TimeUnit.MILLISECONDS)
        return IntervalHandler(executor)
    }

}