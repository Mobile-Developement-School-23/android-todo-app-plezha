package com.example.nahachilzanoch.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.local.Urgency
import com.example.nahachilzanoch.databinding.EditFragmentBinding
import com.example.nahachilzanoch.ui.viewmodels.TaskListViewModel
import com.example.nahachilzanoch.util.getDateString
import com.google.accompanist.themeadapter.material3.createMdc3Theme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalTime
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
                        onDelete = { viewModel.deleteItem(it) },
                        onSaveOrAdd = { viewModel.addOrChangeItem(it) },
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


@Preview(
    device = "spec:width=411dp,height=891dp",
    showBackground = true
)
@Composable
fun EditScreenPreview() {
    EditScreen(
        task = Task(
            "",
            "Task's text",
            Urgency.NORMAL,
            false,
            1,187138718,1
        ),
        onSaveOrAdd = { },
        onDelete = { },
        onExit = { },
    )
}


@Composable
fun EditScreen(
    task: Task,
    onSaveOrAdd: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onExit: () -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(task.text) }
    var selectedUrgency by remember { mutableStateOf(task.urgency) }
    var showUrgencySelectionBottomSheet by remember { mutableStateOf(false) }

    var showDeadlineRow by remember { mutableStateOf(task.deadlineDate != null) }

    val dateTimeToDisplay = task.deadlineDate ?: Calendar.getInstance().time.time

    var selectedDateMillis by remember {
        mutableLongStateOf(
            dateTimeToDisplay - dateTimeToDisplay % (24 * 60 * 60 * 1000L)
        )
    }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var selectedTimeMillis by remember {
        mutableLongStateOf( dateTimeToDisplay % (24 * 60 * 60 * 1000L) )
    }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Task",
                style = MaterialTheme.typography.headlineLarge
            )
            TextButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onExit
            ) {
                Text("Back")
            }
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = { textFieldValue = it }
        )
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showUrgencySelectionBottomSheet = true
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Urgency")
            Text(selectedUrgency.name.lowercase().replaceFirstChar { it.uppercase() })
        }
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Do before:")
            Switch(
                checked = showDeadlineRow,
                onCheckedChange = { showDeadlineRow = !showDeadlineRow }
            )
        }
        if (showDeadlineRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.clickable {
                        showDatePickerDialog = true
                    },
                    text = selectedDateMillis.getDateString(),
                    style = TextStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                )
                Spacer(
                    modifier = Modifier.width(5.dp)
                )
                Text(
                    modifier = Modifier.clickable {
                        showTimePickerDialog = true
                    },
                    text = "${selectedTimeMillis/ (60*60*1000L) }" +
                            ":${selectedTimeMillis / (60*1000L) % 60}",
                    style = TextStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                )

            }
        }
        Divider()
        Spacer( Modifier.weight(Float.MAX_VALUE) ) // To move last row to bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    onDelete( task )
                    onExit()
                }
            ) {
                Text("Delete")
            }
            TextButton(
                onClick = {
                    onSaveOrAdd(
                        task.copy(
                            text = textFieldValue,
                            urgency = selectedUrgency,
                            deadlineDate =
                                if (showDeadlineRow)
                                    selectedDateMillis + selectedTimeMillis
                                else
                                    null,
                            lastEditDate = Calendar.getInstance().time.time,
                        )
                    )
                    onExit()
                }
            ) {
                Text("Save")
            }
        }
    }
    if (showUrgencySelectionBottomSheet) {
        UrgencySelectionBottomSheet(
            onDismiss = { showUrgencySelectionBottomSheet = false },
            onUrgencySelected =  { selectedUrgency = it }
        )
    }
    if (showDatePickerDialog) {
        DateSelectionDialog(
            initialValue = selectedDateMillis,
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { selectedDateMillis = it },
        )
    }
    if (showTimePickerDialog) {
        TimeSelectionDialog(
            defaultTimeMillis = selectedTimeMillis,
            onDismiss = { showTimePickerDialog = false },
            onTimeSelected = { selectedTimeMillis = it }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimeSelectionDialog(
    defaultTimeMillis: Long,
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit,
) {
    ClockDialog(
        state = rememberUseCaseState(
            visible = true,
            onCloseRequest = { onDismiss() }
        ),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            onTimeSelected( (hours * 60 + minutes) * 60 * 1000L )
        },
        config = ClockConfig(
            defaultTime = LocalTime.ofSecondOfDay(defaultTimeMillis / 1000L),
            is24HourFormat = true
        ),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UrgencySelectionBottomSheet(
    onDismiss: () -> Unit,
    onUrgencySelected: (Urgency) -> Unit,
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Urgency.values().forEach {
            Text(
                modifier = Modifier.clickable {
                    onUrgencySelected( it )
                    onDismiss()
                },
                text = it.name.lowercase().replaceFirstChar { it.uppercase() }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateSelectionDialog(
    initialValue: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialValue
    )
    @SuppressLint("UnrememberedMutableState")
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
    // TODO: understand the line above
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onDateSelected(datePickerState.selectedDateMillis!!)
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}