package org.waynezhou.libUtil;

import java.util.function.Predicate;

public class Delegate {
    public static final Runnable DoNothing = ()->{};
    public static final Predicate<?> AlwaysTrue = $->true;
    public static final Predicate<?> AlwaysFalse = $->false;
}
