package com.example.nahachilzanoch.ui.activity.di

import android.content.Context
import androidx.room.Room
import com.example.nahachilzanoch.BASE_URL
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.TasksDao
import com.example.nahachilzanoch.data.local.TasksDatabase
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import com.example.nahachilzanoch.data.remote.TasksApiService
import com.example.nahachilzanoch.di.ActivityScope
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
interface ActivityDataModule {
    companion object {

        @ActivityScope
        @Provides
        fun localDataSource(tasksDao: TasksDao): LocalDataSource {
            return LocalDataSource(tasksDao = tasksDao)
        }
        @ActivityScope
        @Provides
        fun remoteDataSource(
            context: Context,
            tasksApiService: TasksApiService,
        ): RemoteDataSource {
            return RemoteDataSource(
                context = context,
                tasksApiService = tasksApiService
            )
        }
        @ActivityScope
        @Provides
        fun tasksApiService(): TasksApiService =
            Retrofit.Builder()
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(BASE_URL)
                .build().create(TasksApiService::class.java)
        @ActivityScope
        @Provides
        fun tasksDao(
            context: Context
        ): TasksDao =
            Room.databaseBuilder(
                context,
                TasksDatabase::class.java, "Tasks.db",
            ).build().taskDao()
    }
}