package org.waynezhou.libView.recycler;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerListBinder<TItem, TViewHolder extends RecyclerView.ViewHolder> {
    
    @NonNull
    abstract TViewHolder createViewHolder(@NonNull ViewGroup parent);
    abstract void onBind(@NonNull TViewHolder holder, @NonNull List<TItem> items, int position);
}
