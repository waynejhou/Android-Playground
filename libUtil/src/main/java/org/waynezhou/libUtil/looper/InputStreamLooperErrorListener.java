package org.waynezhou.libUtil.input_stream_looper;

@Deprecated
@FunctionalInterface
public interface InputStreamLooperErrorListener {
    void invoke(Exception e);
}

