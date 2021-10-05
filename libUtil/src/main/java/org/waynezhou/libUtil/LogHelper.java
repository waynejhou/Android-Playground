package org.waynezhou.libUtil;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.waynezhou.libUtil.reflection.Reflection;
import org.waynezhou.libUtil.reflection.ReflectionException;

import java.util.Locale;

public final class LogHelper {
    private LogHelper() {
    }

    @NonNull
    private static String packageName = "org.waynezhou.libUtil";
    @NonNull
    private static String tag = "Log";

    static {
        try {
            packageName = Reflection
                    .getCurrentApplication()
                    .getApplicationInfo().packageName;
            String[] s = packageName.split("\\.");
            tag = s[s.length - 1] + tag;
        } catch (ReflectionException e) {
            Log.e(tag, "exception", e);
        }
    }

    @FunctionalInterface
    private interface LogMethod {
        void log(String tag, String msg);
    }

    @FunctionalInterface
    private interface LogThrowableMethod {
        void log(String tag, String msg, Throwable tr);
    }


    private static void log(LogMethod method, Object msg) {
        StackTraceElement callerInfo = Thread.currentThread().getStackTrace()[4];
        String objMsg = "null";
        if (msg != null) objMsg = msg.toString();
        objMsg = callerInfo.getClassName().replaceFirst(packageName, "")
                + "::"
                + callerInfo.getMethodName() + "(" + callerInfo.getFileName() + ":" + callerInfo.getLineNumber() + ")"
                + (objMsg.isEmpty()? "" : " - ")
                + objMsg;
        method.log(tag, objMsg);
    }

    private static void log(LogThrowableMethod method, Object msg, Throwable tr) {
        StackTraceElement callerInfo = Thread.currentThread().getStackTrace()[4];
        String objMsg = "null";
        if (msg != null) objMsg = msg.toString();
        objMsg = callerInfo.getClassName().replaceFirst(packageName, "")
                + "::"
                + callerInfo.getMethodName() + "(" + callerInfo.getFileName() + ":" + callerInfo.getLineNumber() + ")"
                + "\n"
                + objMsg;
        method.log(tag, objMsg, tr);
    }

    public static void ie(Condition isError, String format, Object... args){
        if(test(isError)){
            log(Log::e, String.format(Locale.TRADITIONAL_CHINESE, format, args));
        }else{
            log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, args));
        }
    }
    public static void ie(Condition isError, @Nullable Object msg){
        if(test(isError)){
            log(Log::e, msg);
        }else{
            log(Log::i, msg);
        }
    }

    public static void d(String format, Object... args){
        log(Log::d, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }

    public static void d(@Nullable Object msg) {
        log(Log::d, msg);
    }

    @Deprecated
    public static void d(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::d, msg, tr);
    }

    public static void e(@Nullable Object msg) {
        log(Log::e, msg);
    }

    public static void e(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::e, msg, tr);
    }

    public static void i(){
        log(Log::i, "");
    }

    public static void i(String format, Object... args){
        log(Log::i, String.format(Locale.TRADITIONAL_CHINESE, format, args));
    }

    public static void i(@Nullable Object msg) {
        log(Log::i, msg);
    }

    @Deprecated
    public static void i(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::i, msg, tr);
    }

    public static void v(@Nullable Object msg) {
        log(Log::v, msg);
    }

    @Deprecated
    public static void v(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::v, msg, tr);
    }

    public static void w(@Nullable Object msg) {
        log(Log::w, msg);
    }

    @Deprecated
    public static void w(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::w, msg, tr);
    }

    public static void wtf(@Nullable Object msg) {
        log(Log::wtf, msg);
    }

    @Deprecated
    public static void wtf(@Nullable Object msg, @NonNull Throwable tr) {
        log(Log::wtf, msg, tr);
    }

    private static boolean test(Condition condition){
        return condition.test();
    }
    @FunctionalInterface
    public interface Condition{
        boolean test();
    }
}