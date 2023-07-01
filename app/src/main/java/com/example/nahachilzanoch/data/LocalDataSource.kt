package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.model.Task
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

    override fun observeTask(taskId: String): Flow<Result<Task>> {
        return tasksDao.observeTaskById(taskId).map { Result.success(it) }
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

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            tasksDao.insertTask(task)
        }
    }

    override suspend fun updateCompleted(task: Task, done: Boolean) {
        updateCompleted(task.id, done)
    }

    override suspend fun updateCompleted(taskId: String, done: Boolean) {
        withContext(Dispatchers.IO) {
            tasksDao.updateCompleted(taskId, done)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            tasksDao.deleteTaskById(taskId)
        }
    }


}