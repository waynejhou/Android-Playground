package org.waynezhou.libUtil.deprecated;

import org.waynezhou.libUtil.event.EventListener;

@Deprecated
public abstract class EventAction<TEventAction extends EventAction<TEventAction, TEventPack>, TEventPack extends EventPack<TEventPack>>  {

    protected final TEventPack eventPack;

    protected EventAction(TEventPack eventPack){
        this.eventPack = eventPack;
    }

    public <TEventArgs, TEventHandler extends EventHandler<TEventArgs>> TEventAction set(EventActionHandlerSelector<TEventPack, TEventArgs, TEventHandler> selector, EventListener<TEventArgs> listener){
        TEventHandler handler = selector.select(eventPack);
        handler.add(listener);
        return (TEventAction) this;
    }

    public abstract void fire();
}
