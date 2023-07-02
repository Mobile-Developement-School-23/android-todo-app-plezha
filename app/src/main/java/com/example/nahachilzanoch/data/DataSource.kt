package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun observeTasks(): Flow<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun insertTask(task: Task)

    suspend fun putTask(task: Task)

    suspend fun updateCompleted(taskId: String, done: Boolean)

    suspend fun deleteTask(taskId: String)
}