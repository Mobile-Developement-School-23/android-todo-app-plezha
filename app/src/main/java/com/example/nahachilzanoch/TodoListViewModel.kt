package com.example.nahachilzanoch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nahachilzanoch.data.TodosRepository
import com.example.nahachilzanoch.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val _todoList = MutableStateFlow(TodosRepository.get())
    val todoList = _todoList.asStateFlow()

    fun addOrChange(newItem: TodoItem) {
        _todoList.update {
            val newList = it.toMutableList()
            val index = newList.indexOfFirst { it.id == newItem.id }

            if (index == -1)
                newList.add( newItem )
            else
                newList[ index ] = newItem
            newList
        }

        println(_todoList)
    }
}