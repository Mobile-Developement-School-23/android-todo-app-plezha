package com.example.nahachilzanoch.ui.view.di

import com.example.nahachilzanoch.di.ActivityScope
import com.example.nahachilzanoch.ui.view.MainActivity
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