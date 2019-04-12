package net.attilaszabo.peopledemo.data.people

import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleDatabaseSource {

    suspend fun getPeople(startingAt: Int, amount: Int): List<Person>

    suspend fun savePeople(people: List<Person>)

    suspend fun clearPeople()
}
