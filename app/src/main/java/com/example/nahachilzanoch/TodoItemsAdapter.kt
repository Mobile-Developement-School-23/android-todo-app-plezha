package com.example.nahachilzanoch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nahachilzanoch.databinding.TodoItemBinding
import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency

class TodoItemsAdapter :
    RecyclerView.Adapter<TodoItemsAdapter.TodoItemViewHolder>() {
    lateinit var viewModel: TodoListViewModel // Is it ok?

    class TodoItemViewHolder(
        itemView: View,
        private val viewModel: TodoListViewModel
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
                dateLine.visibility = View.GONE
            else {
                dateTextView.text = todoItem.deadline.toString()
            }

            urgencyImage.visibility =
                if (todoItem.urgency == Urgency.LOW) View.VISIBLE else View.GONE

            urgencyTextView.visibility =
                if (todoItem.urgency == Urgency.URGENT) View.VISIBLE else View.GONE

            todoTextView.text = todoItem.text


            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("todoItem", todoItem)
                }

                Navigation.findNavController(itemView).navigate(
                    R.id.action_fragment1_to_fragment2, bundle
                )
            }

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.addOrChangeItem(todoItem.copy(done = isChecked))
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
            viewModel
        )

    }

    override fun getItemCount() = todoItems.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(todoItems[position])
    }

}

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