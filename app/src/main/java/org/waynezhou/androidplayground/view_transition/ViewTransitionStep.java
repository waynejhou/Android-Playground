package org.waynezhou.androidplayground.view_transition;

import android.animation.Animator;
import android.animation.AnimatorSet;

import org.waynezhou.libUtil.Linq;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewTransitionStep {

    public final List<ViewStep> viewSteps = new ArrayList<>();

    public Animator[] getAnimators(){
        int size = 0;
        for (ViewStep vStep:viewSteps) {
            size += vStep.getAnimators().length;
        }
        Animator[] ret = new Animator[size];
        int idx = 0;
        for (ViewStep vStep:viewSteps) {
            for(int i = 0; i < vStep.getAnimators().length; i++){
                ret[idx++] = vStep.getAnimators()[i];
            }
        }
        return ret;
    }

    public static abstract class ViewStep{
        public abstract Animator[] getAnimators();
    }
}
