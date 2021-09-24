package org.waynezhou.libView;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.util.List;

@FunctionalInterface
public interface RecyclerItemViewBindingBinder<TItem, TViewBinding extends ViewBinding> {
    void bind(@NonNull TViewBinding binding, @NonNull List<TItem> source, @NonNull int position);
}
