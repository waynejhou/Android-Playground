package org.waynezhou.libView.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.waynezhou.libUtil.reflection.ReflectionException;
import org.waynezhou.libView.LibView;

import java.util.List;

public class ViewBindingRecyclerListBinder<TItem, TViewBinding extends ViewBinding>
  extends RecyclerListBinder<TItem, ViewBindingRecyclerListBinder<TItem, TViewBinding>.ViewHolder> {
    @NonNull
    private final LayoutInflater inflater;
    @NonNull
    private final Class<TViewBinding> viewBindingClass;
    @NonNull
    private final RecyclerItemViewBindingBinder<TItem, TViewBinding> binder;
    
    public ViewBindingRecyclerListBinder(
      @NonNull LayoutInflater inflater,
      @NonNull Class<TViewBinding> viewBindingClass,
      @NonNull RecyclerItemViewBindingBinder<TItem, TViewBinding> binder) {
        this.inflater = inflater;
        this.viewBindingClass = viewBindingClass;
        this.binder = binder;
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        TViewBinding binding;
        
        public ViewHolder(@NonNull TViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    
    @NonNull
    @Override
    ViewHolder createViewHolder(@NonNull ViewGroup parent) {
        try {
            return new ViewHolder(LibView.inflate(inflater, viewBindingClass, parent, false));
        } catch (ReflectionException e) {
            throw new RuntimeException("Inflate Error", e);
        }
    }
    
    @Override
    void onBind(@NonNull ViewHolder holder, @NonNull List<TItem> items, int position) {
        binder.bind(holder.binding, items, position);
    }
}
