package org.waynezhou.libUtil.reflection;

public final class ReflectionException extends Exception {
    public ReflectionException(Exception ex) {
        super("Something went wrong which may be caused by reflection.", ex);
    }
}
