package org.waynezhou.libUtil.deprecated;

@Deprecated
@FunctionalInterface
public interface EventArgsRouteConverter<TEventArgs, TEventRouteToArgs> {
    TEventRouteToArgs convert(TEventArgs e);
}
