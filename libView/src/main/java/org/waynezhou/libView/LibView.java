package org.waynezhou.libView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import org.waynezhou.libUtil.reflection.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class LibView {
    private LibView() {
    }
    
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @NonNull
    public static <TViewBinding extends ViewBinding> TViewBinding inflate(
            @NonNull LayoutInflater layoutInflater,
            @NonNull Class<TViewBinding> viewBindingType,
            @Nullable ViewGroup container,
            @NonNull Boolean attachToParent
    ) throws ReflectionException {
        try {
            Method inflateMethod = viewBindingType.getMethod(
                    "inflate",
                    LayoutInflater.class,
                    ViewGroup.class,
                    boolean.class
            );
            return (TViewBinding) (inflateMethod.invoke(null, layoutInflater, container, attachToParent));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }
}
