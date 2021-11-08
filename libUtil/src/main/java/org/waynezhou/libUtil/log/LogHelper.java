package org.waynezhou.libUtil.log;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public final class LogHelper {
    private LogHelper() {
    }
    
    @FunctionalInterface
    private interface LogMethod {
        void log(String tag, String msg);
    }
    
    @FunctionalInterface
    private interface LogThrowableMethod {
        void log(String tag, String msg, Throwable tr);
    }
    
    
    private static void log(@NonNull LogMethod method, @Nullable Object msg) {
        final StackTraceElement callerInfo = Thread.currentThread().getStackTrace()[4];
        String objMsg = msg != null ? msg.toString() : "null";
        objMsg = callerInfo.getMethodName() + "(" + callerInfo.getFileName() + ":" + callerInfo.getLineNumber() + ")"
          + (objMsg.isEmpty() ? "" : " - ")
          + objMsg;
        method.log("LogHelper", objMsg);
    }
    
    private static void log(@NonNull LogThrowableMethod method, @Nullable Object msg, @NonNull Throwable tr) {
        final StackTraceElement callerInfo = Thread.currentThread().getStackTrace()[4];
        String objMsg = msg != null ? msg.toString() : "null";
        objMsg = callerInfo.getMethodName() + "(" + callerInfo.getFileName() + ":" + callerInfo.getLineNumber() + ")"
          + (objMsg.isEmpty() ? "" : " - ")
          + objMsg;
        method.log("LogHelper", objMsg, tr);
    }
    
    public static void ie(@NonNull Condition isError, @NonNull String format, @NonNull Object... args) {
        if (test(isError)) {
            log(Log::e, String.format(Locale.TRADITIONAL_CHINESE, format, args));
        } else {
            log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, args));
        }
    }
    
    public static void ie(@NonNull Condition isError, @Nullable Object msg) {
        if (test(isError)) {
            log(Log::e, msg);
        } else {
            log(Log::i, msg);
        }
    }
    
    public static void d(@NonNull String format, @NonNull Object... args) {
        log(Log::d, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void d(@Nullable Object msg) {
        log(Log::d, msg);
    }
    
    public static void d(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::d, msg, tr);
    }
    
    public static void e(@Nullable Object msg) {
        log(Log::e, msg);
    }
    
    public static void e(@NonNull String format, @NonNull Object... args) {
        log(Log::e, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void e(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::e, msg, tr);
    }
    
    public static void i() {
        log(Log::i, "");
    }
    
    public static void i(@Nullable Object msg) {
        log(Log::i, msg);
    }
    
    public static void i(@NonNull String format, @NonNull Object... args) {
        log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void i(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::i, msg, tr);
    }
    
    public static void v(@Nullable Object msg) {
        log(Log::v, msg);
    }
    
    public static void v(@NonNull String format, @NonNull Object... args) {
        log(Log::v, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void v(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::v, msg, tr);
    }
    
    public static void w(@Nullable Object msg) {
        log(Log::w, msg);
    }
    
    public static void w(@NonNull String format, @NonNull Object... args) {
        log(Log::w, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void w(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::w, msg, tr);
    }
    
    public static void wtf(@Nullable Object msg) {
        log(Log::wtf, msg);
    }
    
    public static void wtf(@NonNull String format, @NonNull Object... args) {
        log(Log::wtf, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }
    
    public static void wtf(@NonNull Throwable tr, @Nullable Object msg) {
        log(Log::wtf, msg, tr);
    }
    
    private static boolean test(Condition condition) {
        return condition.test();
    }
    
    @FunctionalInterface
    public interface Condition {
        boolean test();
    }
}