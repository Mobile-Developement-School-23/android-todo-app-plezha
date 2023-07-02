package com.example.nahachilzanoch.screens

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.util.TaskListViewModel
import com.example.nahachilzanoch.databinding.EditFragmentBinding
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.local.Urgency
import com.example.nahachilzanoch.util.getDate
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*



class EditFragment : Fragment() {
    private val viewModel by activityViewModels<TaskListViewModel> { TaskListViewModel.Factory }

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

    private fun setupDatePicker(
        onDeadlineChange: (Long) -> Unit
    ): MaterialDatePicker<Long> {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        datePicker.addOnPositiveButtonClickListener {
            onDeadlineChange(it)
            binding.deadlineDate.text = it.getDate()
        }

        return datePicker
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker = setupDatePicker {
            binding.deadlineDate.text = it.getDate()
        }
        val task = getTask()

        var deadlineTime = Calendar.getInstance().time.time

        binding.deadlineDate.paintFlags = binding.deadlineDate.paintFlags + Paint.UNDERLINE_TEXT_FLAG

        binding.taskText.setText(task.text)

        binding.toggleButton.check(
            when (task.urgency) {
                Urgency.LOW -> R.id.buttonUrgencyLow
                Urgency.NORMAL -> R.id.buttonUrgencyNormal
                Urgency.URGENT -> R.id.buttonUrgencyUrgent
            }
        )
        if (task.deadlineDate != null) {
            binding.deadlineSwitch.isChecked = true
            binding.deadlinePickerLine.isVisible = true
            binding.deadlineDate.text = task.deadlineDate.getDate()
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

        binding.backTextView.setOnClickListener {
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.delete.setOnClickListener {
            viewModel.deleteItem(task)
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.save.setOnClickListener {
            viewModel.addOrChangeItem(
                task.copy(
                    text = binding.taskText.text.toString(),
                    urgency = when (binding.toggleButton.checkedButtonId) {
                        R.id.buttonUrgencyLow -> Urgency.LOW
                        R.id.buttonUrgencyNormal -> Urgency.NORMAL
                        R.id.buttonUrgencyUrgent -> Urgency.URGENT
                        else -> Urgency.NORMAL // Any way to delete that stub?
                    },
                    deadlineDate = deadlineTime,
                    lastEditDate = Calendar.getInstance().time.time,
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

    private fun getTask(): Task =
        if (arguments != null) {
            requireArguments().getSerializable("task") as Task
        } else {
            Task(
                id = UUID.randomUUID().toString(),
                creationDate = Calendar.getInstance().time.time,
                isDone = false,
                text = "",
                urgency = Urgency.NORMAL,
                lastEditDate = Calendar.getInstance().time.time,
            )
        }
}