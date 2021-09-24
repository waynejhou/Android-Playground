package org.waynezhou.libUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Linq<TItem> {
    private final Collection<TItem> src;

    public Linq(Collection<TItem> src) {
        this.src = src;
    }

    public Linq(TItem[] array) {
        this(Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public Linq(Set<TItem> set) {
        this((TItem[]) set.toArray());
    }

    public <TRet> Linq<TRet> select(Selector<TItem, TRet> selector) {
        ArrayList<TRet> ret = new ArrayList<>();
        for (TItem item : src) {
            ret.add(selector.select(item));
        }
        return new Linq<>(ret);
    }

    public Linq<TItem> where(Predictor<TItem> predictor) {
        ArrayList<TItem> ret = new ArrayList<>();
        for (TItem item : src) {
            if (predictor.predict(item))
                ret.add(item);
        }
        return new Linq<TItem>(ret);
    }

    public boolean any(Predictor<TItem> predictor) {
        for (TItem item : src) {
            if (predictor.predict(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean all(Predictor<TItem> predictor) {
        for (TItem item : src) {
            if (!predictor.predict(item)) {
                return false;
            }
        }
        return true;
    }

    public String join(){
        return this.join(", ");
    }

    public String join(String sep){
        StringBuilder builder = new StringBuilder();
        int c = 0;
        for(TItem i : this.src){
            builder.append(i);
            if(c!=this.src.size()-1){
                builder.append(sep);
            }
            c++;
        }
        return builder.toString();
    }

    public void forEach(RunBlock<TItem> block) {
        for (TItem item : src) {
            block.run(item);
        }
    }

    public int firstIndexOf(Predictor<TItem> predictor){
        int ret = -1;
        for(TItem item: src){
            ret += 1;
            if(predictor.predict(item)){
                return ret;
            }
        }
        return -1;
    }

    public <TMapKey, TMapVal> Map<TMapKey, TMapVal> map(
            Selector<TItem, TMapKey> keySelector, Selector<TItem, TMapVal> valSelector){
        HashMap<TMapKey, TMapVal> ret = new HashMap<>();
        for (TItem item : src) {
            ret.put(keySelector.select(item), valSelector.select(item));
        }
        return ret;
    }

    public List<TItem> list() {
        return new ArrayList<>(src);
    }

    @FunctionalInterface
    public interface RunBlock<T> {
        void run(T t);
    }

    @FunctionalInterface
    public interface Predictor<T> {
        boolean predict(T t);
    }

    @FunctionalInterface
    public interface Selector<T, R> {
        R select(T t);
    }

}

