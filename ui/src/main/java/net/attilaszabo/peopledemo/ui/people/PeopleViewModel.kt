package net.attilaszabo.peopledemo.ui.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.attilaszabo.peopledemo.domain.Result
import net.attilaszabo.peopledemo.domain.people.LoadPeopleUseCase
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.utils.CoroutinesDispatcherProvider
import javax.inject.Inject

class PeopleViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val loadPeopleUseCase: LoadPeopleUseCase
) : ViewModel() {

    // Members

    private var loadPeopleStartingAtPosition: Int = 0
    private var numberOfPeopleLoading: Int = 0

    // Observables

    private val peopleLiveData = MutableLiveData<List<Person>>()
    val people: LiveData<List<Person>>
        get() = peopleLiveData

    private val viewStateLiveData = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = viewStateLiveData

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
        viewModelScope.launch(dispatcherProvider.computation) {
            launch(dispatcherProvider.main) {
                viewStateLiveData.value =
                    if (loadPeopleStartingAtPosition == 0) {
                        Companion.ViewState.LOADING
                    } else {
                        Companion.ViewState.LOADING_MORE
                    }
            }

            val result = loadPeopleUseCase(
                position = loadPeopleStartingAtPosition,
                amount = numberOfPeopleLoading
            )

            launch(dispatcherProvider.main) {
                viewStateLiveData.value =
                    when (result) {
                        is Result.Success -> {
                            Companion.ViewState.LIST
                        }
                        is Result.Error -> {
                            if (loadPeopleStartingAtPosition == 0) {
                                Companion.ViewState.ERROR
                            } else {
                                Companion.ViewState.INLINE_ERROR
                            }
                        }
                    }

                (result as? Result.Success)?.result?.let { people ->
                    loadPeopleStartingAtPosition += people.size
                    numberOfPeopleLoading = 0
                    peopleLiveData.value = people
                }
            }
        }
    }

    companion object {
        enum class ViewState { LOADING, LIST, LOADING_MORE, ERROR, INLINE_ERROR }

        private const val GROUP_OF_PEOPLE = 10
    }
}
