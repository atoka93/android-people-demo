package net.attilaszabo.peopledemo.ui.people.adapter

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_person.view.itemPersonAgeTextView
import kotlinx.android.synthetic.main.item_person.view.itemPersonConstraintLayout
import kotlinx.android.synthetic.main.item_person.view.itemPersonImageView
import kotlinx.android.synthetic.main.item_person.view.itemPersonNameTextView
import kotlinx.android.synthetic.main.item_person.view.itemPersonProgressBar
import net.attilaszabo.peopledemo.R
import net.attilaszabo.peopledemo.domain.people.Person
import net.attilaszabo.peopledemo.ui.utils.GlideRequestFinishedListener

class PersonViewHolder(
    itemView: View,
    private val adapterInteractions: IPeopleAdapterInteractions
) : PeopleAdapter.PeopleViewHolder(itemView) {

    val itemPersonConstraintLayout: ConstraintLayout by lazy { itemView.itemPersonConstraintLayout }
    val itemPersonProgressBar: ProgressBar by lazy { itemView.itemPersonProgressBar }
    val itemPersonImageView: ImageView by lazy { itemView.itemPersonImageView }
    val itemPersonNameTextView: TextView by lazy { itemView.itemPersonNameTextView }
    val itemPersonAgeTextView: TextView by lazy { itemView.itemPersonAgeTextView }

    fun bind(position: Int, person: Person) {
        itemPersonConstraintLayout.setOnClickListener {
            adapterInteractions.onPersonClicked(this, position, person)
        }

        itemPersonProgressBar.visibility = View.VISIBLE
        itemPersonNameTextView.text = person.name
        itemPersonAgeTextView.text = itemView.context.getString(R.string.age, person.age)
        Glide.with(itemView.context)
            .load(person.photo)
            .listener(GlideRequestFinishedListener {
                itemPersonProgressBar.visibility = View.GONE
            })
            .into(itemPersonImageView)

        itemPersonNameTextView.transitionName = "$position${person.name}"
        itemPersonAgeTextView.transitionName = "$position${person.age}"
        itemPersonImageView.transitionName = "$position${person.photo}"
    }

    companion object {
        fun getLayoutId(): Int = R.layout.item_person
    }
}
