package org.waynezhou.libUtil.event;

import androidx.annotation.NonNull;

public class EventHandler<TEventArgs> {
    @NonNull
    private final EventHolder<TEventArgs> holder = new EventHolder<>();
    
    @NonNull
    public EventHolder<TEventArgs>.ListenerToken on(@NonNull EventListener<TEventArgs> listener) {
        return holder.addOnListener(listener);
    }
    
    @NonNull
    public EventHolder<TEventArgs>.ListenerToken once(@NonNull EventListener<TEventArgs> listener) {
        return holder.addOnceListener(listener);
    }
    
    public void removeListener(@NonNull EventListener<TEventArgs> listener) {
        holder.removeListener(listener);
    }
    
    public void removeListener(@NonNull EventHolder<TEventArgs>.ListenerToken token) {
        holder.removeListener(token);
    }
    
    public void invoke(TEventArgs e) {
        holder.invoke(e, () -> false);
    }
    
    public void invoke(
      TEventArgs e,
      @NonNull Condition breakAction
    ) {
        holder.invoke(e, breakAction);
    }
}
