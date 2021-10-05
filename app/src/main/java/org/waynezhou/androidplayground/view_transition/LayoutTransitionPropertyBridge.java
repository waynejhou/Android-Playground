package org.waynezhou.androidplayground.view_transition;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class LayoutTransitionPropertyBridge {
    protected LayoutTransitionPropertyBridge() {
    }

    public abstract float get(View view);

    public abstract void set(View view, float value);

    public static class Width extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getWidth();
        }

        @Override
        public void set(View view, float value) {
            view.getLayoutParams().width = Math.round(value);
        }
    }

    public static class Height extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getHeight();
        }

        @Override
        public void set(View view, float value) {
            view.getLayoutParams().height = Math.round(value);
        }
    }

    public static class Left extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getLeft();
        }

        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).leftMargin = Math.round(value);
        }
    }

    public static class Right extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getRight();
        }

        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).rightMargin = Math.round(value);
        }
    }

    public static class Top extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getTop();
        }

        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).topMargin = Math.round(value);
        }
    }

    public static class Bottom extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            return view.getBottom();
        }

        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).bottomMargin = Math.round(value);
        }
    }

    public static class Gravity extends LayoutTransitionPropertyBridge {

        @Override
        public float get(View view) {
            if(view.getLayoutParams() instanceof LinearLayout.LayoutParams){
                return ((LinearLayout.LayoutParams)view.getLayoutParams()).gravity;
            }else if(view.getLayoutParams() instanceof FrameLayout.LayoutParams){
                return ((FrameLayout.LayoutParams)view.getLayoutParams()).gravity;
            }
            assert false;
            return 0f;
        }

        @Override
        public void set(View view, float value) {
            if(view.getLayoutParams() instanceof LinearLayout.LayoutParams){
                ((LinearLayout.LayoutParams)view.getLayoutParams()).gravity = (int)value;
            }else if(view.getLayoutParams() instanceof FrameLayout.LayoutParams){
                ((FrameLayout.LayoutParams)view.getLayoutParams()).gravity = (int)value;
            }
            assert false;
        }
    }

}
