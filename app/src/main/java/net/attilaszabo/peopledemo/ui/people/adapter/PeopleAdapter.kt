package net.attilaszabo.peopledemo.ui.people.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.people.adapter.PeopleAdapter.PeopleViewHolder
import javax.inject.Inject

class PeopleAdapter @Inject constructor(
    private val adapterInteractions: IPeopleAdapterInteractions
) : RecyclerView.Adapter<PeopleViewHolder>() {

    // Members

    private val people: MutableList<PeopleModel> = mutableListOf()

    // Public Api

    fun showLoading() {
        if (people.isEmpty()) {
            return
        }
        val lastPosition = people.size - 1
        when (people.last()) {
            is PeopleModel.Item -> {
                people.add(PeopleModel.Loading)
                notifyItemInserted(lastPosition)
            }
            is PeopleModel.Error -> {
                people[lastPosition] = PeopleModel.Loading
                notifyItemChanged(lastPosition)
            }
        }
    }

    fun addPeople(newPeople: List<Person>) {
        if (!people.isEmpty()) {
            if (people.last() is PeopleModel.Loading || people.last() is PeopleModel.Error) {
                val lastPosition = people.size - 1
                people.removeAt(lastPosition)
                notifyItemRemoved(lastPosition)
            }
        }
        newPeople.forEach {
            people.add(PeopleModel.Item(it))
        }
        notifyItemRangeInserted(people.size - newPeople.size, newPeople.size)
    }

    fun showError() {
        if (people.isEmpty()) {
            return
        }
        val lastPosition = people.size - 1
        when (people.last()) {
            is PeopleModel.Item -> {
                people.add(PeopleModel.Error)
                notifyItemInserted(lastPosition)
            }
            is PeopleModel.Loading -> {
                people[lastPosition] = PeopleModel.Error
                notifyItemChanged(lastPosition)
            }
        }
    }

    fun getSpanSize(position: Int, spanCount: Int) =
        when (people[position]) {
            is PeopleModel.Loading, is PeopleModel.Error -> spanCount
            else -> 1
        }

    // RecyclerView.Adapter

    override fun getItemCount() = people.size

    override fun getItemViewType(position: Int): Int =
        when (people[position]) {
            is PeopleModel.Loading -> ITEM_TYPE_LOADING
            is PeopleModel.Item -> ITEM_TYPE_PERSON
            else -> ITEM_TYPE_ERROR
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder =
        when (viewType) {
            ITEM_TYPE_LOADING -> {
                PeopleLoadingViewHolder(
                    itemView = inflateViewHolder(parent, PeopleLoadingViewHolder.getLayoutId())
                )
            }
            ITEM_TYPE_PERSON -> {
                PersonViewHolder(
                    itemView = inflateViewHolder(parent, PersonViewHolder.getLayoutId()),
                    adapterInteractions = adapterInteractions
                )
            }
            else -> {
                PeopleErrorViewHolder(
                    itemView = inflateViewHolder(parent, PeopleErrorViewHolder.getLayoutId()),
                    retryLoadingMorePeople = adapterInteractions::retryLoadingMorePeople
                )
            }
        }

    override fun onBindViewHolder(viewHolder: PeopleViewHolder, position: Int) {
        if (position == people.size - 4) {
            adapterInteractions.loadMorePeople()
        }
        when (viewHolder) {
            is PersonViewHolder -> viewHolder.bind(
                position = position,
                person = (people[position] as PeopleModel.Item).person
            )
            is PeopleErrorViewHolder -> viewHolder.bind()
        }
    }

    // Private Api

    private fun inflateViewHolder(parent: ViewGroup, layoutId: Int) =
        LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )

    abstract class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        sealed class PeopleModel {
            data class Item(val person: Person) : PeopleModel()
            object Error : PeopleModel()
            object Loading : PeopleModel()
        }

        private const val ITEM_TYPE_LOADING = 0
        private const val ITEM_TYPE_PERSON = 1
        private const val ITEM_TYPE_ERROR = 9
    }
}
