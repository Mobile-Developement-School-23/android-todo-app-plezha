package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.data.di.ActivityScope
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScope
class TasksRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) {
    fun observeTasks(): Flow<Result<List<Task>>> {
        return localDataSource.observeTasks()
    }

    suspend fun updateFromRemote(): Boolean { // It is as good as backend is
        val remoteTasks = remoteDataSource.getTasks()

        if (remoteTasks.isSuccess) {
            val localTasks = localDataSource.getTasks().getOrNull()!!
            val localTasksMap = mutableMapOf<String, Int>() // Id to index
            localTasks
                .forEachIndexed { index, item -> localTasksMap[item.id] = index }
            remoteTasks.getOrNull()!!.forEach { task ->
                if (localTasksMap.containsKey(task.id)) {
                    if (task.lastEditDate > localTasks[ localTasksMap[task.id]!! ].lastEditDate) {
                        localDataSource.updateTask(task)
                    } else {
                        localDataSource.addTask(task)
                    }
                }
                localDataSource.addTask(task)
            }
        }
        remoteDataSource.patchTasks( localDataSource.getTasks().getOrNull()!! )
        return remoteTasks.isSuccess
    }

    suspend fun getTasks(): Result<List<Task>> {
        return Result.success( localDataSource.getTasks().getOrNull()!! )
    }

    suspend fun getTask(taskId: String): Result<Task> {
        return localDataSource.getTask(taskId)
    }

    suspend fun addTask(task: Task): Result<Task> {
        localDataSource.addTask(task)
        return remoteDataSource.addTask(task)
    }

    suspend fun updateTask(task: Task): Result<Task> {
        localDataSource.updateTask(task)
        return remoteDataSource.updateTask(task)
    }

    suspend fun changeCompleted(taskId: String): Result<Task>  {
        localDataSource.changeCompleted(taskId)
        return remoteDataSource.changeCompleted(taskId)
    }
    suspend fun deleteTask(taskId: String): Result<Task>  {
        localDataSource.deleteTask(taskId)
        return remoteDataSource.deleteTask(taskId)
    }


}