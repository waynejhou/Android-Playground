package org.waynezhou.androidplayground.layout;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;

public class LayoutTemplate<TValueHolder> {
    @NonNull
    protected Runnable preAction = DelegateUtils.NothingRunnable;

    public LayoutTemplate<TValueHolder> setPreAction(@NonNull Runnable preAction) {
        this.preAction = preAction;
        return this;
    }

    @NonNull
    protected LayoutDestination<TValueHolder> dest = new LayoutDestination.Builder<TValueHolder>().build();
    public LayoutTemplate<TValueHolder> setDest(@NonNull LayoutDestination<TValueHolder> dest){
        this.dest = dest;
        return this;
    }
}
