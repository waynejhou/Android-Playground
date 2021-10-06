package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import org.waynezhou.libUtil.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ViewStep<TValueHolder> {
    public static class Builder<TValueHolder>{
        private final ViewTransition.Builder<TValueHolder> host;
        private final ViewTransition.ViewGetter viewGetter;
        protected final List<ViewPropStep<TValueHolder>> propSteps = new ArrayList<>();
        protected final List<ViewPropFix<TValueHolder>> propFixes = new ArrayList<>();
        protected Builder(ViewTransition.Builder<TValueHolder> host, ViewTransition.ViewGetter viewGetter) {
            this.host = host;
            this.viewGetter = viewGetter;
        }
        public ViewPropStep.Builder<TValueHolder> let(String prop){
            return new ViewPropStep.Builder<>(this, prop);
        }
        public ViewPropFix.Builder<TValueHolder> fix(String prop){
            return new ViewPropFix.Builder<>(this, prop);
        }
        public ViewTransition.Builder<TValueHolder> endAddStep(){
            host.viewSteps.add(new ViewStep<>(viewGetter, Collections.unmodifiableList(propSteps), Collections.unmodifiableList(propFixes)));
            return host;
        }
    }

    private final ViewTransition.ViewGetter viewGetter;
    private final List<ViewPropStep<TValueHolder>> propSteps;
    protected final List<ViewPropFix<TValueHolder>> propFixes;
    private ViewStep(ViewTransition.ViewGetter viewGetter, List<ViewPropStep<TValueHolder>> propSteps, List<ViewPropFix<TValueHolder>> fixes) {
        this.viewGetter = viewGetter;
        this.propSteps = propSteps;
        this.propFixes = fixes;
    }

    @SuppressWarnings("unchecked")
    public Animator createAnimator(TValueHolder holder, ViewAnimatorArgs args){
        final List<PropertyValuesHolder> holders = new ArrayList<>();
        for(int i = 0; i < propSteps.size(); i++){
            holders.add(propSteps.get(i).createHolder(viewGetter, holder));
        }
        final PropertyValuesHolder[] holderArr = holders.toArray(new PropertyValuesHolder[0]);
        ValueAnimator ret = new ValueAnimator();
        ret.setDuration(args.duration);
        ret.setInterpolator(args.interpolator);
        ret.setValues(holderArr);
        ret.addUpdateListener(animator->{
            final View view = viewGetter.get();
            for(int i = 0; i < holders.size(); i++){
                final PropertyValuesHolder pvHolder = holders.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(pvHolder.getPropertyName()));
                bridge.set(view, (float)animator.getAnimatedValue(pvHolder.getPropertyName()));
                /**
                LayoutTransitionPropertyBridge.TranslationX.update(view);
                LayoutTransitionPropertyBridge.TranslationY.update(view);
                LayoutTransitionPropertyBridge.ScaleX.update(view);
                LayoutTransitionPropertyBridge.ScaleY.update(view);
                 */
            }
            for(int i = 0; i < propFixes.size(); i++){
                final ViewPropFix<TValueHolder> fix = propFixes.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(fix.propName));
                ValueGetter getter = fix.getter;
                if(getter instanceof ValueGetter.FromView){
                    ((ValueGetter.FromView) getter).setView(viewGetter.get());
                }
                if(getter instanceof ValueGetter.FromValueHolder){
                    ((ValueGetter.FromValueHolder<TValueHolder>)getter).setHolder(holder);
                }
                bridge.set(view, getter.get());

            }
            view.requestLayout();

        });
        return ret;
    }

    @SuppressWarnings("unchecked")
    public Runnable createRunnable(TValueHolder holder){
        final List<ViewPropStep.ViewPropFinalStep> finalSteps = new ArrayList<>();
        for(int i = 0; i < propSteps.size(); i++){
            finalSteps.add(propSteps.get(i).createFinalStep(viewGetter, holder));
        }
        return ()->{
            final View view = viewGetter.get();
            for(int i = 0; i < finalSteps.size(); i++){
                final ViewPropStep.ViewPropFinalStep finalStep = finalSteps.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(finalStep.propName));
                bridge.set(view, finalStep.value);
            }
            for(int i = 0; i < propFixes.size(); i++){
                final ViewPropFix<TValueHolder> fix = propFixes.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(fix.propName));
                ValueGetter getter = fix.getter;
                if(getter instanceof ValueGetter.FromView){
                    ((ValueGetter.FromView) getter).setView(viewGetter.get());
                }
                if(getter instanceof ValueGetter.FromValueHolder){
                    ((ValueGetter.FromValueHolder<TValueHolder>)getter).setHolder(holder);
                }
                bridge.set(view, getter.get());
            }
        };
    }
}
