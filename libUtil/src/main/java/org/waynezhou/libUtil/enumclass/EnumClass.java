package org.waynezhou.libUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class EnumClass<T> {
    public final T identifier;
    public final String statement;
    private final Class<T> clz;
    private static Map<Object, EnumClass<?>> map = new HashMap<>();
    public static Map<Object, EnumClass<?>> getMap(){
        return map;
    }
    protected EnumClass(Class<T> clz, T identifier, String statement) {
        this.clz = clz;
        this.identifier = identifier;
        this.statement = statement;
        map = new HashMap<>(map);
        map.put((Object) this.identifier, this);
        map = Collections.unmodifiableMap(map);
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
        return statement.toString();
    }

    public static abstract class Int extends EnumClass<Integer>{

        protected Int(int identifier, String statement) {
            super(Integer.class, identifier, statement);
        }
        protected Int(String statement) {
            super(Integer.class, statement.hashCode(), statement);
        }
    }
}


