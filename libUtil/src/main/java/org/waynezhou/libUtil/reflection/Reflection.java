package org.waynezhou.libUtil.reflection;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflection {
    private Reflection(){}
    @SuppressWarnings("ConstantConditions")
    @SuppressLint("PrivateApi")
    @NonNull
    public static Application getCurrentApplication() throws ReflectionException {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method method = activityThreadClass.getMethod("currentApplication");
            return (Application)(method.invoke(null));
        }catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }
}
