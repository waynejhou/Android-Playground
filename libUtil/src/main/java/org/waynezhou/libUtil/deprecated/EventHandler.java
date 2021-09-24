package org.waynezhou.libUtil.deprecated;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class EventHandler<TEventArgs> {
    private final List<EventListener<TEventArgs>> listeners = new ArrayList<>();

    public void add(@NonNull EventListener<TEventArgs> listener) {
        listeners.add(listener);
    }

    public void inject(@NonNull EventListener<TEventArgs> listener) {
        listeners.add(0, listener);
    }

    public void remove(@NonNull EventListener<TEventArgs> listener) {
        listeners.remove(listener);
    }

    public void clear(){
        listeners.clear();
    }

    public void invoke(TEventArgs e) {
        for (EventListener<TEventArgs> listener : listeners) {
            listener.invoke(e);
        }
    }

    public <TRouteToEventArgs> void routeTo(
            @NonNull EventHandler<TRouteToEventArgs> handler,
            @NonNull EventArgsRouteConverter<TEventArgs, TRouteToEventArgs> converter
    ) {
        routeTo(handler, converter, false);
    }

    public <TRouteToEventArgs> void routeTo(
            @NonNull EventHandler<TRouteToEventArgs> handler,
            @NonNull EventArgsRouteConverter<TEventArgs, TRouteToEventArgs> converter,
            boolean useInject
    ) {
        if (useInject) {
            inject((e) -> handler.invoke(converter.convert(e)));
        } else {
            add((e) -> handler.invoke(converter.convert(e)));
        }
    }
}
