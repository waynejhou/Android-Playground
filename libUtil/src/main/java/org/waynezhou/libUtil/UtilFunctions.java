package org.waynezhou.libUtil;

public final class UtilFunctions {
    private UtilFunctions(){}
    @FunctionalInterface
    public interface Getter<T>{
        T get();
    }
    @FunctionalInterface
    public interface Setter<T>{
        void set(T value);
    }
    @FunctionalInterface
    public interface GetterGetter<T, P>{
        Getter<P> get(T t);
    }
    @FunctionalInterface
    public interface SetterGetter<T, P>{
        Setter<P> get(T t);
    }
    public static <T, P> void setIfNotEqual(T owner, GetterGetter<T, P> getterGetter, P value, SetterGetter<T, P> setterGetter){
        if (!getterGetter.get(owner).get().equals(value)) {
            setterGetter.get(owner).set(value);
        }
    }

}
