package org.waynezhou.libUtil.delegate;

import java.util.function.Predicate;

public class DelegateUtils {
    public static final Runnable NothingRunnable = ()->{};
    public static final java.util.function.Consumer<?> NothingConsumer = $->{};
    public static final Predicate<?> AlwaysTrue = $->true;
    public static final Predicate<?> AlwaysFalse = $->false;
}
