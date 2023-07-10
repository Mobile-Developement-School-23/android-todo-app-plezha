package com.example.nahachilzanoch.ui.view.di

import com.example.nahachilzanoch.di.FragmentScope
import com.example.nahachilzanoch.ui.view.MainFragment
import dagger.Subcomponent

@Subcomponent
@FragmentScope
interface MainFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun manufacture(): MainFragmentComponent
    }

    fun inject(fragment: MainFragment)
}