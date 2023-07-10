package com.example.nahachilzanoch.ui.view.di

import com.example.nahachilzanoch.di.FragmentScope
import com.example.nahachilzanoch.ui.view.EditFragment
import dagger.Subcomponent

@Subcomponent
@FragmentScope
interface EditFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun manufacture(): EditFragmentComponent
    }

    fun inject(fragment: EditFragment)
}