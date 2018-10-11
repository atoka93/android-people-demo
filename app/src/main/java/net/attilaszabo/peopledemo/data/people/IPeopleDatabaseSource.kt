package net.attilaszabo.peopledemo.data.people

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleDatabaseSource {

    fun getPeople(startingAt: Int, amount: Int): Flowable<List<Person>>

    fun savePeople(people: List<Person>)

    fun clearPeople()
}
