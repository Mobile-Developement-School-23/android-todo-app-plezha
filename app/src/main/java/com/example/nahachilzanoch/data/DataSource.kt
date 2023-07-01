package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.model.Task
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun observeTasks(): Flow<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    fun observeTask(taskId: String): Flow<Result<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun insertTask(task: Task)

    suspend fun updateCompleted(task: Task, done: Boolean)

    suspend fun updateCompleted(taskId: String, done: Boolean)

    suspend fun deleteTask(taskId: String)
}