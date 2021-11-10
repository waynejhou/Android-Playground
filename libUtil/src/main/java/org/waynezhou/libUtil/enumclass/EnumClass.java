package org.waynezhou.libUtil.enumclass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EnumClass<T> {
    public final T identifier;
    public final String statement;
    private final Class<T> clz;
    private static Map<?,?> map = new HashMap<>();
    @SuppressWarnings("unchecked")
    private static <T> Map<Class<EnumClass<T>>,  Map<T, EnumClass<T>>> getMap(Class<T> typeHint){
        return (Map< Class<EnumClass<T>>,  Map<T, EnumClass<T>> >) map;
    }
    
    @SuppressWarnings("unchecked")
    public static <T, TEnumClass extends EnumClass<T>> Map<T, TEnumClass> getMap(Class<TEnumClass> enumClass, Class<T> typeHint){
        if(!getMap(typeHint).containsKey(enumClass))
            putMap((Class<EnumClass<T>>)enumClass, typeHint, Collections.unmodifiableMap(new HashMap<>()));
        return (Map<T, TEnumClass>) getMap(typeHint).get(enumClass);
    }
    private static <T> void putMap(Class<EnumClass<T>> enumClass, Class<T> typeHint, Map<T, EnumClass<T>> map){
        getMap(typeHint).put(enumClass, map);
    }
    
    @SuppressWarnings("unchecked")
    protected EnumClass(Class<T> clz, T identifier, String statement) {
        this.clz = clz;
        this.identifier = identifier;
        this.statement = statement;
        final Map<T, EnumClass<T>> map = new HashMap<>(getMap((Class<EnumClass<T>>)this.getClass(), clz));
        map.put(identifier, this);
        putMap((Class<EnumClass<T>>)this.getClass(), clz, Collections.unmodifiableMap(map));
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof EnumClass)) return false;
        if((((EnumClass<?>) obj).identifier.getClass() != clz)) return false;

        //noinspection unchecked
        return identifier.equals(((EnumClass<T>) obj).identifier);
    }

    @NonNull
    @Override
    public String toString() {
        return statement;
    }

    public static abstract class Int extends EnumClass<Integer>{
        public static <TEnumClass extends Int> Map<Integer, TEnumClass> getMap(Class<TEnumClass> enumClass){
            return EnumClass.getMap(enumClass, Integer.class);
        }
        protected Int(int identifier, String statement) {
            super(Integer.class, identifier, statement);
        }
        protected Int(String statement) {
            super(Integer.class, statement.hashCode(), statement);
        }
    }
}


