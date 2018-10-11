package net.attilaszabo.peopledemo.ui.people.adapter

import net.attilaszabo.peopledemo.domain.people.Person

interface IPeopleAdapterInteractions {

    fun onPersonClicked(viewHolder: PersonViewHolder, position: Int, person: Person)

    fun loadMorePeople()

    fun retryLoadingMorePeople()
}
