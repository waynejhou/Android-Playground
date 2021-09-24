package org.waynezhou.libUtil.event;


@FunctionalInterface
public interface EventListener<TEventArgs> {
    void invoke(TEventArgs e);
}

