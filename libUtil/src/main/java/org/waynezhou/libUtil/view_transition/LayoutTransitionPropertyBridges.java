package org.waynezhou.androidplayground.view_transition;

import java.util.HashMap;

public final class LayoutTransitionPropertyBridges {
    private LayoutTransitionPropertyBridges(){}
    public static final String PROP_WID = "width";
    public static final LayoutTransitionPropertyBridge width = new LayoutTransitionPropertyBridge.Width();
    public static final String PROP_HEI = "height";
    public static final LayoutTransitionPropertyBridge height = new LayoutTransitionPropertyBridge.Height();
    public static final String PROP_LFT = "left";
    public static final LayoutTransitionPropertyBridge left = new LayoutTransitionPropertyBridge.Left();
    public static final String PROP_RGT = "right";
    public static final LayoutTransitionPropertyBridge right = new LayoutTransitionPropertyBridge.Right();
    public static final String PROP_TOP = "top";
    public static final LayoutTransitionPropertyBridge top = new LayoutTransitionPropertyBridge.Top();
    public static final String PROP_BTM = "bottom";
    public static final LayoutTransitionPropertyBridge bottom = new LayoutTransitionPropertyBridge.Bottom();
    public static final String PROP_GVT = "gravity";
    public static final LayoutTransitionPropertyBridge gravity = new LayoutTransitionPropertyBridge.Gravity();

    /**
    public static final String PROP_ROT = "rotation";
    public static final LayoutTransitionPropertyBridge rotation = new LayoutTransitionPropertyBridge.Rotation();
    public static final String PROP_PTX = "pivotX";
    public static final LayoutTransitionPropertyBridge pivotX = new LayoutTransitionPropertyBridge.PivotX();
    public static final String PROP_PTY = "pivotY";
    public static final LayoutTransitionPropertyBridge pivotY = new LayoutTransitionPropertyBridge.PivotY();
    public static final String PROP_SCX = "scaleX";
    public static final LayoutTransitionPropertyBridge scaleX = new LayoutTransitionPropertyBridge.ScaleX();
    public static final String PROP_SCY = "scaleY";
    public static final LayoutTransitionPropertyBridge scaleY = new LayoutTransitionPropertyBridge.ScaleY();
    public static final String PROP_ZZZ = "z";
    public static final LayoutTransitionPropertyBridge z = new LayoutTransitionPropertyBridge.Z();
    public static final String PROP_TRX = "translationX";
    public static final LayoutTransitionPropertyBridge translationX = new LayoutTransitionPropertyBridge.TranslationX();
    public static final String PROP_TRY = "translationY";
    public static final LayoutTransitionPropertyBridge translationY = new LayoutTransitionPropertyBridge.TranslationY();
    */
    public static final HashMap<String, LayoutTransitionPropertyBridge> bridges = new HashMap<String, LayoutTransitionPropertyBridge>(){{
        put(PROP_WID, width);
        put(PROP_HEI, height);
        put(PROP_LFT, left);
        put(PROP_RGT, right);
        put(PROP_TOP, top);
        put(PROP_BTM, bottom);
        put(PROP_GVT, gravity);
        /**
        put(PROP_ROT, rotation);
        put(PROP_PTX, pivotX);
        put(PROP_PTY, pivotY);
        put(PROP_SCX, scaleX);
        put(PROP_SCY, scaleY);
        put(PROP_ZZZ, z);
        put(PROP_TRX, translationX);
        put(PROP_TRY, translationY);
         */
    }};
}
