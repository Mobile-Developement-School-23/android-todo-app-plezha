package com.example.nahachilzanoch.data.remote

import android.content.Context
import android.util.Log
import com.example.nahachilzanoch.data.DataSource
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.remote.models.TaskResponse
import com.example.nahachilzanoch.util.toList
import com.example.nahachilzanoch.util.toTaskAndRevision
import com.example.nahachilzanoch.util.toTaskListRequest
import com.example.nahachilzanoch.util.toTaskRequest
import com.example.nahachilzanoch.util.withRetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class RemoteDataSource(
    private val tasksApiService: TasksApiService,
    private val context: Context,
) : DataSource {
    private var lastKnownRevision: Int = -1

    override fun observeTasks(): Flow<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun patchTasks(list: List<Task>): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val listToPatch = list.toTaskListRequest(context)
                val response = withRetry {
                    tasksApiService.patchTasks(
                        lastKnownRevision,
                        listToPatch,
                    )
                }
                if (response.isSuccessful) {
                    lastKnownRevision = response.body()!!.revision
                    Result.success(response.body()!!.toList())
                } else {
                    Result.failure( HttpException(response) )
                }
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry { tasksApiService.getTasks() }
                if (response.isSuccessful) {
                    lastKnownRevision = response.body()!!.revision
                    Result.success(response.body()!!.toList())
                } else {
                    Result.failure( HttpException(response) )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry { tasksApiService.getTask(taskId) }
                response.getResultAndUpdateRevision()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val taskRequest = task.toTaskRequest(context)
                val response = withRetry {
                    tasksApiService.postTask(
                        lastKnownRevision,
                        taskRequest
                    )
                }
                response.getResultAndUpdateRevision()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry {
                    tasksApiService.putTask(
                        lastKnownRevision,
                        task.id,
                        task.toTaskRequest(context),
                    )
                }
                response.getResultAndUpdateRevision()
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun changeCompleted(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = withRetry { getTask(taskId) }
                if (task.isSuccess) {
                    val task = task.getOrNull()!!
                    val response = withRetry {
                        tasksApiService.putTask(
                            lastKnownRevision,
                            taskId,
                            task
                                .copy(isDone = !task.isDone)
                                .toTaskRequest(context)
                        )
                    }
                    response.getResultAndUpdateRevision()
                } else {
                    Result.failure( task.exceptionOrNull()!! )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry {
                    tasksApiService.deleteTask(
                        lastKnownRevision,
                        taskId
                    )
                }
                response.getResultAndUpdateRevision()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun Response<TaskResponse>.getResultAndUpdateRevision(): Result<Task> {
        return if (isSuccessful) {
            val (task, revision) = body()!!.toTaskAndRevision()
            lastKnownRevision = revision
            Result.success(task)
        } else {
            Result.failure( HttpException(this) )
        }
    }
}