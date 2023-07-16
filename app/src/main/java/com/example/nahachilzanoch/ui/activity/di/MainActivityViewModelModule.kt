package com.example.nahachilzanoch.ui.activity.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nahachilzanoch.di.DaggerViewModelFactory
import com.example.nahachilzanoch.di.ViewModelKey
import com.example.nahachilzanoch.ui.activity.TaskListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TaskListViewModel::class)
    abstract fun bindMainActivityViewModel(vm: TaskListViewModel): ViewModel
}