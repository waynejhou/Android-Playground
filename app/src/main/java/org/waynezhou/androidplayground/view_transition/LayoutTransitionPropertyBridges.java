package org.waynezhou.androidplayground.view_transition;

import java.util.HashMap;

public final class LayoutTransitionPropertyBridges {
    private LayoutTransitionPropertyBridges(){}
    public static final String PROP_WIDTH = "width";
    public static final LayoutTransitionPropertyBridge width = new LayoutTransitionPropertyBridge.Width();
    public static final String PROP_HEIGHT = "height";
    public static final LayoutTransitionPropertyBridge height = new LayoutTransitionPropertyBridge.Height();
    public static final String PROP_LEFT = "left";
    public static final LayoutTransitionPropertyBridge left = new LayoutTransitionPropertyBridge.Left();
    public static final String PROP_RIGHT = "right";
    public static final LayoutTransitionPropertyBridge right = new LayoutTransitionPropertyBridge.Right();
    public static final String PROP_TOP = "top";
    public static final LayoutTransitionPropertyBridge top = new LayoutTransitionPropertyBridge.Top();
    public static final String PROP_BOTTOM = "bottom";
    public static final LayoutTransitionPropertyBridge bottom = new LayoutTransitionPropertyBridge.Bottom();
    public static final String PROP_GRAVITY = "gravity";
    public static final LayoutTransitionPropertyBridge gravity = new LayoutTransitionPropertyBridge.Gravity();
    public static final HashMap<String, LayoutTransitionPropertyBridge> bridges = new HashMap<String, LayoutTransitionPropertyBridge>(){{
        put(PROP_WIDTH, width);
        put(PROP_HEIGHT, height);
        put(PROP_LEFT, left);
        put(PROP_RIGHT, right);
        put(PROP_TOP, top);
        put(PROP_BOTTOM, bottom);
        put(PROP_GRAVITY, gravity);
    }};
}
