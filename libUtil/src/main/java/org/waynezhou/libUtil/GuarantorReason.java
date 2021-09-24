package org.waynezhou.libUtil;

@FunctionalInterface
public interface GuarantorReason<T, TReason> {
    TReason getReason(T t);
}
