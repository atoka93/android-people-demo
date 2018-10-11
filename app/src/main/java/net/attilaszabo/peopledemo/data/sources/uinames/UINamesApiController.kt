package net.attilaszabo.peopledemo.data.sources.uinames

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class UINamesApiController @Inject constructor(
    private val uiNamesApiService: UINamesApiService
) : IPeopleNetworkSource {

    // IPeopleNetworkSource

    override fun getExtendedPeople(amount: Int): Flowable<List<Person>> = if (amount <= 1) {
        Flowable.just(listOf())
    } else {
        uiNamesApiService.loadPeople(amount).map { networkPeople ->
            networkPeople.map { networkPerson ->
                Person(
                    name = networkPerson.name + " " + networkPerson.surname,
                    region = networkPerson.region,
                    age = networkPerson.age,
                    photo = networkPerson.photo
                )
            }
        }
    }
}
