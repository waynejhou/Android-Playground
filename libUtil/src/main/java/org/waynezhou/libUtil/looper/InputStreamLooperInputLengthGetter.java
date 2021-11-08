package org.waynezhou.libUtil.looper;

import java.io.InputStream;

@Deprecated
@FunctionalInterface
public interface InputStreamLooperInputLengthGetter<T extends Exception> {
    int getLength(InputStream in) throws T;
}
