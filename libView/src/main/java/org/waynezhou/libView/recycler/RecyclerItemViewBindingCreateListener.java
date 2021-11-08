package org.waynezhou.libView.recycler;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

@FunctionalInterface
public interface RecyclerItemViewBindingCreateListener<TViewBinding extends ViewBinding> {
    void create(@NonNull TViewBinding binding);
}
