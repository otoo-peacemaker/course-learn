package com.peacemaker.android.courselearn.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.util.*

class RecyclerBaseAdapter<T : Any> : ListAdapter<T, BaseViewHolder<T>>(ItemDiffCallback<T>()),Filterable {

    private var originalList: MutableList<T> = mutableListOf()
    private var filteredList: MutableList<T> = mutableListOf()
    private var comparator: Comparator<T>? = null

    var expressionViewHolderBinding: ((T, ViewBinding) -> Unit)? = null
    var expressionOnCreateViewHolder: ((LayoutInflater, ViewGroup) -> ViewBinding)? = null

    init {
        setHasStableIds(true)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = expressionOnCreateViewHolder?.invoke(inflater, parent)
            ?: throw IllegalStateException("expressionOnCreateViewHolder must be provided.")
        return BaseViewHolder(binding, expressionViewHolderBinding ?: { _, _ -> })
    }
    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val lowercaseQuery = query.lowercase(Locale.getDefault())
        val filteredData = currentList.filter { item ->
            item.toString().lowercase(Locale.getDefault()).contains(lowercaseQuery)
        }
        submitList(filteredData.toMutableList())
        applySort()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setComparator(comparator: Comparator<T>?) {
        this.comparator = comparator
        applySort()
        notifyDataSetChanged()
    }
    private fun applySort() {
        comparator?.let { cmp ->
            val sortedList = currentList.sortedWith(cmp)
            submitList(sortedList.toMutableList())
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase(Locale.getDefault()) ?: ""
                filteredList = if (query.isEmpty()) {
                    originalList.toMutableList()
                } else {
                    originalList.filter { item ->
                        item.toString().lowercase(Locale.getDefault()).contains(query)
                    }.toMutableList()
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredList = results?.values as MutableList<T>
                submitList(filteredList.toMutableList())
                notifyDataSetChanged()
            }
        }
    }
    interface OnPlayPauseClickListener {
        fun onPlayPauseClick(position: Int)
    }

}

class BaseViewHolder<T> internal constructor(
    private val binding: ViewBinding,
    private val expression: (T, ViewBinding) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
        expression(item, binding)
    }
}

class ItemDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}