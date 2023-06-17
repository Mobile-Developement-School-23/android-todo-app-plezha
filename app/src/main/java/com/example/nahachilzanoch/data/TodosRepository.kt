package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.model.TodoItem
import com.example.nahachilzanoch.model.Urgency
import java.lang.StringBuilder
import java.util.*

val largeString = StringBuilder().apply {
    repeat(499) {
        append((0..127).random().toChar())
    }
}.toString()

object TodosRepository {
    private val todos = mutableListOf<TodoItem>()

    private var suitableIdInt = 0 // TODO: think of something better

    val suitableId
        get() = suitableIdInt++.toString()

    init {
        repeat( (10..40).random() ) {
            add(
                TodoItem(
                    id = suitableId,
                    text = listOf("text1", "text2", largeString).random(),
                    urgency =
                    listOf(Urgency.LOW, Urgency.NORMAL, Urgency.NORMAL, Urgency.URGENT).random(),
                    done = listOf(false, false, true).random(),
                    creationDate = Calendar.getInstance().time,
                    deadline = listOf(Date( (0..Int.MAX_VALUE).random()*1000L ), null).random()
                )
            )
        }
    }

    fun add(item: TodoItem) = todos.add(item)

    fun get(): List<TodoItem> {
        return todos
    }

}