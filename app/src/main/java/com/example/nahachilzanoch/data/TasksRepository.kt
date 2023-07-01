package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.model.Task
import kotlinx.coroutines.flow.Flow
import java.lang.StringBuilder

class TasksRepository(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource,
) {
    fun observeTasks(): Flow<Result<List<Task>>> {
        return localDataSource.observeTasks()
    }

    suspend fun getTasks(): Result<List<Task>> {
        return localDataSource.getTasks()
    }

    fun observeTask(taskId: String): Flow<Result<Task>> {
        return localDataSource.observeTask(taskId)
    }

    suspend fun getTask(taskId: String): Result<Task> {
        return localDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        localDataSource.insertTask(task)
    }

    suspend fun updateCompleted(task: Task, done: Boolean) {
        localDataSource.updateCompleted(task, done)
    }

    suspend fun updateCompleted(taskId: String, done: Boolean) {
        localDataSource.updateCompleted(taskId, done)
    }
    suspend fun deleteTask(taskId: String) {
        localDataSource.deleteTask(taskId)
    }


}