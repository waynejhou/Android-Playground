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

public class LayoutTransitionSteps extends ViewTransitionSteps {


    public static class ViewStep extends ViewTransitionSteps.ViewStep {
        public final HashMap<String, ValueGetter[]> propertyMap;

        public ViewStep(HashMap<String, ValueGetter[]> propertyMap) {
            this.propertyMap = propertyMap;
        }

        @SuppressLint("Recycle")
        @Override
        public <THolder> Animator generateAnimator(View view, THolder vHolder, ViewAnimatorArgs args) {
            final ValueAnimator ret = new ValueAnimator();
            ret.setDuration(args.duration);
            ret.setInterpolator(args.interpolator);
            final PropertyValuesHolder[] holders = getHolders(view, vHolder, propertyMap);
            ret.setValues(holders);
            ret.addUpdateListener(valueAnimator -> {
                for (PropertyValuesHolder holder : holders) {
                    setAnimatedValue(view, valueAnimator, holder);
                }
                view.requestLayout();
            });
            return ret;
        }

        @Override
        public <THolder> Runnable generateRunnable(View view, THolder holder) {
            if (propertyMap.size() == 0) return DelegateUtils.NothingRunnable;
            final Pair<String, Float>[] valueMapList = getLastValueMapList(view ,holder, propertyMap);
            return ()->{
                for(Pair<String, Float> item: valueMapList){
                    @NonNull final TransformTransitionPropertyBridge bridge = Objects.requireNonNull(TransformTransitionPropertyBridges.bridges.get(item.first));
                    bridge.set(view, item.second);
                }
            };
        }
    }
}