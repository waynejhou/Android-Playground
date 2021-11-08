package org.waynezhou.libView.transition;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ViewStep<TValueHolder> {

    public static class Builder<TValueHolder>  {
        private final ViewTransition.Builder<TValueHolder> host;
        private final ViewTransition.ViewGetter viewGetter;
        private final ViewTransition.Condition condition;
        protected Builder(ViewTransition.Builder<TValueHolder> host, ViewTransition.ViewGetter viewGetter) {
            this(host, viewGetter, null);
        }
        protected Builder(ViewTransition.Builder<TValueHolder> host, ViewTransition.ViewGetter viewGetter, ViewTransition.Condition condition) {
            this.host = host;
            this.viewGetter = viewGetter;
            this.condition = condition;
        }


        protected final List<ViewPropStep<TValueHolder>> propSteps = new ArrayList<>();

        public ViewPropStep.Builder<TValueHolder> let(String prop) {
            return new ViewPropStep.Builder<>(this, prop);
        }

        protected final List<ViewPropFix<TValueHolder>> propFixes = new ArrayList<>();

        public ViewPropFix.Builder<TValueHolder> fix(String prop) {
            return new ViewPropFix.Builder<>(this, prop);
        }

        public ViewStep.Builder<TValueHolder> preBuild(ViewPreBuild<TValueHolder> preBuild){
            preBuild.build(this);
            return this;
        }

        protected ViewAction preAction = v->{};
        public Builder<TValueHolder> pre(ViewAction action) {
            preAction = action;
            return this;
        }

        public ViewTransition.Builder<TValueHolder> $_endAddStep() {
            if(condition!=null){
                host.conditionalViewSteps.add(new ViewTransition.ConditionalViewStep<>(condition, new ViewStep<>(viewGetter, propSteps, propFixes, preAction)));
            }else{
                host.viewSteps.add(new ViewStep<>(viewGetter, propSteps, propFixes, preAction));
            }
            return host;
        }
    }

    private final ViewTransition.ViewGetter viewGetter;
    private final List<ViewPropStep<TValueHolder>> propSteps;
    protected final List<ViewPropFix<TValueHolder>> propFixes;
    protected final ViewAction preAction;
    private ViewStep(ViewTransition.ViewGetter viewGetter, List<ViewPropStep<TValueHolder>> propSteps, List<ViewPropFix<TValueHolder>> fixes, ViewAction preAction) {
        this.viewGetter = viewGetter;
        this.propSteps = Collections.unmodifiableList(propSteps);
        this.propFixes = Collections.unmodifiableList(fixes);
        this.preAction = preAction;
    }

    @SuppressWarnings("unchecked")
    public Animator createAnimator(TValueHolder holder, ViewAnimatorArgs args) {
        final List<PropertyValuesHolder> holders = new ArrayList<>();
        for (int i = 0; i < propSteps.size(); i++) {
            holders.add(propSteps.get(i).createHolder(viewGetter, holder));
        }
        final PropertyValuesHolder[] holderArr = holders.toArray(new PropertyValuesHolder[0]);
        ValueAnimator ret = new ValueAnimator();
        ret.setDuration(args.duration);
        ret.setInterpolator(args.interpolator);
        ret.setValues(holderArr);
        ret.addUpdateListener(animator -> {
            final View view = viewGetter.get();
            for (int i = 0; i < holders.size(); i++) {
                final PropertyValuesHolder pvHolder = holders.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(pvHolder.getPropertyName()));
                bridge.set(view, (float) animator.getAnimatedValue(pvHolder.getPropertyName()));
                /**
                 LayoutTransitionPropertyBridge.TranslationX.update(view);
                 LayoutTransitionPropertyBridge.TranslationY.update(view);
                 LayoutTransitionPropertyBridge.ScaleX.update(view);
                 LayoutTransitionPropertyBridge.ScaleY.update(view);
                 */
            }
            for (int i = 0; i < propFixes.size(); i++) {
                final ViewPropFix<TValueHolder> fix = propFixes.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(fix.propName));
                ValueGetter getter = fix.getter;
                if (getter instanceof ValueGetter.FromView) {
                    ((ValueGetter.FromView) getter).setView(viewGetter.get());
                }
                if (getter instanceof ValueGetter.FromValueHolder) {
                    ((ValueGetter.FromValueHolder<TValueHolder>) getter).setHolder(holder);
                }
                bridge.set(view, getter.get());

            }
            view.requestLayout();

        });
        ret.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                preAction.execute(viewGetter.get());
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return ret;
    }

    @SuppressWarnings("unchecked")
    public ViewStepExecutor createExecutor(TValueHolder holder) {
        final List<ViewPropStep.ViewPropFinalStep> finalSteps = new ArrayList<>();
        for (int i = 0; i < propSteps.size(); i++) {
            finalSteps.add(propSteps.get(i).createFinalStep(viewGetter, holder));
        }
        return (withRequestLayout) -> {
            final View view = viewGetter.get();
            preAction.execute(view);
            for (int i = 0; i < finalSteps.size(); i++) {
                final ViewPropStep.ViewPropFinalStep finalStep = finalSteps.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(finalStep.propName));
                bridge.set(view, finalStep.value);
            }
            for (int i = 0; i < propFixes.size(); i++) {
                final ViewPropFix<TValueHolder> fix = propFixes.get(i);
                final LayoutTransitionPropertyBridge bridge = Objects.requireNonNull(LayoutTransitionPropertyBridges.bridges.get(fix.propName));
                ValueGetter getter = fix.getter;
                if (getter instanceof ValueGetter.FromView) {
                    ((ValueGetter.FromView) getter).setView(viewGetter.get());
                }
                if (getter instanceof ValueGetter.FromValueHolder) {
                    ((ValueGetter.FromValueHolder<TValueHolder>) getter).setHolder(holder);
                }
                bridge.set(view, getter.get());
            }
            if (withRequestLayout) view.requestLayout();
        };
    }

    public ViewStep<TValueHolder> transpose() {
        final List<ViewPropStep<TValueHolder>> transposedPropSteps = new ArrayList<>();
        for (ViewPropStep<TValueHolder> vpStep : this.propSteps) {
            transposedPropSteps.add(vpStep.transpose());
        }
        final List<ViewPropFix<TValueHolder>> transposedPropFixes = new ArrayList<>();
        for (ViewPropFix<TValueHolder> vpFix : this.propFixes) {
            transposedPropFixes.add(vpFix.transpose());
        }
        return new ViewStep<>(this.viewGetter, transposedPropSteps, transposedPropFixes, this.preAction);
    }

    @FunctionalInterface
    public interface ViewStepExecutor {
        void execute(boolean withRequestLayout);
    }

    @FunctionalInterface
    public interface ViewAction {
        void execute(View view);
    }
    @FunctionalInterface
    public interface ViewPreBuild<TValueHolder> {
        void build(ViewStep.Builder<TValueHolder> it);
    }
}
