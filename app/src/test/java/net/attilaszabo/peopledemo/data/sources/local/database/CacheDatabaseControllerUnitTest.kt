package net.attilaszabo.peopledemo.data.sources.local.database

import net.attilaszabo.peopledemo.TestUtils.generateDatabasePerson
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
class CacheDatabaseControllerUnitTest {

    private val databasePeople = listOf(
        generateDatabasePerson(),
        generateDatabasePerson()
    )

    private val people = listOf(
        Person(
            name = databasePeople[0].name,
            region = databasePeople[0].region,
            age = databasePeople[0].age,
            photo = databasePeople[0].photo
        ),
        Person(
            name = databasePeople[1].name,
            region = databasePeople[1].region,
            age = databasePeople[1].age,
            photo = databasePeople[1].photo
        )
    )

    private lateinit var databaseController: CacheDatabaseController
    private lateinit var peopleDao: PeopleDao
    private lateinit var database: CacheDatabase

    @Before
    fun initVariables() {
        peopleDao = mock()
        database = mock()
        given(database.peopleDao()).willReturn(peopleDao)
        databaseController = CacheDatabaseController(database)
    }

    @Test
    fun cacheDatabaseController_GetPeopleReturnsAllCachedPeople() {
        val startingPosition = Random().nextInt()
        given(peopleDao.getPeople(startingPosition, 2)).willReturn(databasePeople)
        val result = databaseController.getPeople(startingPosition, 2).blockingFirst()

        assertEquals(result, people)
    }

    @Test
    fun cacheDatabaseController_SavePeopleInsertsDatabasePeople() {
        databaseController.savePeople(people)

        Mockito.verify(peopleDao).insertPeople(databasePeople)
    }

    @Test
    fun cacheDatabaseController_ClearPeopleCallsDeleteAll() {
        databaseController.clearPeople()

        Mockito.verify(peopleDao).deleteAll()
    }
}
