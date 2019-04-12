package net.attilaszabo.peopledemo.ui.people.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.BR
import net.attilaszabo.peopledemo.ui.R
import net.attilaszabo.peopledemo.ui.databinding.ItemPersonBinding
import net.attilaszabo.peopledemo.ui.people.adapter.PeopleAdapter.Companion.PeopleModel
import net.attilaszabo.peopledemo.ui.utils.GlideRequestFinishedListener
import net.attilaszabo.peopledemo.ui.utils.databinding.DataBindingViewHolder
import javax.inject.Inject

class PeopleAdapter @Inject constructor(
    private val adapterInteractions: IPeopleAdapterInteractions
) : RecyclerView.Adapter<DataBindingViewHolder<PeopleModel>>() {

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
                people.add(Companion.PeopleModel.Loading)
                notifyItemInserted(lastPosition)
            }
            is PeopleModel.Error -> {
                people[lastPosition] = Companion.PeopleModel.Loading
                notifyItemChanged(lastPosition)
            }
        }
    }

    fun addPeople(newPeople: List<Person>) {
        if (people.isNotEmpty()) {
            if (people.last() is PeopleModel.Loading || people.last() is PeopleModel.Error) {
                val lastPosition = people.size - 1
                people.removeAt(lastPosition)
                notifyItemRemoved(lastPosition)
            }
        }
        newPeople.forEach {
            people.add(Companion.PeopleModel.Item(it))
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
                people.add(Companion.PeopleModel.Error)
                notifyItemInserted(lastPosition)
            }
            is PeopleModel.Loading -> {
                people[lastPosition] = Companion.PeopleModel.Error
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<PeopleModel> {
        val viewHolderDataBinding = inflateViewHolder(
            parent, when (viewType) {
                ITEM_TYPE_LOADING -> R.layout.item_people_loading
                ITEM_TYPE_PERSON -> R.layout.item_person
                else -> R.layout.item_people_error
            }
        )
        viewHolderDataBinding.setVariable(BR.adapterInteractions, adapterInteractions)
        return DataBindingViewHolder(viewHolderDataBinding)
    }

    override fun onBindViewHolder(viewHolder: DataBindingViewHolder<PeopleModel>, position: Int) {
        if (position == people.size - 4) {
            adapterInteractions.loadMorePeople()
        }
        (viewHolder.binding as? ItemPersonBinding)?.apply {
            itemPersonProgressBar.visibility = View.VISIBLE
            setVariable(
                BR.loadedListener,
                GlideRequestFinishedListener {
                    itemPersonProgressBar.visibility = View.GONE
                }
            )
        }
        viewHolder.bind(position, people[position])
    }

    // Private Api

    private fun inflateViewHolder(parent: ViewGroup, layoutId: Int) =
        DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutId, parent, false)

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
