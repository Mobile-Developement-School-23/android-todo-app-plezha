package com.example.nahachilzanoch.data.remote

import android.content.Context
import android.util.Log
import com.example.nahachilzanoch.data.DataSource
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.remote.models.TaskListResponse
import com.example.nahachilzanoch.util.toList
import com.example.nahachilzanoch.util.toTaskAndRevision
import com.example.nahachilzanoch.util.toTaskRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
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
                val response = tasksApiService.getTasks()
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
                val response = tasksApiService.getTask(taskId)
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

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                val taskRequest = task.toTaskRequest(context)
                val response = tasksApiService.postTask(lastKnownRevision, taskRequest)
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

    override suspend fun putTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                val response = tasksApiService.putTask(
                    lastKnownRevision,
                    task.id,
                    task.toTaskRequest(context),
                )
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

    override suspend fun updateCompleted(taskId: String, done: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val task = getTask(taskId)
                if (task.isSuccess) {
                    val response = tasksApiService.putTask(
                        lastKnownRevision,
                        taskId,
                        task.getOrNull()!!.toTaskRequest(context)
                    )
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
                val response = tasksApiService.deleteTask(taskId)
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