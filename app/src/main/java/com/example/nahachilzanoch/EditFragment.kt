package com.example.nahachilzanoch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nahachilzanoch.data.TodosRepository
import com.example.nahachilzanoch.databinding.EditFragmentBinding
import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency
import java.util.*

private fun Float.getDateTime(): String {
    return Date(this.toLong() * 1000).toString() // TODO: make it localized
}

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

        //viewModel = ViewModelProvider(this).get(Fragment2ViewModel::class.java)
        val deadlineTextView = binding.deadlineTextView
        val deadlineSlider = binding.deadlineSlider
        val textView = binding.text
        val toggleButton = binding.toggleButton
        val deadlineSwitch = binding.deadlineSwitch

        val todoItem =
            if (arguments != null) {
                requireArguments().getSerializable("todoItem") as TodoItem
            } else {
                TodoItem(
                    id = TodosRepository.suitableId,
                    creationDate = Calendar.getInstance().time,
                    done = false,
                    text = "",
                    urgency = Urgency.NORMAL
                )
            }

        textView.setText(todoItem.text)

        toggleButton.check(
            when (todoItem.urgency) {
                Urgency.LOW -> R.id.buttonUrgencyLow
                Urgency.NORMAL -> R.id.buttonUrgencyNormal
                Urgency.URGENT -> R.id.buttonUrgencyUrgent
            }
        )
        if (todoItem.deadline != null) {
            deadlineSwitch.isChecked = true
            binding.deadlinePickerLine.visibility = View.VISIBLE
        }

        deadlineSlider.valueTo = Integer.MAX_VALUE.toFloat()
        /*
        // Make it 00:00:00 of today
        val curTimeSeconds = Calendar.getInstance().time.time / 1000f
        deadlineSlider.value = curTimeSeconds - curTimeSeconds % (24 * 60 * 60)
        */
        deadlineSlider.valueFrom = deadlineSlider.value

        if (todoItem.deadline != null) deadlineSlider.value = todoItem.deadline.time / 1000f

        deadlineTextView.text = deadlineSlider.value.getDateTime()

        deadlineSlider.addOnChangeListener { _, value, _ ->
            deadlineTextView.text = value.getDateTime()
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.save.setOnClickListener {
            viewModel.addOrChangeItem(
                todoItem.copy(
                    text = textView.text.toString(),
                    urgency = when (toggleButton.checkedButtonId) {
                        R.id.buttonUrgencyLow -> Urgency.LOW
                        R.id.buttonUrgencyNormal -> Urgency.NORMAL
                        R.id.buttonUrgencyUrgent -> Urgency.URGENT
                        else -> Urgency.NORMAL // Any way to delete that stub?
                    },
                    deadline =
                    if (deadlineSwitch.isChecked)
                        Date(deadlineSlider.value.toLong() * 1000L)
                    else null,
                )
            )
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        binding.deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.deadlinePickerLine.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}