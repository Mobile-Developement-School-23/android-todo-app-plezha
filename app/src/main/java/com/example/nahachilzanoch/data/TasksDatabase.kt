package com.example.nahachilzanoch.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nahachilzanoch.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun taskDao(): TasksDao
}