package com.example.nahachilzanoch.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nahachilzanoch.data.TodosRepository
import com.example.nahachilzanoch.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val _todoList = MutableStateFlow(TodosRepository.get())
    val todoList = _todoList.asStateFlow()
    var completedAmount = MutableStateFlow(0)
    var showPredicate = MutableStateFlow { _: TodoItem -> true }


    init {
        viewModelScope.launch {
            launch {
                TodosRepository.todoList.collect { newValue ->
                    _todoList.update { newValue.filter(showPredicate.value) }
                    completedAmount.update {
                        newValue.count { it.done }
                    }
                }
            }
            launch {
                showPredicate.collect { newPredicate ->
                    _todoList.update {
                        TodosRepository.todoList.value.filter(newPredicate)
                    }
                }
            }
        }
    }

    fun addOrChangeItem(newItem: TodoItem) {
        val newList = TodosRepository.todoList.value.toMutableList()
        val index = newList.indexOfFirst { it.id == newItem.id }

        if (index == -1)
            newList.add(newItem)
        else
            newList[index] = newItem

        TodosRepository.update( newList )
    }

    fun deleteItem(item: TodoItem) {
        TodosRepository.delete(item)
    }
}