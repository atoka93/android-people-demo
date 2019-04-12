package net.attilaszabo.peopledemo.data.sources.uinames

import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class UINamesApiController @Inject constructor(
    private val uiNamesApiService: UINamesApiService
) : IPeopleNetworkSource {

    // IPeopleNetworkSource

    override suspend fun getExtendedPeople(amount: Int): List<Person> =
        if (amount <= 1) {
            listOf()
        } else {
            uiNamesApiService.loadPeople(amount).await().map { networkPerson ->
                Person(
                    name = networkPerson.name + " " + networkPerson.surname,
                    region = networkPerson.region,
                    age = networkPerson.age,
                    photoUrl = networkPerson.photoUrl
                )
            }
        }
}
