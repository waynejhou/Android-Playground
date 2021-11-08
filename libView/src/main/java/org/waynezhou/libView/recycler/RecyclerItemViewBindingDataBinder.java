package org.waynezhou.libView.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

@FunctionalInterface
public interface RecyclerItemViewBindingDataBinder<TItem, TViewBinding extends ViewBinding, TViewHolder extends RecyclerView.ViewHolder> {
    void bind(@NonNull TViewBinding binding, TItem item, @NonNull TViewHolder holder);
}
