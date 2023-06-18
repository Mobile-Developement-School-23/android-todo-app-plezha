package com.example.nahachilzanoch.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency

class TodoItemsAdapter(
    private val onCheckBoxClicked: (TodoItem) -> Unit,
    private val onItemClicked: (TodoItem, View) -> Unit
) :
    RecyclerView.Adapter<TodoItemsAdapter.TodoItemViewHolder>() {

    class TodoItemViewHolder(
        itemView: View,
        private val onCheckBoxClicked: (TodoItem) -> Unit,
        private val onItemClicked: (TodoItem, View) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val dateTextView = itemView.findViewById<TextView>(R.id.date_text)
        private val todoTextView = itemView.findViewById<TextView>(R.id.todo_text)
        private val dateLine = itemView.findViewById<LinearLayout>(R.id.dateLine)
        private val urgencyImage = itemView.findViewById<ImageView>(R.id.urgencyImage)
        private val urgencyTextView = itemView.findViewById<TextView>(R.id.urgencyTextView)

        fun bind(todoItem: TodoItem) {
            checkbox.isChecked = todoItem.done

            if (todoItem.deadline == null)
                dateLine.isVisible = false
            else {
                dateTextView.text = todoItem.deadline.getDate()
            }

            urgencyImage.isVisible = todoItem.urgency == Urgency.LOW

            urgencyTextView.isVisible = todoItem.urgency == Urgency.URGENT

            todoTextView.text = todoItem.text

            itemView.setOnClickListener {
                onItemClicked(todoItem, itemView)
            }

            checkbox.setOnClickListener {
                onCheckBoxClicked(todoItem)
            }
        }
    }

    var todoItems = listOf<TodoItem>()
        set(value) {
            DiffUtil.calculateDiff(
                TodoItemDiffCallback(
                    field,
                    value,
                )
            ).dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        return TodoItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.todo_item,
                    parent,
                    false
                ),
            onCheckBoxClicked,
            onItemClicked
        )

    }

    override fun getItemCount() = todoItems.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(todoItems[position])
    }

}