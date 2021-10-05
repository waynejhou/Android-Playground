package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ViewTransitionPropStep(string propName, ValueGetter...) <=> PropertyValueHolder
 * ViewTransitionStep <=> Animator
 * ViewTransition <=> AnimatorSet
 *
 *
 * new ViewTransition<MainActivity>()
 *     .startAddStep(binding.topContainer)
 *     .startAddPropStep(PROP_WIDTH).fromCurrentTo
 *     .endAddStep
 *     .addStep()
 */









/**
 * A Properties Transition Step for a View
 */
public abstract class ViewTransitionSteps<TValueHolder> {

    public final List<ViewStep> viewSteps = new ArrayList<>();

    public Animator[] generateAnimators(View view, TValueHolder holder, ViewAnimatorArgs args) {
        int size = viewSteps.size();

        Animator[] ret = new Animator[size];
        for (int i = 0; i < viewSteps.size(); i++) {
            ret[i] = viewSteps.get(i).generateAnimator(view, holder, args);
        }
        return ret;
    }
    public Runnable generateRunnable(View view, TValueHolder holder) {
        return ()->{
            for (int i = 0; i < viewSteps.size(); i++) {
                viewSteps.get(i).generateRunnable(view, holder).run();
            }
        };
    }

    public static abstract class ViewStep<TValueHolder> {
        public abstract <TValueHolder> Animator generateAnimator(View view, TValueHolder holder, ViewAnimatorArgs args);
        public abstract <TValueHolder> Runnable generateRunnable(View view, TValueHolder holder);
        protected static <TValueHolder> PropertyValuesHolder[] getHolders(View view, TValueHolder holder, Map<String, ValueGetter[]> propertyMap){
            final PropertyValuesHolder[] holders = new PropertyValuesHolder[propertyMap.size()];
            int idx = 0;
            for (Map.Entry<String, ValueGetter[]> entry : propertyMap.entrySet()) {
                if (!TransformTransitionPropertyBridges.bridges.containsKey(entry.getKey())) continue;
                final float[] values = new float[entry.getValue().length];
                int vIdx = 0;
                for (ValueGetter vg : entry.getValue()) {
                    if(vg instanceof ValueGetter.FromView){
                        ((ValueGetter.FromView) vg).setView(view);
                    }
                    if(vg instanceof ValueGetter.FromValueHolder){
                        //noinspection unchecked
                        ((ValueGetter.FromValueHolder<TValueHolder>) vg).setHolder(holder);
                    }
                    values[vIdx++] = vg.get();
                }
                holders[idx++] = PropertyValuesHolder.ofFloat(entry.getKey(), values);
            }
            return holders;
        }
        protected static void setAnimatedValue(View view, ValueAnimator animator, PropertyValuesHolder holder){
            TransformTransitionPropertyBridge bridge = TransformTransitionPropertyBridges.bridges.get(holder.getPropertyName());
            if(bridge==null) return;
            bridge.set(view, (float)animator.getAnimatedValue(holder.getPropertyName()));
        }
        protected static <THolder> Pair<String, Float>[] getLastValueMapList(View view, THolder holder, Map<String, ValueGetter[]> propertyMap){
            @SuppressWarnings({"unchecked", "ConstantConditions"})
            final Pair<String, Float>[] ret = (Pair<String, Float>[])new Object[propertyMap.size()];
            int idx = 0;
            for (Map.Entry<String, ValueGetter[]> entry : propertyMap.entrySet()) {
                if (!TransformTransitionPropertyBridges.bridges.containsKey(entry.getKey())) continue;
                if(entry.getValue().length <= 0) continue;
                final ValueGetter lGetter = entry.getValue()[entry.getValue().length-1];
                if(lGetter instanceof ValueGetter.FromView){
                    ((ValueGetter.FromView) lGetter).setView(view);
                }
                if(lGetter instanceof ValueGetter.FromValueHolder){
                    //noinspection unchecked
                    ((ValueGetter.FromValueHolder<THolder>) lGetter).setHolder(holder);
                }
                ret[idx++] = new Pair<>(entry.getKey(), lGetter.get());
            }
            return ret;
        }
    }
}
