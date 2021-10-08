package org.waynezhou.androidplayground.main;

import org.waynezhou.libUtil.EnumClass;

final class ControlSignal extends EnumClass.Int {
    public static final ControlSignal CTRL_ROTATE = new ControlSignal("Rotate");
    public static final ControlSignal CTRL_ROTATE_PORT = new ControlSignal("Rotate Port");
    public static final ControlSignal CTRL_ROTATE_LAND = new ControlSignal("Rotate Land");
    public static final ControlSignal CTRL_LAYOUT_PORT_TOP_FULL = new ControlSignal("Layout Port Top Full");
    public static final ControlSignal CTRL_LAYOUT_PORT_STD = new ControlSignal("Layout Port Std");
    public static final ControlSignal CTRL_LAYOUT_PORT_TOP_HALF = new ControlSignal("Layout Top Half");
    public static final ControlSignal CTRL_MEDIA_NEXT_SECTION = new ControlSignal("Media Next Section");
    public static final ControlSignal CTRL_MEDIA_PREV_SECTION = new ControlSignal("Media Prev Section");
    public static final ControlSignal CTRL_FOCUS_NEXT = new ControlSignal("Focus Next");
    public static final ControlSignal CTRL_FOCUS_PREV = new ControlSignal("Focus Prev");
    public static final ControlSignal CTRL_FOCUS_TOP = new ControlSignal("Focus Top");
    public static final ControlSignal CTRL_FOCUS_MIDDLE = new ControlSignal("Focus Middle");
    public static final ControlSignal CTRL_FOCUS_BOTTOM = new ControlSignal("Focus Bottom");
    private ControlSignal(String statement) {
        super(statement);
    }
}