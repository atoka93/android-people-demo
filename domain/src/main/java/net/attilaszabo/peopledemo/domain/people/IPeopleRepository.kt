package net.attilaszabo.peopledemo.domain.people

import net.attilaszabo.peopledemo.domain.Result

interface IPeopleRepository {

    suspend fun loadPeople(startingAt: Int, amount: Int): Result<List<Person>>
}
