package net.attilaszabo.peopledemo.di

import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.data.people.PeopleRepository
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import javax.inject.Singleton

@Suppress("unused")
@Module
class DomainModule {

    @Singleton
    @Provides
    fun provideLoadPeopleUseCase(peopleRepository: PeopleRepository): LoadPeopleUseCase =
        LoadPeopleUseCase(peopleRepository)
}
