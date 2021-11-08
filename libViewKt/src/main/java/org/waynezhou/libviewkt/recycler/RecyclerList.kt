package org.waynezhou.libviewkt.recycler


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.waynezhou.libviewkt.LibView.inflate

class RecyclerList<TItem, TViewHolder: RecyclerView.ViewHolder>
constructor(
    private val activity: AppCompatActivity,
    private val binder: RecyclerListBinder<TItem, TViewHolder>,
    keeper: List<TItem> = listOf(),
) : RecyclerView.Adapter<TViewHolder>(), MutableList<TItem> {
    private val source = keeper.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder {
        return binder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
        binder.onBind(holder, source, position)
    }

    override fun getItemCount(): Int {
        return size
    }

    //region list readonly implement
    override val size: Int
        get() = source.size

    override fun contains(element: TItem): Boolean = source.contains(element)

    override fun containsAll(elements: Collection<TItem>): Boolean = source.containsAll(elements)

    override fun get(index: Int): TItem = source[index]

    override fun indexOf(element: TItem): Int  = source.indexOf(element)

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun iterator(): MutableIterator<TItem> = source.iterator()

    override fun lastIndexOf(element: TItem): Int = source.lastIndexOf(element)


    override fun listIterator(): MutableListIterator<TItem> = source.listIterator()

    override fun listIterator(index: Int): MutableListIterator<TItem> = source.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TItem>  = source.subList(fromIndex, toIndex)

    //endregion

    // region list mutable implement with notify
    override fun add(element: TItem): Boolean = source.add(element).apply {
        activity.runOnUiThread{notifyItemChanged(size-1)}
    }

    override fun add(index: Int, element: TItem) = source.add(index, element).apply {
        activity.runOnUiThread{notifyItemChanged(index)}
    }

    override fun addAll(index: Int, elements: Collection<TItem>): Boolean = source.addAll(index, elements).apply {
        activity.runOnUiThread{notifyItemRangeChanged(index, elements.size)}
    }

    override fun addAll(elements: Collection<TItem>): Boolean = source.addAll(elements).apply {
        activity.runOnUiThread{notifyItemRangeChanged(size-elements.size, elements.size)}
    }

    override fun clear() {
        val count = size
        source.clear()
        activity.runOnUiThread{notifyItemRangeRemoved(0, count)}
    }

    override fun remove(element: TItem): Boolean {
        val idx = source.indexOf(element)
        val ret = source.remove(element)
        activity.runOnUiThread{notifyItemRemoved(idx)}
        return ret
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeAll(elements: Collection<TItem>): Boolean = source.removeAll(elements).run {
        activity.runOnUiThread { notifyDataSetChanged() }
        this
    }

    override fun removeAt(index: Int): TItem = source.removeAt(index).run {
        activity.runOnUiThread { notifyItemRemoved(index) }
        this
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun retainAll(elements: Collection<TItem>): Boolean= source.removeAll(elements).run {
        activity.runOnUiThread { notifyDataSetChanged() }
        this
    }

    override fun set(index: Int, element: TItem): TItem = source.set(index, element).run {
        activity.runOnUiThread { notifyItemChanged(index) }
        this
    }
    // endregion
}

abstract class RecyclerListBinder<TItem, TViewHolder: RecyclerView.ViewHolder>{
    internal abstract fun createViewHolder(parent: ViewGroup): TViewHolder
    internal abstract fun onBind(holder: TViewHolder, items:List<TItem>, position: Int)
}

open class ViewBindingRecyclerListBinder<TItem, TViewBinding: ViewBinding>
    (private val inflater: LayoutInflater, private val viewBindingClass: Class<TViewBinding>, private val binder: (TViewBinding, List<TItem>, position:Int)->Unit)
    : RecyclerListBinder<TItem, ViewBindingRecyclerListBinder<TItem, TViewBinding>.ViewHolder>(){

    inner class ViewHolder(var binding: TViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(viewBindingClass, parent, false))
    }

    override fun onBind(holder: ViewHolder, items: List<TItem>, position: Int) {
        binder(holder.binding, items, position)
    }
}

open class ViewDataBindingRecyclerListBinder<TItem, TViewBinding: ViewDataBinding>
    ( inflater: LayoutInflater, viewBindingClass: Class<TViewBinding>)
    : ViewBindingRecyclerListBinder<TItem, TViewBinding>(inflater, viewBindingClass, { _, _, _ -> }) {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        val holder = super.createViewHolder(parent)
        holder.binding = DataBindingUtil.bind(holder.itemView)!!
        holder.binding.createListener()
        holder.binding.executePendingBindings()
        return holder
    }

    private var createListener: (TViewBinding.()->Unit) = {}
    fun onCreate(listener:  (TViewBinding.()->Unit)){
        createListener = listener
    }

    private var bindingListener: (TViewBinding.(item:TItem, holder: ViewHolder)->Unit) = { _, _ ->}

    fun onBind(listener:  (TViewBinding.(item:TItem, holder: ViewHolder)->Unit)){
        bindingListener = listener
    }

    override fun onBind(holder: ViewHolder, items: List<TItem>, position: Int) {
        bindingListener(holder.binding, items[position], holder)
        holder.binding.executePendingBindings()
    }
}
