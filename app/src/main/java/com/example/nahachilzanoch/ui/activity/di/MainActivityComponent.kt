package com.example.nahachilzanoch.ui.activity.di

import com.example.nahachilzanoch.di.ActivityScope
import com.example.nahachilzanoch.ui.activity.view.MainActivity
import com.example.nahachilzanoch.ui.edit.di.EditFragmentComponent
import com.example.nahachilzanoch.ui.list.di.MainFragmentComponent
import dagger.Subcomponent

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