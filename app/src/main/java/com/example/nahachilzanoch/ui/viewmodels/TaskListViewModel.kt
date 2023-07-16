package com.example.nahachilzanoch.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nahachilzanoch.data.TasksRepository
import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class TaskListViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
) : ViewModel() {
    private val _taskList = MutableStateFlow<List<Task>>(listOf())
    val taskList = _taskList.asStateFlow()
    var completedAmount = MutableStateFlow(0)
    var showPredicate = MutableStateFlow { _: Task -> true }

    var errorStringFlow: MutableStateFlow<String?> = MutableStateFlow(null)


    init {
        Log.d("", "VM INIT")
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                updateFromRemote()
            }
            launch {
                tasksRepository.observeTasks().collect { newValue ->
                    val newValueList = newValue.getOrNull()!!
                    _taskList.update {
                        newValueList.filter(showPredicate.value)
                    }
                    completedAmount.update { newValueList.count { it.isDone } }
                }
            }
            launch {
                showPredicate.collect { newPredicate ->
                    val tasks = tasksRepository.getTasks()
                    _taskList.update {
                        tasks.getOrNull()!!.filter(newPredicate)
                    }
                }
            }
        }
    }

    fun addOrChangeItem(newTask: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = if (tasksRepository.getTask(newTask.id).isSuccess) {
                tasksRepository.updateTask(newTask)
            } else {
                tasksRepository.addTask(newTask)
            }
            if (result.isFailure) errorStringFlow.update { result.getErrorStringForUser() }
        }
    }

    fun updateFromRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!tasksRepository.updateFromRemote()) errorStringFlow.update { "Network Error" }
        }
    }

    fun deleteItem(item: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = tasksRepository.deleteTask(item.id)
            if (result.isFailure) errorStringFlow.update { result.getErrorStringForUser() }
        }
    }

    fun updateCompleted(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = tasksRepository.changeCompleted(taskId)
            if (result.isFailure) errorStringFlow.update { result.getErrorStringForUser() }
        }
    }

    private fun Result<Task>.getErrorStringForUser(): String {
        Log.d("", exceptionOrNull()?.stackTraceToString()!!)
        return if (
            exceptionOrNull() is HttpException ||
            exceptionOrNull() is UnknownHostException) "Network Error"
        else "Unexpected Error"
    }
}