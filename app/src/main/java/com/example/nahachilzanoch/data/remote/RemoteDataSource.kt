package com.example.nahachilzanoch.data.remote

import android.content.Context
import android.util.Log
import com.example.nahachilzanoch.data.DataSource
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.util.toList
import com.example.nahachilzanoch.util.toTaskAndRevision
import com.example.nahachilzanoch.util.toTaskRequest
import com.example.nahachilzanoch.util.withRetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception

class RemoteDataSource(
    private val tasksApiService: TasksApiService,
    private val context: Context,
) : DataSource {
    private var lastKnownRevision: Int = -1

    override fun observeTasks(): Flow<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry { tasksApiService.getTasks() }
                if (response.isSuccessful) {
                    lastKnownRevision = response.body()!!.revision
                    Result.success(response.body()!!.toList())
                } else {
                    Result.failure(
                        IllegalStateException(
                            "Error: ${
                                response.errorBody().toString()
                            }"
                        )
                    )
                }
            } catch (ex: Exception) { // TODO
                Result.failure(ex)
            }
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val response = withRetry { tasksApiService.getTask(taskId) }
                if (response.isSuccessful) {
                    val (task, revision) = response.body()!!.toTaskAndRevision()
                    lastKnownRevision = revision
                    Result.success(task)
                } else {
                    Result.failure(
                        IllegalStateException(
                            "Error: ${
                                response.errorBody().toString()
                            }"
                        )
                    )
                }
            } catch (ex: Exception) { // TODO
                Result.failure(ex)
            }
        }
    }

    override suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                val taskRequest = task.toTaskRequest(context)
                val response = withRetry {
                    tasksApiService.putTask(
                        lastKnownRevision,
                        task.id,
                        taskRequest
                    )
                }
                if (response.isSuccessful) {
                    val (receivedTask, revision) = response.body()!!.toTaskAndRevision()
                    lastKnownRevision = revision
                    Result.success(receivedTask)
                } else {
                    Result.failure(
                        IllegalStateException(
                            "Error: ${
                                response.errorBody().toString()
                            }"
                        )
                    )
                }
            } catch (ex: Exception) { // TODO
                Log.d("", ex.stackTraceToString())
                Result.failure(ex)
            }
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                val response = withRetry {
                    tasksApiService.putTask(
                        lastKnownRevision,
                        task.id,
                        task.toTaskRequest(context),
                    )
                }

                if (response.isSuccessful) {
                    Result.success(response.body()!!.toTaskAndRevision().first)
                } else {
                    Result.failure( IllegalStateException(response.errorBody().toString()) )
                }
            } catch(ex: Exception) { // TODO
                Result.failure(ex)
            }
        }
    }

    override suspend fun changeCompleted(taskId: String) {
        withContext(Dispatchers.IO) {
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
                    if (response.isSuccessful) {
                        lastKnownRevision = response.body()!!.revision
                    } else { // TODO

                    }
                } else { // TODO

                }
                Result.success(task)
            } catch (ex: Exception) { // TODO
                Result.failure(ex)
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = withRetry {
                    tasksApiService.deleteTask(
                        lastKnownRevision,
                        taskId
                    )
                }
                if (response.isSuccessful) {
                    val (task, revision) = response.body()!!.toTaskAndRevision()
                    lastKnownRevision = revision
                } else { // TODO

                }
            } catch (ex: Exception) { // TODO

            }
        }
    }
}