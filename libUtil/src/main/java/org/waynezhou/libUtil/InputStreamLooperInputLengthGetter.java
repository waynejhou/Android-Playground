package org.waynezhou.libUtil;

import java.io.InputStream;

@FunctionalInterface
public interface InputStreamLooperInputLengthGetter<T extends Exception> {
    int getLength(InputStream in) throws T;
}
