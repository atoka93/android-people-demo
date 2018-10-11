package net.attilaszabo.peopledemo.domain.people

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import net.attilaszabo.peopledemo.data.people.PeopleRepository
import net.attilaszabo.peopledemo.domain.Result
import javax.inject.Inject

class LoadPeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val scheduler: Scheduler = Schedulers.io()
) {

    fun execute(position: Int, amount: Int): Flowable<Result<List<Person>>> =
        peopleRepository.loadPeople(startingAt = position, amount = amount)
            .subscribeOn(scheduler)
}
