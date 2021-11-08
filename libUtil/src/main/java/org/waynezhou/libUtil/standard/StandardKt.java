package org.waynezhou.libUtil;

import androidx.annotation.NonNull;

public final class StandardKt {
    private StandardKt(){}
    public static <T> T apply(T t, RunBlock<T> block){
        block.run(t);
        return t;
    }
    public static <T> String toStringNullOnNull(T t){
        if(t==null) return "null";
        return t.toString();
    }
    public static <T> String toStringEmptyOnNull(T t){
        if(t==null) return "";
        return t.toString();
    }
    public static <T> void checkNullRun(T it, CheckNullRunBlock<T> block){
        if(it==null) return;
        block.run(it);
    }
    public static <T, TOut> TOut checkNullRet(T it, CheckNullGetter<T, TOut> getter){
        if(it==null) return null;
        return getter.getFrom(it);
    }

    @FunctionalInterface
    public interface RunBlock<T> {
        void run(T it);
    }

    @FunctionalInterface
    public interface CheckNullGetter<T, TOut> {
        TOut getFrom(T t);
    }

    @FunctionalInterface
    public interface CheckNullRunBlock<T> {
        void run(@NonNull T it);
    }

}

