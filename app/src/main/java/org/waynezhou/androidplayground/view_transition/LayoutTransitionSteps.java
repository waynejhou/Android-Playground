package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;

import java.util.HashMap;
import java.util.Map;

public class LayoutTransitionStep extends ViewTransitionSteps {
    public static class ViewStep extends ViewTransitionSteps.ViewStep{
        public final HashMap<String, ValueGetter[]> propertyMap;
        public ViewStep(HashMap<String, ValueGetter[]> propertyMap){
            this.propertyMap = propertyMap;
        }

        @Override
        public int size() {
            return propertyMap.size();
        }

        @Override
        public Animator generateAnimator(LayoutAnimatorArgs args) {
            Animator vAnimator = new ValueAnimator();
            vAnimator.setDuration(args.duration);
            vAnimator.setInterpolator(args.interpolator);

            PropertyValuesHolder[] holders = new PropertyValuesHolder[size()];
            int idx = 0;
            for (Map.Entry<String, ValueGetter[]> entry: propertyMap.entrySet()) {

                for(int i = 0;i )

                vAnimator.setValues(new PropertyValuesHolder[]{

                });
                arr[idx++] = vAnimator;
            }
        }
        @Override
        public Runnable[] generateRunnable() {
            return new Animator[0];
        }
    }
}