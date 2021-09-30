package org.waynezhou.androidplayground.layout;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;

public class LayoutTemplate<TViewHolder> {
    @NonNull
    protected Runnable preAction = DelegateUtils.NothingRunnable;

    public LayoutTemplate<TViewHolder> setPreAction(@NonNull Runnable preAction) {
        this.preAction = preAction;
        return this;
    }

    @NonNull
    protected LayoutDestination<TViewHolder> dest = new LayoutDestination.Builder<TViewHolder>().build();
    public LayoutTemplate<TViewHolder> setDest(@NonNull LayoutDestination<TViewHolder> dest){
        this.dest = dest;
        return this;
    }
}
