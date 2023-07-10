package com.example.nahachilzanoch.data.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.nahachilzanoch.BASE_URL
import com.example.nahachilzanoch.TodoApplication
import com.example.nahachilzanoch.data.local.LocalDataSource
import com.example.nahachilzanoch.data.local.TasksDao
import com.example.nahachilzanoch.data.local.TasksDatabase
import com.example.nahachilzanoch.data.remote.RemoteDataSource
import com.example.nahachilzanoch.data.remote.TasksApiService
import com.example.nahachilzanoch.ui.view.EditFragment
import com.example.nahachilzanoch.ui.view.MainActivity
import com.example.nahachilzanoch.ui.view.MainFragment
import com.example.nahachilzanoch.ui.viewmodels.TaskListViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AppScope
@Component
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun manufacture(
            @BindsInstance context: Context,
        ): AppComponent
    }
    fun inject(app: TodoApplication)

    fun mainActivityComponent(): MainActivityComponent.Factory
}

@ActivityScope
@Subcomponent(
    modules = [
        ActivityDataModule::class,
        MainActivityViewModelModule::class
    ]
)
interface MainActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun manufacture(): MainActivityComponent

    }

    fun inject(activity: MainActivity)

    fun todoListComponent(): MainFragmentComponent.Factory
    fun todoEditorComponent(): EditFragmentComponent.Factory
}

// Has no use
@Subcomponent
@FragmentScope
interface MainFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun manufacture(): MainFragmentComponent
    }

    fun inject(fragment: MainFragment)
}

// Has no use
@Subcomponent
@FragmentScope
interface EditFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun manufacture(): EditFragmentComponent
    }

    fun inject(fragment: EditFragment)
}

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

@Module
abstract class MainActivityViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory


    @Binds
    @IntoMap
    @ViewModelKey(TaskListViewModel::class)
    abstract fun bindMainActivityViewModel(vm: TaskListViewModel): ViewModel

}





