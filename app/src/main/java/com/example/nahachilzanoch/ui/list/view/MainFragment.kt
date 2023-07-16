package com.example.nahachilzanoch.ui.list.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.databinding.MainFragmentBinding
import com.example.nahachilzanoch.ui.activity.TaskListViewModel
import com.example.nahachilzanoch.ui.activity.view.MainActivity
import com.example.nahachilzanoch.ui.list.util.TasksAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainFragment: Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TaskListViewModel>(
        { activity as MainActivity }
    ) { viewModelFactory }

    //private val viewModel by viewModels<TaskListViewModel> { viewModelFactory }

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).mainActivityComponent.todoListComponent().manufacture()
            .inject(this)
    }

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

    private fun setupRV() {
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