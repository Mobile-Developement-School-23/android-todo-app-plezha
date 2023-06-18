package com.example.nahachilzanoch.screens

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.util.TodoItemsAdapter
import com.example.nahachilzanoch.util.TodoListViewModel
import com.example.nahachilzanoch.databinding.MainFragmentBinding
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
        val showTextView = binding.show
        val todoItemsAdapter = TodoItemsAdapter(
            onCheckBoxClicked = { viewModel.addOrChangeItem(it.copy(done = !it.done)) },
            onItemClicked = { item, view ->
                val bundle = Bundle().apply {
                    putSerializable("todoItem", item)
                }
                Navigation.findNavController(view).navigate(
                    R.id.action_fragment1_to_fragment2, bundle
                )
            }
        )

        todosRV.adapter = todoItemsAdapter

        if (showTextView.text == "Show") {
            viewModel.showPredicate.value = { true }
            showTextView.text = "Hide"
        } else {
            viewModel.showPredicate.value = { !it.done }
            showTextView.text = "Show"
        }

        todosRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        viewLifecycleOwner.lifecycleScope.launch() {
            launch {
                viewModel.todoList.collect {
                        todoItemsAdapter.todoItems = it
                }
            }
            launch {
                viewModel.completedAmount.collect {
                    binding.completed.text = "Completed - $it"
                }
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
            if (showTextView.text == "Show") {
                viewModel.showPredicate.value = { true }
                showTextView.text = "Hide"
            } else {
                viewModel.showPredicate.value = { !it.done }
                showTextView.text = "Show"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}