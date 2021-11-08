package org.waynezhou.libView.transition;

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
}
