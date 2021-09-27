package org.waynezhou.androidplayground.layout;

import android.view.View;

public final class LayoutProperties {
    private LayoutProperties() {
    }

    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_ROTATION = "rotation";

    public static <TValueHolder> FloatGetter<TValueHolder> GET_VIEW_WIDTH(View view) {
        return $ -> (float) view.getWidth();
    }
    public static <TValueHolder> FloatGetter<TValueHolder> GET_VIEW_HEIGHT(View view) {
        return $ -> (float) view.getHeight();
    }
    public static <TValueHolder> FloatGetter<TValueHolder> GET_VIEW_LEFT(View view) {
        return $ -> (float) view.getLeft();
    }
    public static <TValueHolder> FloatGetter<TValueHolder> GET_VIEW_TOP(View view) {
        return $ -> (float) view.getTop();
    }
    public static <TValueHolder> FloatGetter<TValueHolder> GET_VIEW_ROTATION(View view) {
        return $ -> view.getRotation();
    }
}
