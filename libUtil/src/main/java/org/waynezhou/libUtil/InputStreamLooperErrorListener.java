package org.waynezhou.libUtil;

@FunctionalInterface
public interface InputStreamLooperErrorListener {
    void invoke(Exception e);
}

