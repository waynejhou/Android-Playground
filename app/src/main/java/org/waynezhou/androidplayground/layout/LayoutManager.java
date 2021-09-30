package org.waynezhou.androidplayground.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class LayoutManager<TViewHolder> {
    private final TViewHolder layoutValueHolder;

    public LayoutManager(TViewHolder layoutValueHolder) {
        this.layoutValueHolder = layoutValueHolder;
    }

    public Executor openTemplate(LayoutTemplate<TViewHolder> template) {
        return new Executor(template);
    }

    public class Executor {

        private final LayoutTemplate<TViewHolder> template;
        public Runnable executionRunnable;

        private Executor(LayoutTemplate<TViewHolder> template) {
            this.template = template;
            this.executionRunnable = getLayoutExecution();
        }

        private Runnable getAnimationExecution() {
            return () -> {
                //LogHelper.d("animation Execution");
                List<Animator> animators = new ArrayList<>();
                for (LayoutDestination.ViewDestination<TViewHolder> vDest : template.dest.viewDestinations) {
                    List<PropertyValuesHolder> valuesHolders = new ArrayList<>();
                    List<LayoutDestination.ViewPropertyBridge> bridges = new ArrayList<>();
                    View view = vDest.viewGetter.get(layoutValueHolder);
                    for (LayoutDestination.ViewPropertyValues<TViewHolder> prop : vDest.properties) {
                        if (prop.valueGetters.length <= 0) continue;

                        float[] valueArr = new float[prop.valueGetters.length];
                        for (int i = 0; i < prop.valueGetters.length; i++) {
                            FloatGetter<TViewHolder> getter = prop.valueGetters[i];
                            if(getter instanceof LayoutProperties.ViewFloatGetter){
                                ((LayoutProperties.ViewFloatGetter<TViewHolder>) getter).setName(prop.name);
                                ((LayoutProperties.ViewFloatGetter<TViewHolder>) getter).setView(view);
                            }
                            valueArr[i] = prop.valueGetters[i].get(layoutValueHolder);
                        }
                        valuesHolders.add(PropertyValuesHolder.ofFloat(prop.name, valueArr));
                        bridges.add(LayoutDestination.VIEW_PROP_BRIDGE.get(prop.name));
                        /*{
                            int lastIdx = prop.valueGetters.length - 1;
                            FloatGetter<TViewHolder> lastGetter = prop.valueGetters[lastIdx];
                            LayoutDestination.ViewPropertyBridge bridge = LayoutDestination.VIEW_PROP_BRIDGE.get(prop.name);
                            if (bridge == null) return;
                            LogHelper.d("set %s to %f", prop.name, lastGetter.get(layoutValueHolder));
                        }*/
                    }
                    ValueAnimator animator = new ValueAnimator();
                    animator.setValues(valuesHolders.toArray(new PropertyValuesHolder[0]));
                    animator.setDuration(args.duration);
                    animator.addUpdateListener(animation -> {
                        for (int i = 0; i < valuesHolders.size(); i++) {
                            String name = valuesHolders.get(i).getPropertyName();
                            float value = (float) animation.getAnimatedValue(name);
                            LayoutDestination.ViewPropertyBridge bridge = bridges.get(i);
                            bridge.set(view, value);
                        }
                        if(enableRequest){
                            view.requestLayout();
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
                //LogHelper.d("layout Execution");
                for (LayoutDestination.ViewDestination<TViewHolder> vDest : template.dest.viewDestinations) {
                    View view = vDest.viewGetter.get(layoutValueHolder);
                    for (LayoutDestination.ViewPropertyValues<TViewHolder> prop : vDest.properties) {
                        if (prop.valueGetters.length == 0) continue;
                        int lastIdx = prop.valueGetters.length - 1;
                        FloatGetter<TViewHolder> lastGetter = prop.valueGetters[lastIdx];
                        if(lastGetter instanceof LayoutProperties.ViewFloatGetter){
                            ((LayoutProperties.ViewFloatGetter<TViewHolder>) lastGetter).setName(prop.name);
                            ((LayoutProperties.ViewFloatGetter<TViewHolder>) lastGetter).setView(view);
                        }
                        LayoutDestination.ViewPropertyBridge bridge = LayoutDestination.VIEW_PROP_BRIDGE.get(prop.name);
                        if (bridge == null) return;
                        //LogHelper.d("set %s to %f", prop.name, lastGetter.get(layoutValueHolder));
                        bridge.set(view, lastGetter.get(layoutValueHolder));
                    }
                    if(enableRequest){
                        view.requestLayout();
                    }
                }
                postAction.run();
            };
        }

        private Runnable postAction = DelegateUtils.NothingRunnable;

        public Executor setPostAction(Runnable action) {
            this.postAction = action;
            return this;
        }

        private LayoutExecutionArgs args = LayoutExecutionArgs.builder().build();

        public Executor setArgs(LayoutExecutionArgs args) {
            this.args = args;
            if (args.withAnimation) {
                this.executionRunnable = getAnimationExecution();
            } else {
                this.executionRunnable = getLayoutExecution();
            }
            return this;
        }

        private volatile boolean enableRequest = true;
        public void execute() {
            enableRequest = true;
            template.preAction.run();
            executionRunnable.run();
        }
        public void executeWithoutRequest() {
            enableRequest = false;
            template.preAction.run();
            executionRunnable.run();
        }
    }
}
