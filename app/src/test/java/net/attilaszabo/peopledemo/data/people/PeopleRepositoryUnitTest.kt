package net.attilaszabo.peopledemo.data.people

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.TestUtils.generatePerson
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiController
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.Random

@RunWith(MockitoJUnitRunner::class)
class PeopleRepositoryUnitTest {

    private val people = listOf(
        generatePerson(),
        generatePerson(),
        generatePerson(),
        generatePerson(),
        generatePerson()
    )

    private lateinit var networkSource: IPeopleNetworkSource
    private lateinit var databaseSource: IPeopleDatabaseSource
    private lateinit var repository: PeopleRepository

    @Before
    fun initVariables() {
        networkSource = mock()
        databaseSource = mock()
        repository = PeopleRepository(databaseSource, networkSource)
    }

    @Test
    fun peopleRepository_OnExactlyTheNecessaryCachedPeople_NoRequestIsMadeToTheNetwork() {
        val startingPosition = Random().nextInt()
        given(databaseSource.getPeople(startingPosition, 5)).willReturn(Flowable.just(people))

        val result = repository.loadPeople(startingPosition, 5).blockingFirst()

        assertEquals((result as Result.Success).result, people)
    }

    @Test
    fun peopleRepository_OnNoCachedPeople_RequestsNecessaryPeopleFromNetwork() {
        val startingPosition = Random().nextInt()
        given(databaseSource.getPeople(startingPosition, 5)).willReturn(Flowable.error(Throwable("")))
        given(networkSource.getExtendedPeople(5)).willReturn(Flowable.just(people))

        val result = repository.loadPeople(startingPosition, 5).blockingFirst()

        assertEquals((result as Result.Success).result, people)
    }

    @Test
    fun peopleRepository_OnPartialAmountOfCachedPeople_RequestsRemainingAmountOfPeopleFromNetwork() {
        val startingPosition = Random().nextInt()
        given(databaseSource.getPeople(startingPosition, 5)).willReturn(Flowable.just(people.take(2)))
        given(networkSource.getExtendedPeople(3)).willReturn(Flowable.just(people.takeLast(3)))

        val result = repository.loadPeople(startingPosition, 5).blockingFirst()

        assertEquals((result as Result.Success).result, people)
    }

    @Test
    fun peopleRepository_WhenRequestingLessThanTwoPeopleFromNetwork_EmptyListIsReturned() {
        val amount = Random().nextInt(2)
        val peopleNetworkSource: IPeopleNetworkSource = UINamesApiController(mock())

        val result = peopleNetworkSource.getExtendedPeople(amount).blockingFirst()

        assertEquals(result, listOf<Person>())
    }

    @Test
    fun peopleRepository_OnNoCachedPeople_AndNetworkError_ReturnError() {
        val startingPosition = Random().nextInt()
        val throwable = Throwable("A")

        given(databaseSource.getPeople(startingPosition, 5)).willReturn(Flowable.error(Throwable("")))
        given(networkSource.getExtendedPeople(5)).willReturn(Flowable.error(throwable))

        val result = repository.loadPeople(startingPosition, 5).blockingFirst()

        assertEquals((result as Result.Error).throwable, throwable)
    }

    @Test
    fun peopleRepository_WhenNetworkPeopleAreRequested_PeopleAreSavedToTheDatabase() {
        val startingPosition = Random().nextInt()
        given(databaseSource.getPeople(startingPosition, 5)).willReturn(Flowable.error(Throwable("")))
        given(networkSource.getExtendedPeople(5)).willReturn(Flowable.just(people))

        repository.loadPeople(startingPosition, 5).blockingFirst()

        Mockito.verify(databaseSource).savePeople(people)
    }
}
