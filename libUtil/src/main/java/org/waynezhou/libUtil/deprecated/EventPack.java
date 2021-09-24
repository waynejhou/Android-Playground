package org.waynezhou.libUtil.deprecated;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.LogHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class EventPack <TEventPack extends EventPack<TEventPack>> {
    private final List<Method> handlers = new ArrayList<>();

    protected EventPack() {
        for (Method m : getClass().getDeclaredMethods()) {
            if (m.getReturnType().isAssignableFrom(EventHandler.class)
                    && m.getParameterTypes().length == 0) {
                handlers.add(m);
            }
        }
    }


    @SuppressWarnings("ConstantConditions")
    public void addAll(@NonNull EventGenericListener listener) {
        for (Method m : handlers) {
            String fname = m.getName().replaceFirst("get", "");
            String head = fname.substring(0, 1).toLowerCase();
            String tail = fname.substring(1);
            String name = head + tail;
            try {
                EventHandler<?> handler = (EventHandler<?>) m.invoke(this);
                handler.add(($) -> listener.invoke(name));
            } catch (IllegalAccessException | InvocationTargetException e) {
                LogHelper.e("exception", e);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void injectAll(@NonNull EventGenericListener listener) {
        for (Method m : handlers) {
            String fName = m.getName().replaceFirst("get", "");
            String head = fName.substring(0, 1).toLowerCase();
            String tail = fName.substring(1);
            String name = head + tail;
            try {
                EventHandler<?> handler = (EventHandler<?>) m.invoke(this);
                handler.inject(($) -> listener.invoke(name));
            } catch (IllegalAccessException | InvocationTargetException e) {
                LogHelper.e("exception", e);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void clear(){
        for (Method m : handlers) {
            try {
                EventHandler<?> handler = (EventHandler<?>) m.invoke(this);
                handler.clear();
            } catch (IllegalAccessException | InvocationTargetException e) {
                LogHelper.e("exception", e);
            }
        }
    }

    public TEventPack apply(EventPackApplier<TEventPack> applier){
        TEventPack pack = (TEventPack)this;
        applier.apply(pack);
        return pack;
    }
}

