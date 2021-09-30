package org.waynezhou.androidplayground.layout;

import android.view.View;

import org.waynezhou.libUtil.StandardKt;

import java.util.Objects;

public final class LayoutProperties {
    private LayoutProperties() {
    }

    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_ROTATION = "rotation";

    protected static class ViewFloatGetter<TViewHolder> implements FloatGetter<TViewHolder> {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        private View view;

        public void setView(View view) {
            this.view = view;
        }

        @Override
        public float get(TViewHolder it) {
            return Objects.requireNonNull(LayoutDestination.VIEW_PROP_BRIDGE.get(name)).get(view);
        }
    }

    public static <TViewHolder> FloatGetter<TViewHolder> CURRENT() {
        return new ViewFloatGetter<>();
    }

    public static <TViewHolder> FloatGetter<TViewHolder> GET_ZERO() {
        return t -> 0;
    }

    public static <TViewHolder> StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<TViewHolder>> VIEW_HIDE_BOTTOM(FloatGetter<TViewHolder> rootHeight) {
        return it -> {
            it.set(PROP_WIDTH, CURRENT(), CURRENT());
            it.set(PROP_HEIGHT, CURRENT(), GET_ZERO());
            it.set(PROP_LEFT, CURRENT(), CURRENT());
            it.set(PROP_TOP, CURRENT(), rootHeight);
        };
    }
    public static <TViewHolder> StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<TViewHolder>> VIEW_HIDE_TOP() {
        return it -> {
            it.set(PROP_WIDTH, CURRENT(), CURRENT());
            it.set(PROP_HEIGHT, CURRENT(), GET_ZERO());
            it.set(PROP_LEFT, CURRENT(), CURRENT());
            it.set(PROP_TOP, CURRENT(), GET_ZERO());
        };
    }
    public static <TViewHolder> StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<TViewHolder>> VIEW_HIDE_LEFT() {
        return it -> {
            it.set(PROP_WIDTH, CURRENT(), GET_ZERO());
            it.set(PROP_HEIGHT, CURRENT(), CURRENT());
            it.set(PROP_LEFT, CURRENT(), GET_ZERO());
            it.set(PROP_TOP, CURRENT(), CURRENT());
        };
    }
    public static <TViewHolder> StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<TViewHolder>> VIEW_HIDE_RIGHT(
            FloatGetter<TViewHolder> rootWidth) {
        return it -> {
            it.set(PROP_WIDTH, CURRENT(), GET_ZERO());
            it.set(PROP_HEIGHT, CURRENT(), CURRENT());
            it.set(PROP_LEFT, CURRENT(), rootWidth);
            it.set(PROP_TOP, CURRENT(), CURRENT());
        };
    }

    public static <TViewHolder> StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<TViewHolder>> VIEW_FULLSCREEN(
            FloatGetter<TViewHolder> rootWidth, FloatGetter<TViewHolder> rootHeight) {
        return it -> {
            it.set(PROP_WIDTH, CURRENT(), rootWidth);
            it.set(PROP_HEIGHT, CURRENT(), rootHeight);
            it.set(PROP_LEFT, CURRENT(), GET_ZERO());
            it.set(PROP_TOP, CURRENT(), GET_ZERO());
        };
    }
}
