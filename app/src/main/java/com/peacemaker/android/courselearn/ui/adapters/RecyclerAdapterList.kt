package com.peacemaker.android.courselearn.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class RecyclerAdapterList<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val onBind: (VB, T, Int) -> Unit,
    private val areItemsTheSame: (T, T) -> Boolean,
    private val filterPredicate: (T, String) -> Boolean // New parameter for filtering
) : ListAdapter<T, RecyclerAdapterList.ViewHolder<T, VB>>(DiffCallback<T>(areItemsTheSame)) {

    private var originalList: List<T> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T, VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onBind)
    }

    override fun onBindViewHolder(holder: ViewHolder<T, VB>, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun filter(query: String) {
        val filteredList = originalList.filter { item ->
            filterPredicate(item, query)
            true
        }
        submitList(filteredList)
    }

    fun setData(dataList: List<T>) {
        originalList = dataList
        submitList(dataList)
    }

    class ViewHolder<T, VB : ViewBinding>(
        private val binding: VB,
        private val onBind: (VB, T, Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, position: Int) {
            onBind(binding, item, position)
        }
    }

    private class DiffCallback<T : Any>(
        private val areItemsTheSame: (T, T) -> Boolean) : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame(oldItem, newItem)
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }
}

/**
 *
 * val adapter = RecyclerListAdapter(
{ inflater, parent, attachToParent ->
// Replace with your actual ViewBinding class
ItemLayoutBinding.inflate(inflater, parent, attachToParent)
},
{ binding, item, position ->
// Bind data to the ViewBinding components within the layout
// Example: binding.textView.text = item.title
},
{ oldItem, newItem ->
// Compare items to determine if they are the same
// Example: oldItem.id == newItem.id
},
{ item, query ->
// Customize this filtering logic based on your data and requirements
// Example: item.title.contains(query, ignoreCase = true)
}
)

recyclerView.adapter = adapter
val dataList: List<YourDataType> = // Your list of data
adapter.submitList(dataList)


val searchQuery: String = // User's search query
adapter.filter(searchQuery)

 *
 *
 * */
