package net.attilaszabo.peopledemo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.attilaszabo.peopledemo.ui.people.PeopleActivity
import net.attilaszabo.peopledemo.ui.people.PeopleModule

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [PeopleModule::class])
    abstract fun bindPeopleActivity(): PeopleActivity
}
