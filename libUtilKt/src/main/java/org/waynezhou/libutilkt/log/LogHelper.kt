package org.waynezhou.libutilkt.log_helper

import android.util.Log
import java.util.*


typealias LogMethod = (tag: String?, msg: String) -> Unit
typealias LogThrowableMethod = (tag: String?, msg: String?, tr: Throwable?) -> Unit

@Suppress("unused")
object LogHelper {
    private fun log(log: LogMethod, msg: Any?) {
        val callerInfo = Thread.currentThread().stackTrace[4]
        var objMsg = msg?.toString() ?: "null"
        objMsg =
            callerInfo.run {
                "$methodName($fileName:$lineNumber)${(if (objMsg.isEmpty()) "" else " - ")}$objMsg"
            }
        log("LogHelper", objMsg)
    }

    private fun log(log: LogThrowableMethod, msg: Any?, tr: Throwable) {
        val callerInfo = Thread.currentThread().stackTrace[4]
        var objMsg = msg?.toString() ?: "null"
        objMsg =
            (callerInfo.methodName + "(" + callerInfo.fileName + ":" + callerInfo.lineNumber + ")"
                    + (if (objMsg.isEmpty()) "" else " - ")
                    + objMsg)
        log("LogHelper", objMsg, tr)
    }

    fun ie(isError: () -> Boolean, format: String, vararg args: Any?) {
        if (isError())
            log(Log::e, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
        else
            log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun ie(isError: () -> Boolean, msg: Any?) {
        if (isError())
            log(Log::e, msg)
        else
            log(Log::i, msg)
    }

    fun d(format: String, vararg args: Any?) {
        log(Log::d, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun d(msg: Any?) {
        log(Log::d, msg)
    }

    fun d(tr: Throwable, msg: Any?) {
        log(Log::d, msg, tr)
    }

    fun e(msg: Any?) {
        log(Log::e, msg)
    }

    fun e(format: String, vararg args: Any?) {
        log(Log::e, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun e(tr: Throwable, msg: Any?) {
        log(Log::e, msg, tr)
    }

    fun i() {
        log(Log::i, "")
    }

    fun i(format: String, vararg args: Any?) {
        log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun i(msg: Any?) {
        log(Log::i, msg)
    }

    fun i(tr: Throwable, msg: Any?) {
        log(Log::i, msg, tr)
    }

    fun v(msg: Any?) {
        log(Log::v, msg)
    }

    fun v(format: String, vararg args: Any?) {
        log(Log::v, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun v(tr: Throwable, msg: Any?) {
        log(Log::v, msg, tr)
    }

    fun w(msg: Any?) {
        log(Log::w, msg)
    }

    fun w(format: String, vararg args: Any?) {
        log(Log::w, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun w(tr: Throwable, msg: Any?) {
        log(Log::w, msg, tr)
    }

    fun wtf(msg: Any?) {
        log(Log::wtf, msg)
    }

    fun wtf(format: String, vararg args: Any?) {
        log(Log::wtf, String.format(Locale.TRADITIONAL_CHINESE, format, *args))
    }

    fun wtf(tr: Throwable, msg: Any?) {
        log(Log::wtf, msg, tr)
    }

}
