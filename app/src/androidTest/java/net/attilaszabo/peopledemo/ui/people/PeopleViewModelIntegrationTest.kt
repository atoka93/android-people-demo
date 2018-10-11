package net.attilaszabo.peopledemo.ui.people

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import net.attilaszabo.peopledemo.TestUtils.generateDatabasePerson
import net.attilaszabo.peopledemo.TestUtils.generateNetworkPerson
import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import net.attilaszabo.peopledemo.data.people.PeopleRepository
import net.attilaszabo.peopledemo.data.sources.local.database.CacheDatabase
import net.attilaszabo.peopledemo.data.sources.local.database.CacheDatabaseController
import net.attilaszabo.peopledemo.data.sources.local.database.models.PersonDatabase
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiController
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiService
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.domain.people.Person
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class PeopleViewModelIntegrationTest {

    private val testScheduler = Schedulers.trampoline()
    private lateinit var peopleViewModel: PeopleViewModel
    private lateinit var loadPeopleUseCase: LoadPeopleUseCase
    private lateinit var peopleRepository: PeopleRepository
    private lateinit var peopleDatabaseSource: IPeopleDatabaseSource
    private lateinit var peopleNetworkSource: IPeopleNetworkSource
    private lateinit var cacheDatabase: CacheDatabase
    private lateinit var uiNamesApiService: UINamesApiService

    @Before
    fun initVariables() {
        uiNamesApiService = mock(UINamesApiService::class.java)
        cacheDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getTargetContext(),
            CacheDatabase::class.java
        ).allowMainThreadQueries().build()
        peopleNetworkSource = UINamesApiController(uiNamesApiService)
        peopleDatabaseSource = CacheDatabaseController(cacheDatabase)
        peopleRepository = PeopleRepository(peopleDatabaseSource, peopleNetworkSource)
        loadPeopleUseCase = LoadPeopleUseCase(peopleRepository, testScheduler)
        peopleViewModel = PeopleViewModel(loadPeopleUseCase)
    }

    @After
    fun closeDatabase() {
        cacheDatabase.close()
    }

    @Test
    fun peopleViewModel_LoadPeople_FromNetwork() {
        val networkPeople = listOf(
            generateNetworkPerson(),
            generateNetworkPerson()
        )

        given(uiNamesApiService.loadPeople(2)).willReturn(Flowable.just(networkPeople))

        val newPeople = peopleViewModel.onNewPeople().test()
        val viewStates = peopleViewModel.onViewStateChanged().test()

        peopleViewModel.loadPeople(2)

        newPeople.assertValue(
            listOf(
                Person(
                    name = networkPeople[0].name + " " + networkPeople[0].surname,
                    region = networkPeople[0].region,
                    age = networkPeople[0].age,
                    photo = networkPeople[0].photo
                ),
                Person(
                    name = networkPeople[1].name + " " + networkPeople[1].surname,
                    region = networkPeople[1].region,
                    age = networkPeople[1].age,
                    photo = networkPeople[1].photo
                )
            )
        )
        viewStates.assertValues(
            PeopleViewModel.Companion.ViewState.LOADING,
            PeopleViewModel.Companion.ViewState.LIST
        )
        assertEquals(
            cacheDatabase.peopleDao().getPeople(0, 2), listOf(
                PersonDatabase(
                    name = networkPeople[0].name + " " + networkPeople[0].surname,
                    region = networkPeople[0].region,
                    age = networkPeople[0].age,
                    photo = networkPeople[0].photo
                ),
                PersonDatabase(
                    name = networkPeople[1].name + " " + networkPeople[1].surname,
                    region = networkPeople[1].region,
                    age = networkPeople[1].age,
                    photo = networkPeople[1].photo
                )
            )
        )
    }

    @Test
    fun peopleViewModel_LoadPeople_FromCache() {
        val databasePeople = listOf(
            generateDatabasePerson(),
            generateDatabasePerson()
        )
        cacheDatabase.peopleDao().insertPeople(databasePeople)

        val newPeople = peopleViewModel.onNewPeople().test()
        val viewStates = peopleViewModel.onViewStateChanged().test()

        peopleViewModel.loadPeople(2)

        newPeople.assertValue(
            listOf(
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
        )
        viewStates.assertValues(
            PeopleViewModel.Companion.ViewState.LOADING,
            PeopleViewModel.Companion.ViewState.LIST
        )
        verify(uiNamesApiService, never()).loadPeople(ArgumentMatchers.anyInt())
    }

    @Test
    fun peopleViewModel_OnError_EmitError() {
        given(uiNamesApiService.loadPeople(ArgumentMatchers.anyInt())).willReturn(Flowable.error(Throwable()))

        val newPeople = peopleViewModel.onNewPeople().test()
        val viewStates = peopleViewModel.onViewStateChanged().test()

        peopleViewModel.loadPeople(3)

        newPeople.assertNoValues()
        viewStates.assertValues(
            PeopleViewModel.Companion.ViewState.LOADING,
            PeopleViewModel.Companion.ViewState.ERROR
        )
    }
}
