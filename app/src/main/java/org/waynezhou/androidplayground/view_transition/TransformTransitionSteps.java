package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import org.waynezhou.libUtil.DelegateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransformTransitionSteps extends ViewTransitionSteps {
    public static class ViewStep extends ViewTransitionSteps.ViewStep {
        public final HashMap<String, ValueGetter[]> propertyMap;

        public ViewStep(HashMap<String, ValueGetter[]> propertyMap) {
            this.propertyMap = propertyMap;
        }

        @SuppressLint("Recycle")
        @Override
        public Animator generateAnimator(View view, ViewAnimatorArgs args) {
            final ValueAnimator ret = new ValueAnimator();
            ret.setDuration(args.duration);
            ret.setInterpolator(args.interpolator);
            final PropertyValuesHolder[] holders = getHolders(propertyMap);
            ret.setValues(holders);
            ret.addUpdateListener(valueAnimator -> {
                for (PropertyValuesHolder holder : holders) {
                    setAnimatedValue(view, valueAnimator, holder);
                }
            });
            return ret;
        }

        @Override
        public Runnable generateRunnable(View view) {
            if (propertyMap.size() == 0) return DelegateUtils.NothingRunnable;
            final Pair<String, Float>[] valueMapList = getLastValueMapList(propertyMap);
            return ()->{
                for(Pair<String, Float> item: valueMapList){
                    @NonNull final TransformTransitionPropertyBridge bridge = Objects.requireNonNull(TransformTransitionPropertyBridges.bridges.get(item.first));
                    bridge.set(view, item.second);
                }
            };
        }
    }
}
