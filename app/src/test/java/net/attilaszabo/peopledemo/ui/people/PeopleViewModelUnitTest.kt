package net.attilaszabo.peopledemo.ui.people

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.TestUtils.generatePerson
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.mock
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.ERROR
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LIST
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING
import net.attilaszabo.peopledemo.ui.people.PeopleViewModel.Companion.ViewState.LOADING_MORE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.Random

@RunWith(MockitoJUnitRunner::class)
class PeopleViewModelUnitTest {

    private val people = listOf(
        generatePerson(),
        generatePerson(),
        generatePerson(),
        generatePerson(),
        generatePerson()
    )

    private lateinit var peopleViewModel: PeopleViewModel
    private lateinit var loadPeopleUseCase: LoadPeopleUseCase

    @Before
    fun initVariables() {
        loadPeopleUseCase = mock()
        peopleViewModel = PeopleViewModel(loadPeopleUseCase)
    }

    @Test
    fun peopleViewModel_OnLoadingPeopleResultSuccess_PeopleAreReturned() {
        given(loadPeopleUseCase.execute(0, 5)).willReturn(Flowable.just(Result.Success(people)))

        val newPeople = peopleViewModel.onNewPeople().test()

        peopleViewModel.loadPeople(5)

        newPeople.assertValue(people)
    }

    @Test
    fun peopleViewModel_OnLoadingMorePeopleResultSuccess_PeopleAreReturned() {
        val newPeople = peopleViewModel.onNewPeople().test()

        given(loadPeopleUseCase.execute(0, 5)).willReturn(Flowable.just(Result.Success(people)))
        peopleViewModel.loadPeople(5)
        given(loadPeopleUseCase.execute(5, 10)).willReturn(Flowable.just(Result.Success(people.take(2))))
        peopleViewModel.loadMorePeople()

        newPeople.assertValues(people, people.take(2))
    }

    @Test
    fun peopleViewModel_OnRetry_LoadPeopleUseCaseIsCalledWithTheCorrectParameters() {
        given(loadPeopleUseCase.execute(0, 0)).willReturn(Flowable.never())
        peopleViewModel.retry()

        verify(loadPeopleUseCase).execute(0, 0)
    }

    @Test
    fun peopleViewModel_OnRetryLoadingMore_LoadPeopleUseCaseIsCalledWithTheCorrectParameters() {
        given(loadPeopleUseCase.execute(0, 0)).willReturn(Flowable.never())
        peopleViewModel.retryLoadingMore()

        verify(loadPeopleUseCase).execute(0, 0)
    }

    @Test
    fun peopleViewModel_OnLoadingPeopleResultSuccess_TheCorrectViewStatesAreReturned() {
        given(loadPeopleUseCase.execute(0, 5)).willReturn(Flowable.just(Result.Success(people)))

        val viewState = peopleViewModel.onViewStateChanged().test()

        peopleViewModel.loadPeople(5)

        viewState.assertValues(LOADING, LIST)
    }

    @Test
    fun peopleViewModel_OnLoadingMorePeopleResultSuccess_TheCorrectViewStatesAreReturned() {
        val viewState = peopleViewModel.onViewStateChanged().test()

        given(loadPeopleUseCase.execute(0, 5)).willReturn(Flowable.just(Result.Success(people)))
        peopleViewModel.loadPeople(5)
        given(loadPeopleUseCase.execute(5, 10)).willReturn(Flowable.just(Result.Success(people)))
        peopleViewModel.loadMorePeople()

        viewState.assertValues(LOADING, LIST, LOADING_MORE, LIST)
    }

    @Test
    fun peopleViewModel_OnLoadingPeopleWithNoInitialValue_DefaultValueIsUsed() {
        given(loadPeopleUseCase.execute(0, 10)).willReturn(Flowable.never())
        peopleViewModel.loadPeople(null)

        verify(loadPeopleUseCase).execute(0, 10)
    }

    @Test
    fun peopleViewModel_OnResultError_TheCorrectViewStatesAreReturned() {
        val amount = Random().nextInt()
        given(loadPeopleUseCase.execute(0, amount)).willReturn(Flowable.just(Result.Error(Throwable())))

        val viewState = peopleViewModel.onViewStateChanged().test()

        peopleViewModel.loadPeople(amount)

        viewState.assertValues(LOADING, ERROR)
    }
}
