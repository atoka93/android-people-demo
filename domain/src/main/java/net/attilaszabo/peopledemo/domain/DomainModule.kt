package net.attilaszabo.peopledemo.domain

import dagger.Module
import dagger.Provides
import net.attilaszabo.peopledemo.domain.people.IPeopleRepository
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import javax.inject.Singleton

@Suppress("unused")
@Module
class DomainModule {

    @Singleton
    @Provides
    fun provideLoadPeopleUseCase(peopleRepository: IPeopleRepository): LoadPeopleUseCase =
        LoadPeopleUseCase(peopleRepository)
}
