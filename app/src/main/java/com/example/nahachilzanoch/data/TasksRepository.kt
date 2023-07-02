package com.example.nahachilzanoch.data

import android.content.Context
import android.util.Log
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.util.getAndroidID
import kotlinx.coroutines.flow.Flow

class TasksRepository(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource,
) {
    fun observeTasks(): Flow<Result<List<Task>>> {
        return localDataSource.observeTasks()
    }

    suspend fun updateFromRemote() {
        val remoteTasks = remoteDataSource.getTasks()
        if (remoteTasks.isSuccess) {
            localDataSource.getTasks().getOrNull()?.forEach { localDataSource.deleteTask(it.id) }
            remoteTasks.getOrNull()!!.forEach { localDataSource.insertTask(it) }
        }
    }

    suspend fun getTasks(): Result<List<Task>> {
        return Result.success( localDataSource.getTasks().getOrNull()!! )
    }

    suspend fun getTask(taskId: String): Result<Task> {
        return localDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        localDataSource.insertTask(task)
        remoteDataSource.insertTask(task)
    }

    suspend fun updateCompleted(taskId: String, done: Boolean) {
        localDataSource.updateCompleted(taskId, done)
        remoteDataSource.updateCompleted(taskId, done)
    }
    suspend fun deleteTask(taskId: String) {
        localDataSource.deleteTask(taskId)
        remoteDataSource.deleteTask(taskId)
    }


}