package org.waynezhou.libUtil;

@FunctionalInterface
public interface GuarantorChecker<T> {
    boolean check(T t);
}

