package com.example.nahachilzanoch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nahachilzanoch.databinding.MainFragmentBinding
import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment: Fragment() {
    private val viewModel by activityViewModels<TodoListViewModel>()

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        val todosRV = binding.todos
        val todoItemsAdapter = TodoItemsAdapter()
        todosRV.adapter = todoItemsAdapter

        todosRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todoList.collect {
                println("$it")
                todoItemsAdapter.todoItems = it
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showTextView = binding.show

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_fragment1_to_fragment2)
        }

        showTextView.setOnClickListener {

            showTextView.text = if (showTextView.text == "Show") "Hide" else "Show"
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TodoItemsAdapter :
RecyclerView.Adapter<TodoItemsAdapter.TodoItemViewHolder>() {

    class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // TODO: mb make it with `val item = TodoItemBinding`?
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
                )
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
        newItems[newItemPosition] == oldItems[oldItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newItems[newItemPosition] == oldItems[oldItemPosition]

}