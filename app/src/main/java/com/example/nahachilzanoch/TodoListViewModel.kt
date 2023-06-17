package com.example.nahachilzanoch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nahachilzanoch.data.TodosRepository
import com.example.nahachilzanoch.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val _todoList = MutableStateFlow(
        TodosRepository.get()
                to { _: TodoItem -> true }
    )// I hate it. These are the list and filter predicate
    val todoList = _todoList.asStateFlow()
    var completedAmount = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            TodosRepository.todoList.collect { newValue ->
                _todoList.update { (_, predicate) ->
                    newValue.filter(predicate) to predicate
                }
                completedAmount.update {
                    todoList.value.first.count { it.done }
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

    fun setPredicate(newPredicate: (TodoItem) -> Boolean) {
        println(newPredicate)
        _todoList.update { TodosRepository.todoList.value.filter(newPredicate) to newPredicate}
    }
}