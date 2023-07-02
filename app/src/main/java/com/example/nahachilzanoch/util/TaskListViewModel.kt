package com.example.nahachilzanoch.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nahachilzanoch.TodoApplication
import com.example.nahachilzanoch.data.TasksRepository
import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _taskList = MutableStateFlow<List<Task>>( listOf() )
    val taskList = _taskList.asStateFlow()
    var completedAmount = MutableStateFlow(0)
    var showPredicate = MutableStateFlow { _: Task -> true }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.updateFromRemote()
            launch {
                tasksRepository.observeTasks().collect { newValue ->
                    // No reason to catch anything that never happens TODO
                    val newValueList = newValue.getOrNull()!!
                    _taskList.update {
                        newValueList.filter(showPredicate.value)
                    }
                    completedAmount.update { newValueList.count { it.isDone } }
                }
            }
            launch {
                showPredicate.collect { newPredicate ->
                    // No reason to catch anything that never happens TODO
                    val tasks = tasksRepository.getTasks()
                    _taskList.update {
                        if (tasks.isSuccess) {
                            tasks.getOrNull()!!
                        } else {
                            listOf()
                        }
                    }
                }
            }
        }
    }

    fun addOrChangeItem(newTask: Task) {
        viewModelScope.launch(Dispatchers.IO) { tasksRepository.saveTask(newTask) }
    }

    fun deleteItem(item: Task) {
        viewModelScope.launch(Dispatchers.IO) { tasksRepository.deleteTask(item.id) }
    }

    fun updateCompleted(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) { tasksRepository.updateCompleted(taskId) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoApplication)
                val tasksRepository = application.tasksRepository
                TaskListViewModel(tasksRepository)
            }
        }
    }
}