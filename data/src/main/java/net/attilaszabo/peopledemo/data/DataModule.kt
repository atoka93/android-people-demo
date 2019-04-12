package net.attilaszabo.peopledemo.data

import dagger.Binds
import dagger.Module
import net.attilaszabo.peopledemo.data.people.PeopleRepository
import net.attilaszabo.peopledemo.domain.people.IPeopleRepository

@Suppress("unused")
@Module
abstract class DataModule {

    @Binds
    abstract fun bindIPeopleRepository(
        peopleRepository: PeopleRepository
    ): IPeopleRepository
}
