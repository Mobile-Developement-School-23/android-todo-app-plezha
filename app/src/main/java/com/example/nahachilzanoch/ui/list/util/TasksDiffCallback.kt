package com.example.nahachilzanoch.ui.list.util

import androidx.recyclerview.widget.DiffUtil
import com.example.nahachilzanoch.data.local.Task

class TasksDiffCallback(
    private val oldItems: List<Task>,
    private val newItems: List<Task>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newItems[newItemPosition].id == oldItems[oldItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newItems[newItemPosition] == oldItems[oldItemPosition]
}