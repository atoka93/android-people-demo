package net.attilaszabo.peopledemo.ui.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.attilaszabo.peopledemo.domain.people.Person
import javax.inject.Inject

class PersonViewModel @Inject constructor() : ViewModel() {

    // Observables

    private val nameLiveData = MutableLiveData<String>()
    val name: LiveData<String>
        get() = nameLiveData

    private val ageLiveData = MutableLiveData<Int>()
    val age: LiveData<Int>
        get() = ageLiveData

    private val regionLiveData = MutableLiveData<String>()
    val region: LiveData<String>
        get() = regionLiveData

    private val imageUrlLiveData = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = imageUrlLiveData

    // Public Api

    fun setPerson(person: Person) {
        person.apply {
            nameLiveData.value = name
            ageLiveData.value = age
            regionLiveData.value = region
            imageUrlLiveData.value = photoUrl
        }
    }
}
