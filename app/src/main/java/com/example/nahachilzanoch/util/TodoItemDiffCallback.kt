package com.example.nahachilzanoch.util

import androidx.recyclerview.widget.DiffUtil
import com.example.nahachilzanoch.model.TodoItem

class TodoItemDiffCallback(
    private val oldItems: List<TodoItem>,
    private val newItems: List<TodoItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newItems[newItemPosition].id == oldItems[oldItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newItems[newItemPosition] == oldItems[oldItemPosition]
}