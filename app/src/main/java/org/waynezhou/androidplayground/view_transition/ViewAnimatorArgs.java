package org.waynezhou.androidplayground.view_transition;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;

public class LayoutAnimatorArgs {
    public final long duration;
    public final TimeInterpolator interpolator;
    private LayoutAnimatorArgs(long duration, TimeInterpolator interpolator){
        this.duration = duration;
        this.interpolator = interpolator;
    }
    static class Builder{
        private long duration = 0;
        private TimeInterpolator interpolator = new AccelerateInterpolator();

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
        }
        public LayoutAnimatorArgs build(){
            return new LayoutAnimatorArgs(duration, interpolator);
        }
    }
}
