package com.example.nahachilzanoch

import android.app.Application
import android.content.Context
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.TasksDatabase
import com.example.nahachilzanoch.data.TasksRepository
import com.example.nahachilzanoch.data.remote.DataRefreshWorker
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import com.example.nahachilzanoch.data.remote.TasksApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"

class TodoApplication : Application() {
    lateinit var tasksRepository: TasksRepository
    override fun onCreate() {
        super.onCreate()

        initTasksRepository()
        startDataRefreshWorker()
    }

    private fun initTasksRepository() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .baseUrl(BASE_URL)
            .build()

        val tasksApiService = retrofit.create(TasksApiService::class.java)

        val remoteDataSource = RemoteDataSource(
            tasksApiService,
            applicationContext,
        )

        val localDataSource = LocalDataSource(
            Room.databaseBuilder(
                applicationContext,
                TasksDatabase::class.java, "Tasks.db",
            ).build().taskDao()
        )

        tasksRepository = TasksRepository(
            localDataSource,
            remoteDataSource
        )
    }

    private fun startDataRefreshWorker() {
        val dataRefreshWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<DataRefreshWorker>(8, TimeUnit.HOURS)
                .addTag("dataRefreshWorkRequest")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(
                            NetworkType.CONNECTED
                        )
                        .build()
                )
                .setBackoffCriteria( // In case of server error
                    BackoffPolicy.LINEAR,
                    2,
                    TimeUnit.HOURS
                )
                .build()
        WorkManager
            .getInstance(applicationContext)
            .enqueue(dataRefreshWorkRequest)
    }
}