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

        public Builder withAnimation() {
            withAnimation = true;
            return this;
        }

        public Builder setWithAnimation(boolean withAnimation) {
            this.withAnimation = withAnimation;
            return this;
        }

        public Builder setAnimationDuration(int duration) {
            this.duration = duration;
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

