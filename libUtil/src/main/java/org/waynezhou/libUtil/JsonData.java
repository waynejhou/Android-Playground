package org.waynezhou.libUtil;


import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public abstract class JsonData<T extends JsonData<T>> {
    protected JsonData() {

    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (Field field : getClass().getDeclaredFields()) {
            //if (!field.isAccessible()) continue;
            stringBuilder.append(field.getType().getSimpleName());
            stringBuilder.append(" ");
            stringBuilder.append(field.getName());
            stringBuilder.append(" = ");
            try {
                stringBuilder.append(field.get(this));
            } catch (IllegalAccessException ignored) {
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    public T apply(ApplyBlock<T> applier) {
        applier.apply((T) this);
        return (T) this;
    }

    @FunctionalInterface
    public interface ApplyBlock<T extends JsonData<T>> {
        void apply(T t);
    }
}
