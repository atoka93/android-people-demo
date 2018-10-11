package net.attilaszabo.peopledemo.ui.people

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.attilaszabo.peopledemo.di.ViewModelKey
import net.attilaszabo.peopledemo.ui.people.adapter.IPeopleAdapterInteractions

@Suppress("unused")
@Module
abstract class PeopleModule {

    @Binds
    abstract fun bindIPeopleAdapterInteractions(
        peopleActivity: PeopleActivity
    ): IPeopleAdapterInteractions

    @Binds
    @IntoMap
    @ViewModelKey(PeopleViewModel::class)
    internal abstract fun bindPeopleViewModel(viewModel: PeopleViewModel): ViewModel
}
