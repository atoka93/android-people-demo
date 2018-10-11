package net.attilaszabo.peopledemo.data.sources.uinames

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.TestUtils.generateNetworkPerson
import net.attilaszabo.peopledemo.data.sources.uinames.models.PersonNetwork
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UINamesApiControllerUnitTest {

    private val networkPeople = listOf(
        generateNetworkPerson(),
        generateNetworkPerson()
    )

    private lateinit var apiController: UINamesApiController
    private lateinit var apiService: UINamesApiService

    @Before
    fun initVariables() {
        apiService = mock()
        apiController = UINamesApiController(apiService)
    }

    @Test
    fun uiNamesApiController_OnSmallerThanOrEqualToZero_ReturnsEmptyList() {
        val result = apiController.getExtendedPeople(0).blockingFirst()

        assertEquals(result, listOf<PersonNetwork>())
    }

    @Test
    fun uiNamesApiController_ForNRequested_ReturnsNPeople() {
        given(apiService.loadPeople(2)).willReturn(Flowable.just(networkPeople))

        val result = apiController.getExtendedPeople(2).blockingFirst()

        assertEquals(
            result, listOf(
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
    }
}
