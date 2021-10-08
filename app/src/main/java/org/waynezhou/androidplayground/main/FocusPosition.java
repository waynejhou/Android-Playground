package org.waynezhou.androidplayground.main;

import org.waynezhou.libUtil.EnumClass;

final class FocusPositions extends EnumClass.Int {
    static final FocusPositions FOCUS_TOP = new FocusPositions(0, "Focus Top");
    static final FocusPositions FOCUS_MIDDLE = new FocusPositions(1, "Focus Middle");
    static final FocusPositions FOCUS_BOTTOM = new FocusPositions(2, "Focus Bottom");

    private FocusPositions(int identifier, String statement) {
        super(identifier, statement);
    }
}