package com.example.nahachilzanoch.data.local

import com.example.nahachilzanoch.data.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val tasksDao: TasksDao,
) : DataSource {
    override fun observeTasks(): Flow<Result<List<Task>>> {
        return tasksDao.observeTasks().map { Result.success(it) }
    }

    override suspend fun patchTasks(list: List<Task>): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            Result.success(tasksDao.getTasks())
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            val task = tasksDao.getTaskById(taskId)
            if (task == null) {
                Result.failure(Exception("No task with id $taskId"))
            } else {
                Result.success(task)
            }
        }
    }

    override suspend fun addTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            tasksDao.insertTask(task)
            Result.success(task)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                tasksDao.updateTask(task)
                Result.success(task)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun changeCompleted(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                tasksDao.changeCompleted(taskId)
                Result.success(
                    Task(taskId, "", Urgency.URGENT, false, 0, 0, 0)
                ) // Stub
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                tasksDao.deleteTaskById(taskId)
                Result.success(
                    Task(taskId, "", Urgency.URGENT, false, 0, 0, 0)
                ) // Stub
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}