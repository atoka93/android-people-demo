package net.attilaszabo.peopledemo.ui.person

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.attilaszabo.peopledemo.ui.di.viewmodel.ViewModelKey

@Suppress("unused")
@Module
abstract class PersonModule {

    @Binds
    @IntoMap
    @ViewModelKey(PersonViewModel::class)
    internal abstract fun bindPeopleViewModel(viewModel: PersonViewModel): ViewModel
}
