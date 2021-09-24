package org.waynezhou.libUtil.deprecated;

@Deprecated
@FunctionalInterface
public interface EventGenericListener {
    void invoke(String handlerName);
}
