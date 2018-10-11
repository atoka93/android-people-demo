package net.attilaszabo.peopledemo.data.people

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleNetworkSource {

    fun getExtendedPeople(amount: Int): Flowable<List<Person>>
}
