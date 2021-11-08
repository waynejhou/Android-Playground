package org.waynezhou.libView;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RecyclerList<TItem, TViewBinding extends ViewBinding>
        extends RecyclerView.Adapter<RecyclerList<TItem, TViewBinding>.ViewHolder>
        implements List<TItem> {

    private final AppCompatActivity activity;
    private final Class<TViewBinding> viewBindingType;

    private RecyclerList(
            final List<TItem> keeper,
            final AppCompatActivity activity,
            final Class<TViewBinding> viewBindingType,
            final RecyclerItemViewBindingBinder<TItem, TViewBinding> binder,
            final RecyclerItemClickListener onClickListener,
            final RecyclerItemClickListener onLongClickListener) {
        super();
        this.source = keeper;
        this.activity = activity;
        this.viewBindingType = viewBindingType;
        this.binder = binder;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    // region builder
    public static class Builder<TItem, TViewBinding extends ViewBinding> {
        public Builder(Class<TViewBinding> viewBindingType) {
            this.viewBindingType = viewBindingType;
        }

        private final Class<TViewBinding> viewBindingType;
        private List<TItem> source = new ArrayList<>();
        private RecyclerItemViewBindingBinder<TItem, TViewBinding> itemUpdateListener = ($1, $2, $3) -> {
        };
        private RecyclerViewLayoutManagerConfiguration layoutManagerConfiguration = (view, activity) -> {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            view.setLayoutManager(linearLayoutManager);
            view.addItemDecoration(new DividerItemDecoration(activity, linearLayoutManager.getOrientation()));
        };
        private RecyclerItemClickListener onClickListener = ($1, $2) -> {
        };
        private RecyclerItemClickListener onLongClickListener = ($1, $2) -> {
        };

        public Builder<TItem, TViewBinding> setSource(List<TItem> source) {
            this.source = source;
            return this;
        }

        public Builder<TItem, TViewBinding> setUpdate(RecyclerItemViewBindingBinder<TItem, TViewBinding> listener) {
            this.itemUpdateListener = listener;
            return this;
        }

        public Builder<TItem, TViewBinding> setLayoutManagerConfiguration(RecyclerViewLayoutManagerConfiguration configuration) {
            this.layoutManagerConfiguration = configuration;
            return this;
        }

        public Builder<TItem, TViewBinding> setClick(RecyclerItemClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        public Builder<TItem, TViewBinding> setLongClick(RecyclerItemClickListener listener) {
            this.onLongClickListener = listener;
            return this;
        }

        public RecyclerList<TItem, TViewBinding> buildOn(
                AppCompatActivity activity,
                RecyclerView view
        ) {
            RecyclerList<TItem, TViewBinding> list = new RecyclerList<>(
                    source,
                    activity,
                    viewBindingType,
                    itemUpdateListener,
                    onClickListener,
                    onLongClickListener
            );
            view.setAdapter(list);
            layoutManagerConfiguration.config(view, activity);
            view.addOnItemTouchListener(
                    new RecyclerList.ItemClickListener(view, onClickListener, onLongClickListener)
            );
            return list;
        }
    }
    // endregion

    // region adapter

    private final RecyclerItemViewBindingBinder<TItem, TViewBinding> binder;

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TViewBinding binding;
        public ViewHolder( TViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(List<TItem> source, int position) {
            binder.bind(binding, source, position);
        }
    }


    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            return new ViewHolder(LibView.inflate(activity.getLayoutInflater(), viewBindingType, parent, false));
        } catch (ReflectionException e) {
            LogHelper.e("Can't reflect ViewBinding Class.", e);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerList<TItem, TViewBinding>.ViewHolder holder, int position) {
        holder.bind(this, position);
    }

    @Override
    public int getItemCount() {
        return source.size();
    }
    // endregion

    // region list
    private final List<TItem> source;

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

    @Override
    public boolean remove(@Nullable Object o) {
        boolean result = source.remove(o);
        activity.runOnUiThread(this::notifyDataSetChanged);
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

    // region ItemClickListener
    private final RecyclerItemClickListener onClickListener;
    private final RecyclerItemClickListener onLongClickListener;

    private static class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
        private final GestureDetectorCompat gestureDetector;

        public ItemClickListener(final RecyclerView view,
                                 final RecyclerItemClickListener onClickListener,
                                 final RecyclerItemClickListener onLongClickListener) {
            super();
            gestureDetector = new GestureDetectorCompat(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View childView = view.findChildViewUnder(e.getX(), e.getY());
                    onClickListener.invoke(childView, view.getChildAdapterPosition(childView));
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = view.findChildViewUnder(e.getX(), e.getY());
                    onLongClickListener.invoke(childView, view.getChildAdapterPosition(childView));
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            gestureDetector.onTouchEvent(e);
            return false;
        }
    }
    // endregion
}
