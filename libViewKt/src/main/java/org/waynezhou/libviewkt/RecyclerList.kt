package org.waynezhou.libviewkt


import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.reflection.ReflectionException

class RecyclerList<TItem, TItemViewBinding : ViewBinding>
constructor(
    private val activity: AppCompatActivity,
    private val itemViewBindingClass: Class<TItemViewBinding>,
    keeper: List<TItem> = listOf(),
) : RecyclerView.Adapter<RecyclerList<TItem, TItemViewBinding>.ViewHolder>(), MutableList<TItem> {
    private val source = keeper.toMutableList()

    private var binder: (binding: TItemViewBinding, source: List<TItem>, position: Int) -> Unit =
        { _, _, _ -> }

    fun onBind(binder: (binding: TItemViewBinding, source: List<TItem>, position: Int) -> Unit){
        this.binder = binder
    }

    inner class ViewHolder constructor(
        private val binding: TItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(source: List<TItem>, position: Int) {
            binder(binding, source, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LibView.inflate(
                activity.layoutInflater,
                itemViewBindingClass,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, position)
    }

    override fun getItemCount(): Int {
        return size;
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