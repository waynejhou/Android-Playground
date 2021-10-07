package org.waynezhou.libView.view_transition;

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
    public ViewPropFix<TValueHolder> transpose() {
        if(LayoutTransitionPropertyBridges.transposeMap.containsKey(this.propName)){
            return new ViewPropFix<>(LayoutTransitionPropertyBridges.transposeMap.get(this.propName), this.getter);
        }
        return this;
    }
}
