package com.example.nahachilzanoch.data

import android.content.Context
import android.provider.Settings
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.remote.models.TaskListRequest
import com.example.nahachilzanoch.data.remote.models.TaskListResponse
import com.example.nahachilzanoch.data.remote.models.TaskNWModel
import com.example.nahachilzanoch.data.remote.models.TaskRequest
import com.example.nahachilzanoch.data.remote.models.TaskResponse
import com.example.nahachilzanoch.util.toUrgency

fun TaskResponse.toTaskAndRevision(): Pair<Task, Int> {
    return task.toTask() to revision
}

fun TaskNWModel.toTask(): Task {
    return Task(
        id = id,
        text = text,
        urgency = importance.toUrgency(),
        isDone = isDone,

        creationDate = createdAt * 1000L,
        deadlineDate = if (deadline != null) deadline * 1000L else null,
        lastEditDate = changedAt * 1000L,
    )
}

fun Task.toTaskRequest(context: Context): TaskRequest {
    return TaskRequest(
        this.toTaskNWModel(context)
    )
}

fun getAndroidID(context: Context): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

fun TaskListResponse.toList(): List<Task> {
    val list = mutableListOf<Task>()
    taskList.forEach {
        list.add(it.toTask())
    }
    return list.toList()
}

fun Task.toTaskNWModel(context: Context) = TaskNWModel(
    id = id,
    text = text,
    importance = urgency.importance,
    isDone = isDone,

    createdAt = (creationDate/1000).toInt(),
    deadline = if (deadlineDate != null) (deadlineDate/1000).toInt() else null,
    changedAt = (lastEditDate/1000).toInt(),

    device = getAndroidID(context)
)

fun List<Task>.toTaskListRequest(context: Context) =
    TaskListRequest(
        this.map {
            it.toTaskNWModel(context)
        }
    )

