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

    override suspend fun getTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            Result.success(tasksDao.getTasks())
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            val task = tasksDao.getTaskById(taskId)
            if (task == null) {
                return@withContext Result.failure(Exception("No task with id $taskId"))
            } else {
                return@withContext Result.success(task)
            }
        }
    }

    override suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            tasksDao.insertTask(task)
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            tasksDao.updateTask(task)
        }
    }

    override suspend fun changeCompleted(taskId: String) {
        withContext(Dispatchers.IO) {
            tasksDao.changeCompleted(taskId)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            tasksDao.deleteTaskById(taskId)
        }
    }


}