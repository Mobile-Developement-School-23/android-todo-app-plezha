package com.example.nahachilzanoch.data

import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.flow.Flow

class TasksRepository(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource,
) {
    fun observeTasks(): Flow<Result<List<Task>>> {
        return localDataSource.observeTasks()
    }

    suspend fun updateFromRemote(): Boolean { // It is as good as backend is
        val remoteTasks = remoteDataSource.getTasks()

        if (remoteTasks.isSuccess) {
            val localTasks = localDataSource.getTasks().getOrNull()!! // TODO
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
        return remoteTasks.isSuccess
    }

    suspend fun getTasks(): Result<List<Task>> {
        return Result.success( localDataSource.getTasks().getOrNull()!! )
    }

    suspend fun getTask(taskId: String): Result<Task> {
        return localDataSource.getTask(taskId)
    }

    suspend fun addTask(task: Task) {
        localDataSource.addTask(task)
        remoteDataSource.addTask(task)
    }

    suspend fun updateTask(task: Task) {
        localDataSource.updateTask(task)
        remoteDataSource.updateTask(task)
    }

    suspend fun changeCompleted(taskId: String) {
        localDataSource.changeCompleted(taskId)
        remoteDataSource.changeCompleted(taskId)
    }
    suspend fun deleteTask(taskId: String) {
        localDataSource.deleteTask(taskId)
        remoteDataSource.deleteTask(taskId)
    }


}