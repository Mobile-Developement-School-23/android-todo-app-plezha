package com.example.nahachilzanoch.ui.edit.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.local.Urgency
import com.example.nahachilzanoch.databinding.EditFragmentBinding
import com.example.nahachilzanoch.ui.activity.TaskListViewModel
import com.example.nahachilzanoch.ui.activity.view.MainActivity
import com.google.accompanist.themeadapter.material3.createMdc3Theme
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


class EditFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TaskListViewModel>(
        { activity as MainActivity }
    ) { viewModelFactory }

    private var _binding: EditFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).mainActivityComponent.todoEditorComponent().manufacture()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val task = getTask()

        var (colorScheme, typography, shapes) = createMdc3Theme(
            context = requireContext(),
            layoutDirection = LayoutDirection.Ltr,
            setTextColors = true
        )


        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = colorScheme!!,
                    typography = typography!!,
                    shapes = shapes!!
                ) {
                    EditScreen(
                        task = task,
                        onDeleteTask = { viewModel.deleteItem(it) },
                        onSaveOrAddTask = { viewModel.addOrChangeItem(it) },
                        onExit = {
                            findNavController().navigate(R.id.action_fragment2_to_fragment1)
                        }
                    )
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}