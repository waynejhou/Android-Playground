package org.waynezhou.androidplayground.view_transition;

import java.util.HashMap;

public final class TransformTransitionPropertyBridges {
    private TransformTransitionPropertyBridges(){}
    public static final String PROP_ROTATION = "rotation";
    public static final TransformTransitionPropertyBridge rotation = new TransformTransitionPropertyBridge.Rotation();
    public static final String PROP_PIVOT_X = "pivotX";
    public static final TransformTransitionPropertyBridge pivotX = new TransformTransitionPropertyBridge.PivotX();
    public static final String PROP_PIVOT_Y = "pivotY";
    public static final TransformTransitionPropertyBridge pivotY = new TransformTransitionPropertyBridge.PivotY();
    public static final String PROP_Scale_X = "scaleX";
    public static final TransformTransitionPropertyBridge scaleX = new TransformTransitionPropertyBridge.ScaleX();
    public static final String PROP_Scale_Y = "scaleY";
    public static final TransformTransitionPropertyBridge scaleY = new TransformTransitionPropertyBridge.ScaleY();
    public static final String PROP_Z = "z";
    public static final TransformTransitionPropertyBridge z = new TransformTransitionPropertyBridge.Z();
    public static final HashMap<String, TransformTransitionPropertyBridge> bridges = new HashMap<String, TransformTransitionPropertyBridge>(){{
        put(PROP_ROTATION, rotation);
        put(PROP_PIVOT_X, pivotX);
        put(PROP_PIVOT_Y, pivotY);
        put(PROP_Scale_X, scaleX);
        put(PROP_Scale_Y, scaleY);
        put(PROP_Z, z);
    }};
}
