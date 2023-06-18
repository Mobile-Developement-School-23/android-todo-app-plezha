package com.example.nahachilzanoch.model

import java.io.Serializable
import java.util.*

data class TodoItem(
    val id: String,
    val text: String,
    val urgency: Urgency,
    val done: Boolean,

    val creationDate: Long,
    val deadline: Long? = null,
    val lastEditTime: Long? = null,
) : Serializable
// Both too lazy to generate uid for it and to put items in a bundle one-by-one

enum class Urgency {
    LOW, NORMAL, URGENT
}



