package org.waynezhou.libView.recycler;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.reflection.ReflectionException;
import org.waynezhou.libView.LibView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class RecyclerList<TItem, TViewHolder extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<TViewHolder>
    implements List<TItem>
{
    private final AppCompatActivity activity;
    private final RecyclerListBinder<TItem, TViewHolder> binder;
    private final List<TItem> source;
    public RecyclerList(AppCompatActivity activity, RecyclerListBinder<TItem, TViewHolder> binder, List<TItem> keeper){
        this.activity = activity;
        this.binder = binder;
        this.source = new LinkedList<>(keeper);
    }
    public RecyclerList(AppCompatActivity activity, RecyclerListBinder<TItem, TViewHolder> binder){
        this(activity, binder, new ArrayList<>());
    }
    
    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return binder.createViewHolder(parent);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {
        binder.onBind(holder, source, position);
    }
    
    @Override
    public int getItemCount() {
        return size();
    }
    
    
    // region list
    // region region readonly methods of implement [List<TItem>]
    @Override
    public int size() {
        return source.size();
    }
    
    @Override
    public boolean isEmpty() {
        return source.isEmpty();
    }
    
    @Override
    public boolean contains(@Nullable Object o) {
        return source.contains(o);
    }
    
    @NonNull
    @Override
    public Iterator<TItem> iterator() {
        return source.iterator();
    }
    
    @NonNull
    @Override
    public Object[] toArray() {
        return source.toArray();
    }
    
    @SuppressWarnings("SuspiciousToArrayCall")
    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return source.toArray(a);
    }
    
    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return source.containsAll(c);
    }
    
    @Override
    public TItem get(int index) {
        return source.get(index);
    }
    
    @Override
    public int indexOf(@Nullable Object o) {
        return source.indexOf(o);
    }
    
    @Override
    public int lastIndexOf(@Nullable Object o) {
        return source.lastIndexOf(o);
    }
    
    @NonNull
    @Override
    public ListIterator<TItem> listIterator() {
        return source.listIterator();
    }
    
    @NonNull
    @Override
    public ListIterator<TItem> listIterator(int index) {
        return source.listIterator(index);
    }
    
    @NonNull
    @Override
    public List<TItem> subList(int fromIndex, int toIndex) {
        return source.subList(fromIndex, toIndex);
    }
    // endregion
    
    // region mutable methods of implement [MutableList<TItem>]
    // with injected notify index
    @Override
    public boolean add(TItem tItem) {
        boolean result = source.add(tItem);
        activity.runOnUiThread(() -> notifyItemInserted(source.size() - 1));
        return result;
    }
    
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean remove(@Nullable Object o) {
        int removedIdx = source.indexOf(o);
        if(removedIdx==-1) return false;
        boolean result = source.remove(o);
        activity.runOnUiThread(() -> notifyItemRemoved(removedIdx));
        return result;
    }
    
    @Override
    public boolean addAll(@NonNull Collection<? extends TItem> c) {
        int from = source.size();
        boolean result = source.addAll(c);
        activity.runOnUiThread(() -> notifyItemRangeInserted(from, c.size() ));
        return result;
    }
    
    @Override
    public boolean addAll(int index, @NonNull Collection<? extends TItem> c) {
        boolean result = source.addAll(index, c);
        activity.runOnUiThread(() -> notifyItemRangeInserted(index,  c.size() ));
        return result;
    }
    
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        boolean result = source.removeAll(c);
        activity.runOnUiThread(this::notifyDataSetChanged);
        return result;
    }
    
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        boolean result = source.retainAll(c);
        activity.runOnUiThread(this::notifyDataSetChanged);
        return result;
    }
    
    @Override
    public void clear() {
        source.clear();
        activity.runOnUiThread(this::notifyDataSetChanged);
    }
    
    @Override
    public TItem set(int index, TItem element) {
        TItem result = source.set(index, element);
        activity.runOnUiThread(() -> notifyItemChanged(index));
        return result;
    }
    
    @Override
    public void add(int index, TItem element) {
        source.add(index, element);
        activity.runOnUiThread(() -> notifyItemInserted(index));
    }
    
    @Override
    public TItem remove(int index) {
        TItem result = source.remove(index);
        activity.runOnUiThread(() -> notifyItemRemoved(index));
        return result;
    }
    // endregion
    // endregion
}

