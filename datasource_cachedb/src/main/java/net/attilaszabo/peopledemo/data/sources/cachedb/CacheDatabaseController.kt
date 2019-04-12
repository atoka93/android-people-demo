package net.attilaszabo.peopledemo.data.sources.cachedb

import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import net.attilaszabo.peopledemo.data.sources.cachedb.models.PersonDatabase
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class CacheDatabaseController @Inject constructor(
    private val database: CacheDatabase
) : IPeopleDatabaseSource {

    // IPeopleDatabaseSource

    override suspend fun getPeople(startingAt: Int, amount: Int): List<Person> =
        database.peopleDao().getPeople(startingAt = startingAt, amount = amount)
            .map { databasePerson ->
                Person(
                    name = databasePerson.name,
                    region = databasePerson.region,
                    age = databasePerson.age,
                    photoUrl = databasePerson.photoUrl
                )
            }


    override suspend fun savePeople(people: List<Person>) {
        database.peopleDao().insertPeople(people = people.map { person ->
            PersonDatabase(
                name = person.name,
                region = person.region,
                age = person.age,
                photoUrl = person.photoUrl
            )
        })
    }

    override suspend fun clearPeople() {
        database.peopleDao().deleteAll()
    }
}
