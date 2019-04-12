package net.attilaszabo.peopledemo.data.people

import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleNetworkSource {

    suspend fun getExtendedPeople(amount: Int): List<Person>
}
