package org.waynezhou.androidplayground.layout;

import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;

import org.waynezhou.libUtil.StandardKt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LayoutDestination<TViewHolder> {
    protected final List<ViewDestination<TViewHolder>> viewDestinations;

    private LayoutDestination(List<ViewDestination<TViewHolder>> viewDestinations) {
        this.viewDestinations = viewDestinations;
    }

    public static <TViewHolder> Builder<TViewHolder> builder(Class<TViewHolder> clz) {
        return new Builder<>();
    }

    public static <TViewHolder> Builder<TViewHolder> builder(LayoutDestination<TViewHolder> dest) {
        return new Builder<>(dest);
    }

    public static class Builder<TViewHolder> {
        private List<ViewDestination<TViewHolder>> viewDestinations = new ArrayList<>();

        public Builder() {

        }

        public Builder(LayoutDestination<TViewHolder> dest) {
            this.viewDestinations = new ArrayList<>(dest.viewDestinations);
        }

        public ViewDestinationBuilder<TViewHolder> beginConfig(ViewGetter<TViewHolder> viewGetter) {
            return new ViewDestinationBuilder<>(this, viewGetter);
        }

        public LayoutDestination<TViewHolder> build() {
            return new LayoutDestination<>(viewDestinations);
        }
    }


    public static class ViewDestinationBuilder<TViewHolder> {
        private final ViewGetter<TViewHolder> viewGetter;
        private final LayoutDestination.Builder<TViewHolder> host;

        private ViewDestinationBuilder(LayoutDestination.Builder<TViewHolder> host, ViewGetter<TViewHolder> viewGetter) {
            this.viewGetter = viewGetter;
            this.host = host;
        }


        private final List<ViewPropertyValues<TViewHolder>> properties = new ArrayList<>();

        @SafeVarargs
        public final ViewDestinationBuilder<TViewHolder> set(String propertyName, FloatGetter<TViewHolder>... values) {
            properties.add(new ViewPropertyValues<>(propertyName, values));
            return this;
        }

        public ViewDestinationBuilder<TViewHolder> apply(StandardKt.RunBlock<ViewDestinationBuilder<TViewHolder>> runBlock) {
            runBlock.run(this);
            return this;
        }

        public LayoutDestination.Builder<TViewHolder> endConfig() {
            host.viewDestinations.add(new ViewDestination<>(viewGetter, properties));
            return host;
        }
    }


    protected static class ViewDestination<TViewHolder> {
        protected final ViewGetter<TViewHolder> viewGetter;
        protected final List<ViewPropertyValues<TViewHolder>> properties;

        private ViewDestination(ViewGetter<TViewHolder> viewGetter, List<ViewPropertyValues<TViewHolder>> properties) {
            this.viewGetter = viewGetter;
            this.properties = properties;
        }
    }

    protected static class ViewPropertyValues<TViewHolder> {
        protected final String name;
        protected final FloatGetter<TViewHolder>[] valueGetters;

        private ViewPropertyValues(String name, FloatGetter<TViewHolder>[] valueGetters) {
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
