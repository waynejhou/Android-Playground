package org.waynezhou.androidplayground.main;

import org.waynezhou.libUtil.enumclass.EnumClass;

final class FocusPosition extends EnumClass.Int {
    static final FocusPosition FOCUS_TOP = new FocusPosition(0, "Focus Top");
    static final FocusPosition FOCUS_MIDDLE = new FocusPosition(1, "Focus Middle");
    static final FocusPosition FOCUS_BOTTOM = new FocusPosition(2, "Focus Bottom");

    private FocusPosition(int identifier, String statement) {
        super(identifier, statement);
    }
}