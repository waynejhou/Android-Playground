package org.waynezhou.androidplayground.main;

import org.waynezhou.libUtil.enumclass.EnumClass;

final class LayoutChangedReason extends EnumClass.Int {
    public static final LayoutChangedReason ROTATION_REQUESTED = new LayoutChangedReason("Rotation Requested");
    public static final LayoutChangedReason CONTENT_VIEW_SET = new LayoutChangedReason("Content View Set");

    private LayoutChangedReason(String identifier) {
        super(identifier);
    }
}
