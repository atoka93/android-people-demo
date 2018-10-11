package net.attilaszabo.peopledemo.ui.people

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class PeopleViewModel @Inject constructor(
    private val loadPeopleUseCase: LoadPeopleUseCase
) : ViewModel() {

    // Members

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val peopleRelay = PublishRelay.create<List<Person>>()
    private val visibleViewStateRelay = PublishRelay.create<ViewState>()
    private var loadPeopleStartingAtPosition: Int = 0
    private var numberOfPeopleLoading: Int = 0

    // Observables

    fun onNewPeople(): Observable<List<Person>> = peopleRelay.distinctUntilChanged()
    fun onViewStateChanged(): Observable<ViewState> = visibleViewStateRelay

    // Public Api

    fun loadPeople(amount: Int?) {
        loadPeopleStartingAtPosition = 0
        numberOfPeopleLoading = amount ?: GROUP_OF_PEOPLE
        loadPeople()
    }

    fun retry() {
        loadPeople()
    }

    fun loadMorePeople() {
        if (numberOfPeopleLoading == 0) {
            numberOfPeopleLoading = GROUP_OF_PEOPLE
            loadPeople()
        }
    }

    fun retryLoadingMore() {
        loadPeople()
    }

    // Private Api

    private fun loadPeople() {
        visibleViewStateRelay.accept(
            if (loadPeopleStartingAtPosition == 0) {
                ViewState.LOADING
            } else {
                ViewState.LOADING_MORE
            }
        )
        compositeDisposable.add(
            loadPeopleUseCase.execute(position = loadPeopleStartingAtPosition, amount = numberOfPeopleLoading)
                .doOnEach { resultNotification ->
                    if (!resultNotification.isOnComplete) {
                        visibleViewStateRelay.accept(
                            if (resultNotification.isOnError || resultNotification.value is Result.Error) {
                                if (loadPeopleStartingAtPosition == 0) {
                                    ViewState.ERROR
                                } else {
                                    ViewState.INLINE_ERROR
                                }
                            } else {
                                ViewState.LIST
                            }
                        )
                    }
                }
                .filter { it is Result.Success }
                .map { (it as Result.Success).result }
                .subscribe { people ->
                    loadPeopleStartingAtPosition += people.size
                    numberOfPeopleLoading = 0
                    peopleRelay.accept(people)
                }
        )
    }

    // ViewModel

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {
        enum class ViewState { LOADING, LIST, LOADING_MORE, ERROR, INLINE_ERROR }

        private const val GROUP_OF_PEOPLE = 10
    }
}
