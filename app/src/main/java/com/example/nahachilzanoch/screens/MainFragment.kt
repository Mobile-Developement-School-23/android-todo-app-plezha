package com.example.nahachilzanoch.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.databinding.MainFragmentBinding
import com.example.nahachilzanoch.util.TaskListViewModel
import com.example.nahachilzanoch.util.TasksAdapter
import kotlinx.coroutines.launch

class MainFragment: Fragment() {
    private val viewModel by activityViewModels<TaskListViewModel> { TaskListViewModel.Factory }

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        setupRV()
        val showTextView = binding.show

        if (showTextView.text == "Show") {
            viewModel.showPredicate.value = { true }
            showTextView.text = "Hide"
        } else {
            viewModel.showPredicate.value = { !it.isDone }
            showTextView.text = "Show"
        }



        viewLifecycleOwner.lifecycleScope.launch {
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

        binding.refreshTasks.setOnClickListener {
            viewModel.updateFromRemote()
        }

        showTextView.setOnClickListener {
            if (showTextView.text == "Show") {
                viewModel.showPredicate.value = { true }
                showTextView.text = "Hide"
            } else {
                viewModel.showPredicate.value = { !it.isDone }
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

    fun setupRV() {
        val tasksRV = binding.tasks
        val tasksAdapter = TasksAdapter(
            onCheckBoxClicked = { viewModel.updateCompleted(it.id) },
            onItemClicked = { task, view ->
                val bundle = Bundle().apply {
                    putSerializable("task", task)
                }
                Navigation.findNavController(view).navigate(
                    R.id.action_fragment1_to_fragment2, bundle
                )
            }
        )
        tasksRV.adapter = tasksAdapter
        tasksRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycleScope.launch{
            launch {
                viewModel.taskList.collect {
                    tasksAdapter.tasks = it
                }
            }
        }

    }
}