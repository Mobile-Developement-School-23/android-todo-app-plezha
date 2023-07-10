package com.example.nahachilzanoch.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val tasksDao: TasksDao,
) {
    fun observeTasks(): Flow<Result<List<Task>>> {
        return tasksDao.observeTasks().map { Result.success(it) }
    }

    suspend fun getTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            Result.success(tasksDao.getTasks())
        }
    }

    suspend fun getTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            val task = tasksDao.getTaskById(taskId)
            if (task == null) {
                Result.failure(Exception("No task with id $taskId"))
            } else {
                Result.success(task)
            }
        }
    }

    suspend fun addTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            tasksDao.insertTask(task)
            Result.success(task)
        }
    }

    suspend fun updateTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                tasksDao.updateTask(task)
                Result.success(task)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun changeCompleted(taskId: String): Result<Task> {
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

    suspend fun deleteTask(taskId: String): Result<Task> {
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