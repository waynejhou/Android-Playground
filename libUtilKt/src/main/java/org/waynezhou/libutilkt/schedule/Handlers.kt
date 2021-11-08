package org.waynezhou.libutilkt.schedule

import android.os.Handler
import java.util.concurrent.ScheduledExecutorService

class TimeoutHandler {
    private val cancelAction: () -> Unit

    internal constructor(thread: Thread) {
        cancelAction = { if (!thread.isInterrupted) thread.interrupt() }
    }

    internal constructor(
        handler: Handler,
        runnable: () -> Unit
    ) {
        cancelAction = { handler.removeCallbacks(runnable) }
    }

    fun cancel() {
        cancelAction()
    }
}


class IntervalHandler {
    private val stopAction: ()->Unit

    internal constructor(scheduledExecutorService: ScheduledExecutorService) {
        stopAction = {
            if (!scheduledExecutorService.isShutdown) {
                scheduledExecutorService.shutdown()
            }
        }
    }

    internal constructor(
        handler: Handler,
        wrapper: HandlerSchedule.RunnableWrapper
    ) {
        stopAction = { handler.removeCallbacks(wrapper.runnable) }
    }

    fun shutdown() {
        stopAction()
    }
}
