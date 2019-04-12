package net.attilaszabo.peopledemo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.attilaszabo.peopledemo.ui.people.PeopleFragment
import net.attilaszabo.peopledemo.ui.people.PeopleModule
import net.attilaszabo.peopledemo.ui.person.PersonFragment
import net.attilaszabo.peopledemo.ui.person.PersonModule

@Suppress("unused")
@Module
abstract class MainFragmentBuilderModule {

    @ContributesAndroidInjector(modules = [PeopleModule::class])
    abstract fun contributePeopleFragment(): PeopleFragment

    @ContributesAndroidInjector(modules = [PersonModule::class])
    abstract fun contributePersonFragment(): PersonFragment
}
