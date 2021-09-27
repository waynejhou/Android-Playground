package org.waynezhou.androidplayground.layout;

import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;

import org.waynezhou.libUtil.StandardKt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LayoutDestination<TValuesHolder> {
    protected final List<ViewDestination<TValuesHolder>> viewDestinations;

    private LayoutDestination(List<ViewDestination<TValuesHolder>> viewDestinations) {
        this.viewDestinations = viewDestinations;
    }

    public static <TValuesHolder> Builder<TValuesHolder> builder(Class<TValuesHolder> clz) {
        return new Builder<>();
    }

    public static <TValuesHolder> Builder<TValuesHolder> builder(LayoutDestination<TValuesHolder> dest) {
        return new Builder<>(dest);
    }

    public static class Builder<TValuesHolder> {
        private List<ViewDestination<TValuesHolder>> viewDestinations = new ArrayList<>();

        public Builder() {

        }

        public Builder(LayoutDestination<TValuesHolder> dest) {
            this.viewDestinations = new ArrayList<>(dest.viewDestinations);
        }

        public ViewDestinationBuilder<TValuesHolder> beginConfig(View view) {
            return new ViewDestinationBuilder<>(this, view);
        }

        public LayoutDestination<TValuesHolder> build() {
            return new LayoutDestination<>(viewDestinations);
        }
    }


    public static class ViewDestinationBuilder<TValuesHolder> {
        private final View view;
        private final LayoutDestination.Builder<TValuesHolder> host;

        private ViewDestinationBuilder(LayoutDestination.Builder<TValuesHolder> host, View view) {
            this.view = view;
            this.host = host;
        }


        private final List<ViewPropertyValues<TValuesHolder>> properties = new ArrayList<>();

        @SafeVarargs
        public ViewDestinationBuilder<TValuesHolder> set(String propertyName, FloatGetter<TValuesHolder>... values) {
            properties.clear();
            properties.add(new ViewPropertyValues<>(propertyName, values));
            return this;
        }

        public ViewDestinationBuilder<TValuesHolder> apply(StandardKt.RunBlock<ViewDestinationBuilder<TValuesHolder>> runBlock) {
            runBlock.run(this);
            return this;
        }

        public LayoutDestination.Builder<TValuesHolder> endConfig() {
            host.viewDestinations.add(new ViewDestination<>(view, properties));
            return host;
        }
    }


    protected static class ViewDestination<TValuesHolder> {
        protected final View view;
        protected final List<ViewPropertyValues<TValuesHolder>> properties;

        private ViewDestination(View view, List<ViewPropertyValues<TValuesHolder>> properties) {
            this.view = view;
            this.properties = properties;
        }
    }

    protected static class ViewPropertyValues<TValuesHolder> {
        protected final String name;
        protected final FloatGetter<TValuesHolder>[] valueGetters;

        private ViewPropertyValues(String name, FloatGetter<TValuesHolder>[] valueGetters) {
            this.name = name;
            this.valueGetters = valueGetters;
        }
    }

    protected static abstract class ViewPropertyBridge {
        abstract float get(View view);

        abstract void set(View view, float value);
    }

    private static class ViewWidthBridge extends ViewPropertyBridge {
        @Override
        float get(View view) {
            return view.getMeasuredWidth();
        }

        @Override
        void set(View view, float value) {
            view.getLayoutParams().width = (int) value;
            view.requestLayout();
        }
    }

    private static class ViewHeightBridge extends ViewPropertyBridge {
        @Override
        float get(View view) {
            return view.getMeasuredHeight();
        }

        @Override
        void set(View view, float value) {
            view.getLayoutParams().height = (int) value;
            view.requestLayout();
        }
    }

    private static class ViewLeftBridge extends ViewPropertyBridge {
        @Override
        float get(View view) {
            return view.getLeft();
        }

        @Override
        void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = (int) value;
            view.requestLayout();
        }
    }

    private static class ViewTopBridge extends ViewPropertyBridge {
        @Override
        float get(View view) {
            return view.getTop();
        }

        @Override
        void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = (int) value;
            view.requestLayout();
        }
    }

    private static class ViewRotateBridge extends ViewPropertyBridge {
        @Override
        float get(View view) {
            return view.getRotation();
        }

        @Override
        void set(View view, float value) {
            view.setRotation(value);
        }
    }



    protected static HashMap<String, ViewPropertyBridge> VIEW_PROP_BRIDGE = new HashMap<String, ViewPropertyBridge>(){{
        put(LayoutProperties.PROP_WIDTH, new ViewWidthBridge());
        put(LayoutProperties.PROP_HEIGHT, new ViewHeightBridge());
        put(LayoutProperties.PROP_LEFT, new ViewLeftBridge());
        put(LayoutProperties.PROP_TOP, new ViewTopBridge());
        put(LayoutProperties.PROP_ROTATION, new ViewRotateBridge());
    }};
}
