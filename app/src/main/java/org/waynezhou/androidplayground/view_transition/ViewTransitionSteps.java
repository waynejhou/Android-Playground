package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewTransitionStep {

    public final List<ViewStep> viewSteps = new ArrayList<>();

    public Animator[] generateAnimators(){
        int size = 0;
        for (ViewStep vStep:viewSteps) {
            size += vStep.size();
        }
        Animator[] ret = new Animator[size];
        int idx = 0;
        for (ViewStep vStep:viewSteps) {
            for(int i = 0; i < vStep.size(); i++){
                ret[idx++] = vStep.generateAnimators()[i];
            }
        }
        return ret;
    }

    public static abstract class ViewStep{
        public abstract int size();
        public abstract Animator[] generateAnimators();
        public abstract Runnable[] generateRunnable();
    }
}
