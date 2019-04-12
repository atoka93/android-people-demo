package net.attilaszabo.peopledemo.ui.people.adapter

import androidx.databinding.ViewDataBinding
import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleAdapterInteractions {

    fun onPersonClicked(viewDataBinding: ViewDataBinding, person: Person)

    fun loadMorePeople()

    fun retryLoadingMorePeople()
}
