package org.waynezhou.libUtil.deprecated;

@Deprecated
@FunctionalInterface
public interface EventPackApplier<TEventPack extends EventPack<TEventPack>> {
    void apply(TEventPack pack);
}
