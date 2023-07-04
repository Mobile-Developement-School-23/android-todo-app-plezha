package com.example.nahachilzanoch.data.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nahachilzanoch.data.TasksRepository

class DataRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    private val tasksRepository: TasksRepository,
): CoroutineWorker(
    appContext,
    params,
) {
    override suspend fun doWork(): Result {
        if (tasksRepository.updateFromRemote()) return Result.success()
        return Result.retry()
    }

}