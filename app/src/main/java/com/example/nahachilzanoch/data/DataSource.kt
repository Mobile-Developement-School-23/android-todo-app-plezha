package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun observeTasks(): Flow<Result<List<Task>>>

    suspend fun patchTasks(list: List<Task>): Result<List<Task>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun addTask(task: Task): Result<Task>

    suspend fun updateTask(task: Task): Result<Task>

    suspend fun changeCompleted(taskId: String): Result<Task>

    suspend fun deleteTask(taskId: String): Result<Task>
}