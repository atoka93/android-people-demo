package net.attilaszabo.peopledemo.data.people

import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.IPeopleRepository
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val databaseSource: IPeopleDatabaseSource,
    private val networkSource: IPeopleNetworkSource
) : IPeopleRepository {

    // Public Api

    override suspend fun loadPeople(startingAt: Int, amount: Int): Result<List<Person>> =
        try {
            val cachedPeople = loadAllPeopleFromDatabase(startingAt, amount)
            Result.Success(
                if (cachedPeople.size >= amount) {
                    cachedPeople.take(amount)
                } else {
                    cachedPeople.toMutableList().apply {
                        addAll(loadPeopleFromNetwork(amount))
                    }
                }
            )
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    // Private Api

    private suspend fun loadAllPeopleFromDatabase(startingAt: Int, amount: Int): List<Person> =
        try {
            databaseSource.getPeople(startingAt, amount)
        } catch (exception: Exception) {
            listOf()
        }

    private suspend fun loadPeopleFromNetwork(amount: Int): List<Person> =
        networkSource.getExtendedPeople(amount).apply {
            if (isNotEmpty()) {
                databaseSource.savePeople(this)
            }
        }
}
