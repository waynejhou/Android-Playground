package org.waynezhou.libUtil.event;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class EventHolder<TEventArgs>{
    private class Listener {
        public final boolean isOnce;
        public final EventListener<TEventArgs> listener;
        private Listener(boolean isOnce, EventListener<TEventArgs> listener) {
            this.isOnce = isOnce;
            this.listener = listener;
        }
    }
    private final List<Listener> listeners = new ArrayList<>();
    ListenerToken addOnceListener(@NonNull EventListener<TEventArgs> listener) {
        Listener l = new Listener(true, listener);
        ListenerToken token = new ListenerToken(l.hashCode());
        listeners.add(l);
        return token;
    }
    ListenerToken addOnListener(@NonNull EventListener<TEventArgs> listener){
        Listener l = new Listener(false, listener);
        ListenerToken token = new ListenerToken(l.hashCode());
        listeners.add(l);
        return token;
    }
    void removeListener(@NonNull EventListener<TEventArgs> listener) {
        listeners.removeIf(x->x.listener == listener);
    }
    void removeListener(@NonNull ListenerToken token) {
        listeners.removeIf(x->x.hashCode() == token.hashcode);
    }
    void invoke(TEventArgs e) {
        List<Integer> removedIdx = new ArrayList<>();
        int c = 0;
        for (Listener listener : listeners) {
            listener.listener.invoke(e);
            if(listener.isOnce){
                removedIdx.add(c);
            }
            c++;
        }
        for(int idx: removedIdx){
            listeners.remove(idx);
        }
    }
    public class ListenerToken{
        public final int hashcode;
        public ListenerToken(int hashcode) {
            this.hashcode = hashcode;
        }
    }
}