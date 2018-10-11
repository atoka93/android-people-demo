package net.attilaszabo.peopledemo.data.sources.local.database

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import net.attilaszabo.peopledemo.data.sources.local.database.models.PersonDatabase
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class CacheDatabaseController @Inject constructor(
    private val database: CacheDatabase
) : IPeopleDatabaseSource {

    // IPeopleDatabaseSource

    override fun getPeople(startingAt: Int, amount: Int): Flowable<List<Person>> =
        Flowable.fromCallable {
            database.peopleDao().getPeople(startingAt = startingAt, amount = amount)
                .map { databasePerson ->
                    Person(
                        name = databasePerson.name,
                        region = databasePerson.region,
                        age = databasePerson.age,
                        photo = databasePerson.photo
                    )
                }
        }

    override fun savePeople(people: List<Person>) {
        database.peopleDao().insertPeople(people = people.map { person ->
            PersonDatabase(
                name = person.name,
                region = person.region,
                age = person.age,
                photo = person.photo
            )
        })
    }

    override fun clearPeople() {
        database.peopleDao().deleteAll()
    }
}
