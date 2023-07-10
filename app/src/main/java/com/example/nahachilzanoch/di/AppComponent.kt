package com.example.nahachilzanoch.di

import android.content.Context
import com.example.nahachilzanoch.TodoApplication
import com.example.nahachilzanoch.ui.view.di.MainActivityComponent
import dagger.BindsInstance
import dagger.Component

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
