package org.waynezhou.androidplayground.layout;

import org.waynezhou.libUtil.DelegateUtils;

public class LayoutExecutionArgs {
    public final boolean withAnimation;
    public final int duration;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LayoutExecutionArgs args) {
        return new Builder(args);
    }

    public static class Builder {
        private boolean withAnimation = false;
        private int duration = 0;

        public Builder() {
        }

        public Builder(LayoutExecutionArgs args) {
            withAnimation = args.withAnimation;
            duration = args.duration;
        }


        public Builder setAnimationDuration(int duration) {
            if (duration <= 0){
                this.withAnimation = false;
                this.duration = 0;
            }
            else {
                this.withAnimation = true;
                this.duration = duration;
            }
            return this;
        }


        public LayoutExecutionArgs build() {
            return new LayoutExecutionArgs(withAnimation, duration);
        }
    }

    private LayoutExecutionArgs(boolean withAnimation, int duration) {
        this.withAnimation = withAnimation;
        this.duration = duration;
    }
}

