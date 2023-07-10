package com.example.nahachilzanoch

import android.app.Application
import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

import com.example.nahachilzanoch.data.remote.DataRefreshWorker
import com.example.nahachilzanoch.di.AppComponent
import com.example.nahachilzanoch.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"

class TodoApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().manufacture(
            applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)
        startDataRefreshWorker()
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

val Context.appComponent: AppComponent
    get() = when (this) {
        is TodoApplication -> appComponent
        else -> this.applicationContext.appComponent
    }