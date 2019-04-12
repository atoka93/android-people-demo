package net.attilaszabo.peopledemo.ui.people

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.room.Room
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.attilaszabo.peopledemo.data.people.IPeopleDatabaseSource
import net.attilaszabo.peopledemo.data.people.IPeopleNetworkSource
import net.attilaszabo.peopledemo.data.people.PeopleRepository
import net.attilaszabo.peopledemo.data.sources.cachedb.CacheDatabase
import net.attilaszabo.peopledemo.data.sources.cachedb.CacheDatabaseController
import net.attilaszabo.peopledemo.data.sources.cachedb.models.PersonDatabase
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiController
import net.attilaszabo.peopledemo.data.sources.uinames.UINamesApiService
import net.attilaszabo.peopledemo.domain.DomainTestUtils
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.mock
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class PeopleViewModelIntegrationTest {

    private lateinit var peopleViewModel: PeopleViewModel
    private lateinit var loadPeopleUseCase: LoadPeopleUseCase
    private lateinit var peopleRepository: PeopleRepository
    private lateinit var peopleDatabaseSource: IPeopleDatabaseSource
    private lateinit var peopleNetworkSource: IPeopleNetworkSource
    private lateinit var cacheDatabase: CacheDatabase
    private lateinit var uiNamesApiService: UINamesApiService
    private lateinit var mockLifecycle: Lifecycle

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun initVariables() {
        uiNamesApiService = mock(UINamesApiService::class.java)
        cacheDatabase =
            Room.inMemoryDatabaseBuilder(getApplicationContext(), CacheDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        peopleNetworkSource = UINamesApiController(uiNamesApiService)
        peopleDatabaseSource = CacheDatabaseController(cacheDatabase)
        peopleRepository = PeopleRepository(peopleDatabaseSource, peopleNetworkSource)
        loadPeopleUseCase = LoadPeopleUseCase(peopleRepository)
        peopleViewModel = PeopleViewModel(DomainTestUtils.getTestCoroutinesDispatcherProvider(), loadPeopleUseCase)
        mockLifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java)).apply {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    @After
    fun closeDatabase() {
        cacheDatabase.close()
    }

    @Test
    fun peopleViewModel_LoadPeople_FromNetwork() =
        runBlocking {
            val networkPeople = listOf(
                generateNetworkPerson(),
                generateNetworkPerson()
            )
            given(uiNamesApiService.loadPeople(2)).willReturn(async { networkPeople })

            val observer: (List<Person>) -> Unit = mock()
            peopleViewModel.people.observe({ mockLifecycle }, observer)

            peopleViewModel.loadPeople(2)

            verify(observer).invoke(
                listOf(
                    Person(
                        name = networkPeople[0].name + " " + networkPeople[0].surname,
                        region = networkPeople[0].region,
                        age = networkPeople[0].age,
                        photoUrl = networkPeople[0].photoUrl
                    ),
                    Person(
                        name = networkPeople[1].name + " " + networkPeople[1].surname,
                        region = networkPeople[1].region,
                        age = networkPeople[1].age,
                        photoUrl = networkPeople[1].photoUrl
                    )
                )
            )
//        assertEquals(
//            peopleViewModel.viewState.value,
//            listOf(
//                LOADING,
//                LIST
//            )
//        )
            assertEquals(
                cacheDatabase.peopleDao().getPeople(0, 2), listOf(
                    PersonDatabase(
                        name = networkPeople[0].name + " " + networkPeople[0].surname,
                        region = networkPeople[0].region,
                        age = networkPeople[0].age,
                        photoUrl = networkPeople[0].photoUrl
                    ),
                    PersonDatabase(
                        name = networkPeople[1].name + " " + networkPeople[1].surname,
                        region = networkPeople[1].region,
                        age = networkPeople[1].age,
                        photoUrl = networkPeople[1].photoUrl
                    )
                )
            )
        }

//    @Test
//    fun peopleViewModel_LoadPeople_FromCache() {
//        val databasePeople = listOf(
//            generateDatabasePerson(),
//            generateDatabasePerson()
//        )
//        cacheDatabase.peopleDao().insertPeople(databasePeople)
//
//        val newPeople = peopleViewModel.onNewPeople().test()
//        val viewStates = peopleViewModel.onViewStateChanged().test()
//
//        peopleViewModel.loadPeople(2)
//
//        newPeople.assertValue(
//            listOf(
//                Person(
//                    name = databasePeople[0].name,
//                    region = databasePeople[0].region,
//                    age = databasePeople[0].age,
//                    photoUrl = databasePeople[0].photoUrl
//                ),
//                Person(
//                    name = databasePeople[1].name,
//                    region = databasePeople[1].region,
//                    age = databasePeople[1].age,
//                    photoUrl = databasePeople[1].photoUrl
//                )
//            )
//        )
//        viewStates.assertValues(
//            LOADING,
//            LIST
//        )
//        verify(uiNamesApiService, never()).loadPeople(ArgumentMatchers.anyInt())
//    }
//
//    @Test
//    fun peopleViewModel_OnError_EmitError() {
//        given(uiNamesApiService.loadPeople(ArgumentMatchers.anyInt())).willReturn(Throwable())
//
//        val newPeople = peopleViewModel.onNewPeople().test()
//        val viewStates = peopleViewModel.onViewStateChanged().test()
//
//        peopleViewModel.loadPeople(3)
//
//        newPeople.assertNoValues()
//        viewStates.assertValues(
//            LOADING,
//            ERROR
//        )
//    }
}
