package net.attilaszabo.peopledemo.ui.people.adapter

import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.item_people_error.view.itemPeopleErrorRetryButton
import net.attilaszabo.peopledemo.R

class PeopleErrorViewHolder(
    itemView: View,
    private val retryLoadingMorePeople: () -> Unit
) : PeopleAdapter.PeopleViewHolder(itemView) {

    private val itemPeopleErrorRetryButton: Button by lazy { itemView.itemPeopleErrorRetryButton }

    fun bind() {
        itemPeopleErrorRetryButton.setOnClickListener {
            retryLoadingMorePeople()
        }
    }

    companion object {
        fun getLayoutId(): Int = R.layout.item_people_error
    }
}
