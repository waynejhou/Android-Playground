package org.waynezhou.androidplayground.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;

import org.waynezhou.libUtil.DelegateUtils;

import java.util.ArrayList;
import java.util.List;

public class LayoutManager<TValuesHolder> {
    private final TValuesHolder layoutValueHolder;

    public LayoutManager(TValuesHolder layoutValueHolder) {
        this.layoutValueHolder = layoutValueHolder;
    }

    public Executor openTemplate(LayoutTemplate<TValuesHolder> template) {
        return new Executor(template);
    }

    public class Executor {

        private final LayoutTemplate<TValuesHolder> template;
        public Runnable executionRunnable;

        private Executor(LayoutTemplate<TValuesHolder> template) {
            this.template = template;
            this.executionRunnable = getLayoutExecution();
        }

        private Runnable getAnimationExecution() {
            return () -> {
                List<Animator> animators = new ArrayList<>();
                for (LayoutDestination.ViewDestination<TValuesHolder> vDest : template.dest.viewDestinations) {
                    List<PropertyValuesHolder> valuesHolders = new ArrayList<>();
                    List<LayoutDestination.ViewPropertyBridge> bridges = new ArrayList<>();
                    for (LayoutDestination.ViewPropertyValues<TValuesHolder> prop : vDest.properties) {
                        if (prop.valueGetters.length <= 0) continue;

                        float[] valueArr = new float[prop.valueGetters.length];
                        for (int i = 0; i < prop.valueGetters.length; i++) {
                            valueArr[i] = prop.valueGetters[i].get(layoutValueHolder);
                        }
                        valuesHolders.add(PropertyValuesHolder.ofFloat(prop.name, valueArr));
                        bridges.add(LayoutDestination.VIEW_PROP_BRIDGE.get(prop.name));
                    }
                    ValueAnimator animator = new ValueAnimator();
                    animator.setValues(valuesHolders.toArray(new PropertyValuesHolder[0]));
                    animator.setDuration(args.duration);
                    animator.addUpdateListener(animation -> {
                        for (int i = 0; i < valuesHolders.size(); i++) {
                            float value = (float) animation.getAnimatedValue(valuesHolders.get(i).getPropertyName());
                            LayoutDestination.ViewPropertyBridge bridge = bridges.get(i);
                            bridge.set(vDest.view, value);
                        }
                    });
                    animators.add(animator);
                }
                AnimatorSet set = new AnimatorSet();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        postAction.run();
                    }
                });
                set.playTogether(animators);
                set.start();
            };
        }

        private Runnable getLayoutExecution() {
            return () -> {
                for (LayoutDestination.ViewDestination<TValuesHolder> vDest : template.dest.viewDestinations) {
                    for (LayoutDestination.ViewPropertyValues<TValuesHolder> prop : vDest.properties) {
                        if (prop.valueGetters.length == 0) continue;
                        int lastIdx = prop.valueGetters.length - 1;
                        FloatGetter<TValuesHolder> lastGetter = prop.valueGetters[lastIdx];
                        LayoutDestination.ViewPropertyBridge bridge = LayoutDestination.VIEW_PROP_BRIDGE.get(prop.name);
                        if (bridge == null) return;
                        bridge.set(vDest.view, lastGetter.get(layoutValueHolder));
                    }
                }
            };
        }

        private Runnable postAction = DelegateUtils.NothingRunnable;

        public Executor setPostAction(Runnable action) {
            this.postAction = action;
            return this;
        }

        private LayoutExecutionArgs args = LayoutExecutionArgs.builder().build();

        public Executor setLayoutExecutionArgs(LayoutExecutionArgs args) {
            this.args = args;
            if (args.withAnimation) {
                this.executionRunnable = getAnimationExecution();
            } else {
                this.executionRunnable = getLayoutExecution();
            }
            return this;
        }

        public void execute() {
            template.preAction.run();
            executionRunnable.run();
        }
    }
}
