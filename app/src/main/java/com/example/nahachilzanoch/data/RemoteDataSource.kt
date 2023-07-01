package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.model.Task
import kotlinx.coroutines.flow.Flow

class RemoteDataSource: DataSource {
    override fun observeTasks(): Flow<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): Flow<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompleted(task: Task, done: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompleted(taskId: String, done: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

}