package org.waynezhou.libUtil.deprecated;

@Deprecated
public interface EventActionHandlerSelector<TEventPack extends EventPack<TEventPack>, TEventArgs, TEventHandler extends EventHandler<TEventArgs>> {
    TEventHandler select(TEventPack pack);
}
