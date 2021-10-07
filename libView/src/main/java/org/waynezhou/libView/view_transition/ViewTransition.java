package org.waynezhou.libUtil.view_transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewTransition<TValueHolder> {

    public static class Builder<TValueHolder>{
        private final TValueHolder valHolder;
        protected final List<ViewStep<TValueHolder>> viewSteps = new ArrayList<>();
        public Builder(TValueHolder valHolder) {
            this.valHolder = valHolder;
        }

        public ViewStep.Builder<TValueHolder> startAddStep(ViewGetter viewGetter){
            return new ViewStep.Builder<>(this, viewGetter);
        }
        public ViewTransition<TValueHolder> build(){
            return new ViewTransition<TValueHolder>(valHolder, Collections.unmodifiableList(viewSteps));
        }
    }

    private final TValueHolder valHolder;
    private final List<ViewStep<TValueHolder>> viewSteps;
    private ViewTransition(TValueHolder valHolder, List<ViewStep<TValueHolder>> viewSteps) {
        this.valHolder = valHolder;
        this.viewSteps = viewSteps;
    }

    public AnimatorSet createAnimatorSet(ViewAnimatorArgs args){
        AnimatorSet set = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();
        for(int i =0;i < viewSteps.size();i++){
            animators.add(viewSteps.get(i).createAnimator(valHolder, args));
        }
        set.playTogether(animators);
        return set;
    }

    public void runWithoutAnimation(){
        for(int i =0;i < viewSteps.size();i++){
            viewSteps.get(i).createRunnable(valHolder).run();
        }
    }

    public interface ViewGetter{
        View get();
    }

}
