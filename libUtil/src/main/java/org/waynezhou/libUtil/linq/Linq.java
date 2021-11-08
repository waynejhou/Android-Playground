package org.waynezhou.libUtil.linq;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Linq<TItem> {
    @NonNull
    private final Collection<TItem> src;
    
    public Linq(@NonNull final Collection<TItem> src) {
        this.src = src;
    }
    
    
    public Linq(@NonNull final TItem... array) {
        this(Arrays.asList(array));
    }
    
    @SuppressWarnings("unchecked")
    public Linq(@NonNull final Set<TItem> set) {
        this((TItem[]) set.toArray());
    }
    
    @NonNull
    public <TRet> Linq<TRet> select(@NonNull final Selector<TItem, TRet> selector) {
        ArrayList<TRet> ret = new ArrayList<>();
        for (TItem item : src) {
            ret.add(selector.select(item));
        }
        return new Linq<>(ret);
    }
    
    @NonNull
    public Linq<TItem> where(@NonNull final Predictor<TItem> predictor) {
        ArrayList<TItem> ret = new ArrayList<>();
        for (TItem item : src) {
            if (predictor.predict(item))
                ret.add(item);
        }
        return new Linq<TItem>(ret);
    }
    
    public boolean any(@NonNull final Predictor<TItem> predictor) {
        for (TItem item : src) {
            if (predictor.predict(item)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean all(@NonNull final Predictor<TItem> predictor) {
        for (TItem item : src) {
            if (!predictor.predict(item)) {
                return false;
            }
        }
        return true;
    }
    
    @NonNull
    public String join() {
        return this.join(", ");
    }
    
    @NonNull
    public String join(@NonNull final String sep) {
        StringBuilder builder = new StringBuilder();
        int c = 0;
        for (TItem i : this.src) {
            builder.append(i);
            if (c != this.src.size() - 1) {
                builder.append(sep);
            }
            c++;
        }
        return builder.toString();
    }
    
    public void forEach(@NonNull final RunBlock<TItem> block) {
        for (TItem item : src) {
            block.run(item);
        }
    }
    
    public int firstIndexOf(@NonNull final Predictor<TItem> predictor) {
        int ret = -1;
        for (TItem item : src) {
            ret += 1;
            if (predictor.predict(item)) {
                return ret;
            }
        }
        return -1;
    }
    
    @NonNull
    public <TMapKey, TMapVal> Map<TMapKey, TMapVal> map(
      @NonNull final Selector<TItem, TMapKey> keySelector,
      @NonNull final Selector<TItem, TMapVal> valSelector
    ) {
        HashMap<TMapKey, TMapVal> ret = new HashMap<>();
        for (TItem item : src) {
            ret.put(keySelector.select(item), valSelector.select(item));
        }
        return ret;
    }
    
    @NonNull
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

