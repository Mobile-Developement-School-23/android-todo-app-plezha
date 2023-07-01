package com.example.nahachilzanoch

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nahachilzanoch.data.LocalDataSource
import com.example.nahachilzanoch.data.TasksDatabase
import com.example.nahachilzanoch.data.TasksRepository

class TodoApplication : Application() {
    lateinit var tasksRepository: TasksRepository
    override fun onCreate() {
        super.onCreate()
        val localDataSource = LocalDataSource(
            Room.databaseBuilder(
                this.applicationContext,
                TasksDatabase::class.java, "Tasks.db"
            ).build().taskDao()
        )
        this.tasksRepository = TasksRepository(
            localDataSource,
            localDataSource
        )
    }
}