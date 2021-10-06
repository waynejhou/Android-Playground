package org.waynezhou.androidplayground.view_transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewPropFix<TValueHolder> {

    public static class Builder<TValueHolder>{
        private final ViewStep.Builder<TValueHolder> host;
        private final String propName;
        public Builder(ViewStep.Builder<TValueHolder> host, String prop) {
            this.host = host;
            this.propName = prop;
        }
        private ValueGetter getter;
        public ViewPropFix.Builder<TValueHolder> as(ValueGetter getter){
            this.getter = getter;
            return this;
        }
        public ViewPropFix.Builder<TValueHolder> asView(ValueGetter.ViewValueGetter getter){
            this.getter = new ValueGetter.FromView(getter);
            return this;
        }

        public ViewStep.Builder<TValueHolder> end(){
            if(this.getter==null) throw new IllegalArgumentException();
            host.propFixes.add(new ViewPropFix<>(propName, getter));
            return host;
        }
    }
    protected final String propName;
    protected final ValueGetter getter;
    public ViewPropFix(String propName, ValueGetter getter) {
        this.propName = propName;
        this.getter = getter;
    }
}
