package com.example.nahachilzanoch.data.di

import android.content.Context
import com.example.nahachilzanoch.data.DataSource
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.TasksDao
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import com.example.nahachilzanoch.data.remote.TasksApiService
import com.example.nahachilzanoch.ui.view.EditFragment
import com.example.nahachilzanoch.ui.view.MainFragment
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [Module::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun manufacture(
            @BindsInstance context: Context,
            @BindsInstance tasksApiService: TasksApiService,
            @BindsInstance tasksDao: TasksDao
        ): AppComponent
    }
    fun inject(where: MainFragment)

    fun inject(where: EditFragment)

}

@Module
interface DataModule {
    companion object {
        @Provides
        fun localDataSource(tasksDao: TasksDao): DataSource {
            return LocalDataSource(tasksDao = tasksDao)
        }
        @Provides
        fun remoteDataSource(
            context: Context,
            tasksApiService: TasksApiService,
        ): DataSource {
            return RemoteDataSource(
                context = context,
                tasksApiService = tasksApiService
            )
        }
    }
    @Binds
    fun a(impl: LocalDataSource): DataSource
}