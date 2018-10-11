package net.attilaszabo.peopledemo.data.people

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val databaseSource: IPeopleDatabaseSource,
    private val networkSource: IPeopleNetworkSource
) {

    // Public Api

    fun loadPeople(startingAt: Int, amount: Int): Flowable<Result<List<Person>>> =
        loadAllPeopleFromDatabase(startingAt, amount)
            .flatMap { cachedPeople ->
                val cachedPeopleObservable = Flowable.just(cachedPeople.take(amount))
                if (cachedPeople.size >= amount) {
                    cachedPeopleObservable
                } else {
                    combineAndLoadPeopleFromNetwork(cachedPeopleObservable, amount - cachedPeople.size)
                }
            }
            .map { Result.Success(it) as Result<List<Person>> }
            .onErrorReturn { Result.Error(it) }

    // Private Api

    private fun loadAllPeopleFromDatabase(startingAt: Int, amount: Int) =
        databaseSource.getPeople(startingAt, amount)
            .onErrorResumeNext { _: Throwable ->
                Flowable.just(listOf())
            }

    private fun combineAndLoadPeopleFromNetwork(cachedPeople: Flowable<List<Person>>, amount: Int) =
        cachedPeople.zipWith(
            loadPeopleFromNetwork(amount),
            BiFunction<List<Person>, List<Person>, List<Person>> { repackedCachedPeople, networkPeople ->
                repackedCachedPeople.toMutableList().apply {
                    addAll(networkPeople)
                }
            })

    private fun loadPeopleFromNetwork(amount: Int) =
        networkSource.getExtendedPeople(amount)
            .doOnNext { networkPeople ->
                databaseSource.savePeople(networkPeople)
            }
}
