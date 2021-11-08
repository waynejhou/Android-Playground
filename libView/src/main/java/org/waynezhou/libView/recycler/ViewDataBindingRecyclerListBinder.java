package org.waynezhou.libView.recycler;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.List;
import java.util.Objects;

public class ViewDataBindingRecyclerListBinder<TItem, TViewBinding extends ViewDataBinding>
    extends ViewBindingRecyclerListBinder<TItem, TViewBinding>
{
    ViewDataBindingRecyclerListBinder(
      LayoutInflater inflater,
      Class<TViewBinding> viewBindingClass
    ){
        super(inflater, viewBindingClass, ($0,$1,$2)->{});
        
    }
    
    @NonNull
    @Override
    ViewHolder createViewHolder(@NonNull ViewGroup parent) {
        ViewHolder holder = super.createViewHolder(parent);
        holder.binding = Objects.requireNonNull(DataBindingUtil.bind(holder.itemView));
        createListener.create(holder.binding);
        holder.binding.executePendingBindings();
        return holder;
    }
    
    @NonNull
    private RecyclerItemViewBindingCreateListener<TViewBinding> createListener = $->{};
    void onCreate(@NonNull RecyclerItemViewBindingCreateListener<TViewBinding> listener){
        createListener = listener;
    }
    
    @NonNull
    private RecyclerItemViewBindingDataBinder<TItem, TViewBinding, ViewHolder> bindingListener = ($0, $1, $2)->{};
    
    void onBind(@NonNull RecyclerItemViewBindingDataBinder<TItem, TViewBinding, ViewHolder> listener){
        bindingListener = listener;
    }
    
    @Override
    void onBind(@NonNull ViewHolder holder, @NonNull List<TItem> items, int position) {
        bindingListener.bind(holder.binding, items.get(position), holder);
        holder.binding.executePendingBindings();
    }
}
