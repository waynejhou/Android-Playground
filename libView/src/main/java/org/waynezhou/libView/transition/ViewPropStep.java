package org.waynezhou.libView.view_transition;

import android.animation.PropertyValuesHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class ViewPropStep<TValueHolder> {
    public static class Builder<TValueHolder>{
        private final ViewStep.Builder<TValueHolder> host;
        private final String propName;
        private final List<ValueGetter> valueGetters = new ArrayList<>();
        public Builder(ViewStep.Builder<TValueHolder> host, String prop) {
            this.host = host;
            this.propName = prop;
        }
        public ViewPropStep.Builder<TValueHolder> setSteps(ValueGetter... getters){
            valueGetters.clear();
            return toStep(getters);
        }
        public ViewPropStep.Builder<TValueHolder> toStep(ValueGetter... getters){
            Collections.addAll(valueGetters, getters);
            return this;
        }
        public ViewPropStep.Builder<TValueHolder> toStep(ValueGetter.HolderValueGetter<TValueHolder> getter){
            valueGetters.add(new ValueGetter.FromValueHolder<>(getter));
            return this;
        }
        public ViewPropStep.Builder<TValueHolder> toViewStep(ValueGetter.ViewValueGetter getter){
            valueGetters.add(new ValueGetter.FromView(getter));
            return this;
        }
        public ViewPropStep.Builder<TValueHolder> startFromCurrent(){
            valueGetters.clear();
            return addCurrent();
        }
        public ViewPropStep.Builder<TValueHolder> addCurrent(){
            LayoutTransitionPropertyBridge bridge = LayoutTransitionPropertyBridges.bridges.get(propName);
            assert bridge != null;
            valueGetters.add(new ValueGetter.FromView(bridge::get));
            return this;
        }
        public ViewStep.Builder<TValueHolder> end(){
            if(valueGetters.size()<=0) throw new IllegalArgumentException();
            for(int i = 0; i < host.propSteps.size(); i++){
                if(host.propSteps.get(i).propName.equals(propName)){
                    host.propSteps.remove(i);
                    break;
                }
            }
            host.propSteps.add(new ViewPropStep<>(propName, valueGetters));
            return host;
        }
    }

    private final String propName;
    private final List<ValueGetter> valueGetters;
    private ViewPropStep(String propName, List<ValueGetter> valueGetters){
        this.propName = propName;
        this.valueGetters = Collections.unmodifiableList(valueGetters);
    }

    protected PropertyValuesHolder createHolder(ViewTransition.ViewGetter viewGetter, TValueHolder holder){
        float[] vals = new float[valueGetters.size()];
        for(int i = 0;  i < valueGetters.size(); i++){
            ValueGetter getter = valueGetters.get(i);
            if(getter instanceof ValueGetter.FromView){
                ((ValueGetter.FromView) getter).setView(viewGetter.get());
            }
            if(getter instanceof ValueGetter.FromValueHolder){
                ((ValueGetter.FromValueHolder<TValueHolder>)getter).setHolder(holder);
            }
            vals[i] = valueGetters.get(i).get();
        }
        return PropertyValuesHolder.ofFloat(propName, vals);
    }
    protected ViewPropFinalStep createFinalStep(ViewTransition.ViewGetter viewGetter, TValueHolder holder){
        ValueGetter lastValueGetter = valueGetters.get(valueGetters.size()-1);
        if(lastValueGetter instanceof ValueGetter.FromView){
            ((ValueGetter.FromView) lastValueGetter).setView(viewGetter.get());
        }
        if(lastValueGetter instanceof ValueGetter.FromValueHolder){
            ((ValueGetter.FromValueHolder<TValueHolder>)lastValueGetter).setHolder(holder);
        }
        return new ViewPropFinalStep(propName, lastValueGetter.get());
    }
    public ViewPropStep<TValueHolder> transpose() {
        if(LayoutTransitionPropertyBridges.transposeMap.containsKey(this.propName)){
            return new ViewPropStep<>(LayoutTransitionPropertyBridges.transposeMap.get(this.propName), this.valueGetters);
        }
        return this;
    }

    protected static class ViewPropFinalStep{
        public final String propName;
        public final float value;

        public ViewPropFinalStep(String propName, float value) {
            this.propName = propName;
            this.value = value;
        }
    }
}
