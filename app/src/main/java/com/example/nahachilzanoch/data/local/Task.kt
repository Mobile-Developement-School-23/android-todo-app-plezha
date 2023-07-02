package com.example.nahachilzanoch.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey @ColumnInfo(name = "entryid") val id: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "urgency") val urgency: Urgency,
    @ColumnInfo(name = "done") val isDone: Boolean,

    @ColumnInfo(name = "creationDate") val creationDate: Long,
    @ColumnInfo(name = "deadlineDate") val deadlineDate: Long? = null,
    @ColumnInfo(name = "lastEditDate") val lastEditDate: Long,
) : Serializable

enum class Urgency(val importance: String) {
    LOW("low"),
    NORMAL("basic"),
    URGENT("important")
}



