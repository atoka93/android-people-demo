package net.attilaszabo.peopledemo.domain.people

import net.attilaszabo.peopledemo.domain.Result
import javax.inject.Inject

class LoadPeopleUseCase @Inject constructor(
    private val peopleRepository: IPeopleRepository
) {

    suspend operator fun invoke(position: Int, amount: Int): Result<List<Person>> =
        peopleRepository.loadPeople(startingAt = position, amount = amount)
}
