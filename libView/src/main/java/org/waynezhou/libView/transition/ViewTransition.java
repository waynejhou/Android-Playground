package org.waynezhou.libView.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewTransition<TValueHolder> {

    public static class Builder<TValueHolder> {
        private final TValueHolder valHolder;
        protected final List<ViewStep<TValueHolder>> viewSteps;
        protected final List<ConditionalViewStep<TValueHolder>> conditionalViewSteps;

        public Builder(TValueHolder valHolder) {
            this.valHolder = valHolder;
            this.viewSteps = new ArrayList<>();
            this.conditionalViewSteps = new ArrayList<>();
        }
        public Builder(ViewTransition<TValueHolder> other){
            this.valHolder = other.valHolder;
            this.viewSteps = new ArrayList<>(other.viewSteps);
            this.conditionalViewSteps = new ArrayList<>(other.conditionalViewSteps);
        }

        public ViewStep.Builder<TValueHolder> $_startAddStep(ViewGetter viewGetter) {
            return new ViewStep.Builder<>(this, viewGetter);
        }

        public Builder<TValueHolder> $_addStep(ViewGetter viewGetter, ViewStep.ViewPreBuild<TValueHolder> preBuild) {
            ViewStep.Builder<TValueHolder> builder = new ViewStep.Builder<>(this, viewGetter);
            preBuild.build(builder);
            return builder.$_endAddStep();
        }

        public ViewStep.Builder<TValueHolder> $_startAddStep(Condition condition, ViewGetter viewGetter) {
            return new ViewStep.Builder<>(this, viewGetter, condition);
        }

        public ViewTransition<TValueHolder> build() {
            return new ViewTransition<>(valHolder, viewSteps, conditionalViewSteps);
        }
    }


    private final TValueHolder valHolder;
    private final List<ViewStep<TValueHolder>> viewSteps;
    protected final List<ConditionalViewStep<TValueHolder>> conditionalViewSteps;

    private ViewTransition(TValueHolder valHolder, List<ViewStep<TValueHolder>> viewSteps, List<ConditionalViewStep<TValueHolder>> conditionalViewSteps) {
        this.valHolder = valHolder;
        this.viewSteps = Collections.unmodifiableList(viewSteps);
        this.conditionalViewSteps = Collections.unmodifiableList(conditionalViewSteps);
    }

    public AnimatorSet createAnimatorSet(ViewAnimatorArgs args) {
        AnimatorSet set = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < viewSteps.size(); i++) {
            animators.add(viewSteps.get(i).createAnimator(valHolder, args));
        }
        for (int i = 0; i < conditionalViewSteps.size(); i++) {
            ConditionalViewStep<TValueHolder> cvStep = conditionalViewSteps.get(i);
            if (cvStep.condition.isPassed()) {
                animators.add(cvStep.viewStep.createAnimator(valHolder, args));
            }
        }
        set.playTogether(animators);
        return set;
    }

    public void runWithoutAnimation(boolean withRequestLayout) {
        for (int i = 0; i < viewSteps.size(); i++) {
            viewSteps.get(i).createExecutor(valHolder).execute(withRequestLayout);
        }
        for (int i = 0; i < conditionalViewSteps.size(); i++) {
            ConditionalViewStep<TValueHolder> cvStep = conditionalViewSteps.get(i);
            if (cvStep.condition.isPassed()) {
                cvStep.viewStep.createExecutor(valHolder).execute(withRequestLayout);
            }
        }
    }

    public ViewTransition<TValueHolder> transpose() {
        final List<ViewStep<TValueHolder>> transposedViewSteps = new ArrayList<>();
        for (ViewStep<TValueHolder> vStep : this.viewSteps) {
            final ViewStep<TValueHolder> transposedViewStep = vStep.transpose();
            transposedViewSteps.add(transposedViewStep);
        }
        final List<ConditionalViewStep<TValueHolder>> transposedConditionalViewSteps = new ArrayList<>();
        for (ConditionalViewStep<TValueHolder> cvStep : this.conditionalViewSteps) {
            final ConditionalViewStep<TValueHolder> transposedConditionalViewStep = new ConditionalViewStep<>(cvStep.condition, cvStep.viewStep.transpose());
            transposedConditionalViewSteps.add(transposedConditionalViewStep);
        }

        return new ViewTransition<>(this.valHolder, transposedViewSteps, transposedConditionalViewSteps);
    }

    protected static class ConditionalViewStep<TValueHolder> {
        protected final Condition condition;
        protected final ViewStep<TValueHolder> viewStep;

        public ConditionalViewStep(Condition condition, ViewStep<TValueHolder> viewStep) {
            this.condition = condition;
            this.viewStep = viewStep;
        }
    }

    public interface ViewGetter {
        View get();
    }

    public interface Condition {
        boolean isPassed();
    }
}
