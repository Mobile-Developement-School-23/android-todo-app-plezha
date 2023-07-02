package com.example.nahachilzanoch

import android.app.Application
import androidx.room.Room
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.TasksDatabase
import com.example.nahachilzanoch.data.TasksRepository
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import com.example.nahachilzanoch.data.remote.TasksApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"

class TodoApplication : Application() {
    lateinit var tasksRepository: TasksRepository
    override fun onCreate() {
        super.onCreate()

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
}