package net.attilaszabo.peopledemo.ui.people.adapter

import android.view.View
import net.attilaszabo.peopledemo.R

class PeopleLoadingViewHolder(itemView: View) : PeopleAdapter.PeopleViewHolder(itemView) {
    companion object {
        fun getLayoutId(): Int = R.layout.item_people_loading
    }
}
