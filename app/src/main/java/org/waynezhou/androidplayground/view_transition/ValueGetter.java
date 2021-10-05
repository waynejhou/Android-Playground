package org.waynezhou.androidplayground.view_transition;

import android.view.View;

public abstract class ValueGetter {
    public abstract float get();
    @FunctionalInterface
    public interface ViewValueGetter{
        float get(View view);
    }
    public static class FromView extends ValueGetter{
        private final ViewValueGetter getter;
        public FromView(ViewValueGetter getter){
            this.getter = getter;
        }
        private View view = null;
        public void setView(View view){
            this.view = view;
        }

        @Override
        public float get() {
            return getter.get(this.view);
        }
    }
    @FunctionalInterface
    public interface HolderValueGetter<TValueHolder>{
        float get(TValueHolder holder);
    }
    public static class FromValueHolder<TValueHolder> extends ValueGetter{
        private final HolderValueGetter<TValueHolder> getter;
        public FromValueHolder(HolderValueGetter<TValueHolder> getter){
            this.getter = getter;
        }
        private TValueHolder holder = null;
        public void setHolder(TValueHolder holder){
            this.holder = holder;
        }

        @Override
        public float get() {
            return getter.get(this.holder);
        }
    }

    public static PropGetterBuilder prop(String propName){
        return new PropGetterBuilder(propName);
    }
    public static class PropGetterBuilder{
        private final String propName;

        private PropGetterBuilder(String propName) {
            this.propName = propName;
        }

        @SuppressWarnings("ConstantConditions")
        public ValueGetter[] FromCurrentLayoutTo(ValueGetter getter){
            return new ValueGetter[]{
                    new FromView(v->LayoutTransitionPropertyBridges.bridges.get(propName).get(v)),
                    getter
            };
        }
        public ValueGetter[] FromCurrentLayoutTo(ViewValueGetter getter){
            return new ValueGetter[]{
                    new FromView(v->LayoutTransitionPropertyBridges.bridges.get(propName).get(v)),
                    new FromView(getter)
            };
        }
        public <TVHolder> ValueGetter[] FromCurrentLayoutTo(HolderValueGetter<TVHolder> getter){
            return new ValueGetter[]{
                    new FromView(v->LayoutTransitionPropertyBridges.bridges.get(propName).get(v)),
                    new FromValueHolder<>(getter)
            };
        }
        @SuppressWarnings("ConstantConditions")
        public ValueGetter[] FromCurrentTransformTo(ValueGetter getter){
            return new ValueGetter[]{
                    new FromView(v->TransformTransitionPropertyBridges.bridges.get(propName).get(v)),
                    getter
            };
        }
    }
}
