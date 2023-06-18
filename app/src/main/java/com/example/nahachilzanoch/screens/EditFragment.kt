package com.example.nahachilzanoch.screens

import android.app.DatePickerDialog
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.util.TodoListViewModel
import com.example.nahachilzanoch.data.TodosRepository
import com.example.nahachilzanoch.databinding.EditFragmentBinding
import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency
import com.example.nahachilzanoch.util.getDate
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*



class EditFragment : Fragment() {
    private val viewModel by activityViewModels<TodoListViewModel>()

    private var _binding: EditFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        var deadlineTime = Calendar.getInstance().time.time

        binding.deadlineDate.paintFlags = binding.deadlineDate.paintFlags + Paint.UNDERLINE_TEXT_FLAG

        val todoItem =
            if (arguments != null) {
                requireArguments().getSerializable("todoItem") as TodoItem
            } else {
                TodoItem(
                    id = TodosRepository.suitableId,
                    creationDate = Calendar.getInstance().time.time,
                    done = false,
                    text = "",
                    urgency = Urgency.NORMAL
                )
            }

        binding.todoText.setText(todoItem.text)

        binding.toggleButton.check(
            when (todoItem.urgency) {
                Urgency.LOW -> R.id.buttonUrgencyLow
                Urgency.NORMAL -> R.id.buttonUrgencyNormal
                Urgency.URGENT -> R.id.buttonUrgencyUrgent
            }
        )
        if (todoItem.deadline != null) {
            binding.deadlineSwitch.isChecked = true
            binding.deadlinePickerLine.isVisible = true
            binding.deadlineDate.text = todoItem.deadline.getDate()
        } else {
            binding.deadlinePickerLine.isVisible = false
        }

        binding.deadlineDate.setOnClickListener {
            MaterialDatePicker.Builder.datePicker()
                .setSelection(
                MaterialDatePicker.todayInUtcMilliseconds()
            )
            datePicker.show(parentFragmentManager, "")
        }

        datePicker.addOnPositiveButtonClickListener {
            deadlineTime = it
            binding.deadlineDate.text = it.getDate()
        }


        binding.backTextView.setOnClickListener {
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.delete.setOnClickListener {
            viewModel.deleteItem(todoItem)
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.save.setOnClickListener {
            viewModel.addOrChangeItem(
                todoItem.copy(
                    text = binding.todoText.text.toString(),
                    urgency = when (binding.toggleButton.checkedButtonId) {
                        R.id.buttonUrgencyLow -> Urgency.LOW
                        R.id.buttonUrgencyNormal -> Urgency.NORMAL
                        R.id.buttonUrgencyUrgent -> Urgency.URGENT
                        else -> Urgency.NORMAL // Any way to delete that stub?
                    },
                    deadline = deadlineTime,
                    lastEditTime = Calendar.getInstance().time.time,
                )
            )
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            deadlineTime = Calendar.getInstance().time.time
            binding.deadlineDate.text = deadlineTime.getDate()
            binding.deadlinePickerLine.isVisible = isChecked
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}