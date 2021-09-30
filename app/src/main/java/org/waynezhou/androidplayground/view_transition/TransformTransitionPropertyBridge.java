package org.waynezhou.androidplayground.view_transition;

import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

public abstract class TransformTransitionPropertyBridge {
    protected TransformTransitionPropertyBridge() {
    }

    public abstract float get(View view);

    public abstract void set(View view, float value);

    public static class Rotation extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getRotation();
        }

        @Override
        public void set(View view, float value) {
            view.setRotation(value);
        }
    }

    public static class PivotX extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getPivotX();
        }

        @Override
        public void set(View view, float value) {
            view.setPivotX(value);
        }
    }

    public static class PivotY extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getPivotY();
        }

        @Override
        public void set(View view, float value) {
            view.setPivotY(value);
        }
    }

    public static class ScaleX extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getScaleX();
        }

        @Override
        public void set(View view, float value) {
            view.setScaleX(value);
        }
    }

    public static class ScaleY extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getScaleY();
        }

        @Override
        public void set(View view, float value) {
            view.setScaleY(value);
        }
    }

    public static class Z extends TransformTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getZ();
        }

        @Override
        public void set(View view, float value) {
            view.setZ(value);
        }
    }
}
